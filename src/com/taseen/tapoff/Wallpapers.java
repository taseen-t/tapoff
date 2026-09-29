package com.taseen.tapoff;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

// Where wallpapers come from (Pixel's built-in pack, Bing, Wallhaven), the on-phone cache, and applying one.
// Everything here blocks: call it off the main thread.
final class Wallpapers {
    static final String PIXEL_PACK = "com.google.android.apps.wallpaper.pixel";
    // Online lists are refetched after this; offline, the last list is used.
    static final long LIST_TTL_MS = 12 * 60 * 60 * 1000L;
    // ponytail: oldest downloads are deleted past this size; no smarter eviction needed yet.
    static final long CACHE_CAP_BYTES = 400L * 1024 * 1024;
    // Wallhaven sets shown as cards: {title, search, cover id, blocked ids}. "+" must match, "-" excludes; the
    // exclusions keep people and AI portraits out. The cover is a hand-picked result shown first. Blocked ids are
    // results that got through but don't belong (game art, placeholders, duplicates). All checked by eye against
    // contact sheets on 2026-09-29; re-check when changing a search.
    private static final String PEOPLE = " -women -girl -anime -men -model -portrait -face";
    static final String[][] THEMES = {
        {"Abstract", "+abstract" + PEOPLE, "z8gz7w", "9me7j8 eogzx8"},
        {"Minimal", "+minimalism" + PEOPLE, "lqjqdp", "572128 2ek616 gp5ywd w8232p jx57qm"},
        {"Dreamscape", "+digital art +landscape" + PEOPLE, "jxkr9q", "exl7kr ex7o7w"},
        {"Space", "+nebula" + PEOPLE, "d65kwm", ""},
    };

    static final class Item {
        final String title, full, cover, credit;
        final int res; // resource id in the Pixel pack, else 0

        Item(String title, String full, String cover, String credit, int res) {
            this.title = title;
            this.full = full;
            this.cover = cover;
            this.credit = credit;
            this.res = res;
        }
    }

    static final class Collection {
        final String name, source;
        final List<Item> items;

        Collection(String name, String source, List<Item> items) {
            this.name = name;
            this.source = source;
            this.items = items;
        }
    }

    // Calls back once per collection as each one is ready, Pixel first.
    static void load(Context c, Consumer<Collection> out) {
        trimCache(c);
        Collection pixel = pixel(c);
        if (pixel != null) out.accept(pixel);
        out.accept(CutoutArt.collection());
        try {
            out.accept(bing(c));
        } catch (IOException | JSONException e) {
            Log.w("TapOff", "Bing unavailable", e);
        }
        for (String[] t : THEMES) {
            try {
                Collection col = wallhaven(c, t[0], t[1], t[2], t[3]);
                if (!col.items.isEmpty()) out.accept(col);
            } catch (IOException | JSONException e) {
                Log.w("TapOff", "Wallhaven " + t[0] + " unavailable", e);
            }
        }
    }

    private static Collection pixel(Context c) {
        List<Item> items = new ArrayList<>();
        try {
            Resources res = pixelRes(c);
            try (XmlResourceParser x = res.getXml(res.getIdentifier("wallpapers", "xml", PIXEL_PACK))) {
                for (int t = x.getEventType(); t != XmlPullParser.END_DOCUMENT; t = x.next()) {
                    if (t != XmlPullParser.START_TAG || !"static-wallpaper".equals(x.getName())) continue;
                    int title = x.getAttributeResourceValue(null, "title", 0);
                    items.add(new Item(title != 0 ? res.getString(title) : "Pixel", null, null,
                        "", x.getAttributeResourceValue(null, "src", 0)));
                }
            }
        } catch (Exception e) { // no Pixel pack on this phone, or its format changed
            return null;
        }
        return items.isEmpty() ? null : new Collection("Pixel", "Pixel", items);
    }

    private static Collection bing(Context c) throws IOException, JSONException {
        JSONArray a = new JSONObject(list(c, "bing",
            "https://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=8&mkt=en-US")).getJSONArray("images");
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < a.length(); i++) {
            JSONObject o = a.getJSONObject(i);
            String base = "https://www.bing.com" + o.getString("urlbase");
            items.add(new Item(o.optString("title", "Bing"), base + "_1080x1920.jpg", base + "_1366x768.jpg",
                o.optString("copyright"), 0));
        }
        return new Collection("Today", "Bing", items);
    }

    private static Collection wallhaven(Context c, String name, String query, String cover, String blocked)
            throws IOException, JSONException {
        // Keyed by the search itself, so changing a search never reuses an old list.
        JSONArray a = new JSONObject(list(c, "wallhaven-" + sha1(query),
            "https://wallhaven.cc/api/v1/search?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8)
                + "&categories=100&purity=100&ratios=portrait&atleast=1080x1920&sorting=favorites"))
            .getJSONArray("data");
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < a.length(); i++) {
            JSONObject o = a.getJSONObject(i);
            String id = o.getString("id");
            if (blocked.contains(id)) continue;
            Item it = new Item(name, o.getString("path"), null, "", 0);
            if (id.equals(cover)) items.add(0, it);
            else items.add(it);
        }
        return new Collection(name, "Wallhaven", items);
    }

    // A cached API response, refreshed after LIST_TTL_MS.
    private static String list(Context c, String key, String url) throws IOException {
        File f = new File(c.getCacheDir(), "lists/" + key + ".json");
        if (System.currentTimeMillis() - f.lastModified() > LIST_TTL_MS) {
            try {
                download(url, f);
            } catch (IOException e) {
                if (!f.exists()) throw e; // offline: keep using the old list
            }
        }
        return new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
    }

    // Downloaded once, then read from the phone.
    static File cached(Context c, String url) throws IOException {
        File f = new File(c.getCacheDir(), "img/" + sha1(url));
        if (f.exists()) f.setLastModified(System.currentTimeMillis()); // recently used survives trimming
        else download(url, f);
        return f;
    }

    // Wallhaven's image server answers bursts with 429 Too Many Requests, so downloads go two at a time
    // and a throttled one waits and tries again.
    private static final Semaphore DOWNLOADS = new Semaphore(2);
    private static final int TRIES = 4;

    private static void download(String url, File to) throws IOException {
        DOWNLOADS.acquireUninterruptibly();
        try {
            for (int attempt = 1; ; attempt++) {
                HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
                con.setRequestProperty("User-Agent", "TapOff/1.0 (Android)");
                con.setConnectTimeout(15000);
                con.setReadTimeout(30000);
                try {
                    int code = con.getResponseCode();
                    if (code == 429 && attempt < TRIES) {
                        pause(con.getHeaderFieldInt("Retry-After", attempt * 2));
                        continue;
                    }
                    if (code != 200) throw new IOException("HTTP " + code + " for " + url);
                    to.getParentFile().mkdirs();
                    // Its own temp file: the same image can be fetched twice at once (a swipe racing the prefetch).
                    File tmp = File.createTempFile(to.getName(), ".part", to.getParentFile());
                    try (InputStream in = con.getInputStream()) {
                        Files.copy(in, tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        // Rename so a half-finished download never looks cached.
                        Files.move(tmp.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } finally {
                        tmp.delete(); // leftover only if the download failed
                    }
                    return;
                } finally {
                    con.disconnect();
                }
            }
        } finally {
            DOWNLOADS.release();
        }
    }

    private static void pause(int seconds) throws IOException {
        try {
            Thread.sleep(Math.min(seconds, 10) * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("interrupted", e);
        }
    }

    private static void trimCache(Context c) {
        File[] files = new File(c.getCacheDir(), "img").listFiles();
        if (files == null) return;
        long total = 0;
        for (File f : files) total += f.length();
        if (total <= CACHE_CAP_BYTES) return;
        Arrays.sort(files, Comparator.comparingLong(File::lastModified));
        for (File f : files) {
            if (total <= CACHE_CAP_BYTES * 8 / 10) break;
            total -= f.length();
            f.delete();
        }
    }

    private static String sha1(String s) {
        try {
            StringBuilder sb = new StringBuilder();
            for (byte b : MessageDigest.getInstance("SHA-1").digest(s.getBytes(StandardCharsets.UTF_8)))
                sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Resources pixelRes;

    private static synchronized Resources pixelRes(Context c) throws Exception {
        if (pixelRes == null) pixelRes = c.getPackageManager().getResourcesForApplication(PIXEL_PACK);
        return pixelRes;
    }

    // Decodes at the smallest size that still covers minW x minH, so huge images stay light.
    static Bitmap decode(Context c, Item it, boolean cover, int minW, int minH) throws Exception {
        if (it.full != null && it.full.startsWith(CutoutArt.SCHEME)) return CutoutArt.render(c, it, cover, minW, minH);
        ImageDecoder.Source src;
        if (it.res != 0) src = ImageDecoder.createSource(pixelRes(c), it.res);
        else if (it.full.startsWith("content:")) src = ImageDecoder.createSource(c.getContentResolver(), Uri.parse(it.full));
        else src = ImageDecoder.createSource(cached(c, cover && it.cover != null ? it.cover : it.full));
        return ImageDecoder.decodeBitmap(src, (d, info, s) -> {
            d.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE);
            int w = info.getSize().getWidth(), h = info.getSize().getHeight();
            d.setTargetSampleSize(Math.max(1, Math.min(w / minW, h / minH)));
        });
    }

    // Home screen: TapOff's live wallpaper draws this file (it has to be live for double-tap).
    // Lock screen: set as an ordinary still wallpaper.
    static void apply(Context c, Bitmap bmp) throws IOException {
        File out = TapWallpaper.photo(c), tmp = new File(c.getFilesDir(), "wallpaper.tmp");
        try (FileOutputStream os = new FileOutputStream(tmp)) {
            if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, os)) throw new IOException("couldn't save image");
        }
        // Rename so the wallpaper never reads a half-written file.
        Files.move(tmp.toPath(), out.toPath(), StandardCopyOption.REPLACE_EXISTING);
        WallpaperManager.getInstance(c).setBitmap(bmp, null, true, WallpaperManager.FLAG_LOCK);
    }

    static boolean isActive(Context c) {
        android.app.WallpaperInfo info = WallpaperManager.getInstance(c).getWallpaperInfo();
        return info != null && info.getPackageName().equals(c.getPackageName());
    }
}
