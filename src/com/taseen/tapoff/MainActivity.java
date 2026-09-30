package com.taseen.tapoff;

import android.app.Activity;
import android.app.StatusBarManager;
import android.app.UiModeManager;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Insets;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.VelocityTracker;
import android.view.MotionEvent;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private static final int PICK = 1;
    private static final String BUG_REPORTS = "https://x.com/taseen_tariq_";
    private static final int CARD_GAP_DP = 14;
    private static final long THEME_FADE_MS = 420, TAB_SLIDE_MS = 300;
    // Deck tuning: how far each older card peeks out above the next, and how many stay visible.
    private static final int PEEK_DP = 10, DECK_DEPTH = 3;
    // Appearance choices, stored as the index; following the system is the default.
    private static final String[] APPEARANCE = {"System", "Light", "Dark"};
    private static final int[] NIGHT_MODE = {UiModeManager.MODE_NIGHT_AUTO, UiModeManager.MODE_NIGHT_NO, UiModeManager.MODE_NIGHT_YES};

    private static final String GRANT = "adb shell pm grant com.taseen.tapoff android.permission.WRITE_SECURE_SETTINGS",
        SETUP_GUIDE = "https://tapoff.vercel.app/#setup",
        RELEASES_API = "https://api.github.com/repos/taseen-t/tapoff/releases/latest",
        RELEASES = "https://github.com/taseen-t/tapoff/releases/latest",
        QUICK_TAP_SETTINGS = "com.google.android.settings.gestures.QUICK_TAP_SETTINGS";

    private final ExecutorService bg = Executors.newFixedThreadPool(3);
    // ponytail: every card stays alive (no recycling) so the deck can show; fine for the ~8 collections.
    private final List<GlassCard> cards = new ArrayList<>();
    // Kept across a theme change, so switching light/dark repaints without reloading anything.
    private final List<Wallpapers.Collection> collections = new ArrayList<>();
    private final java.util.Map<String, Bitmap> covers = new java.util.HashMap<>();
    private Bitmap tileBitmap;
    private final TextView[] segments = new TextView[3];
    private ScrollView feed, settings;
    private LinearLayout feedList;
    private TextView lockStatus;
    private ImageView tileImage, volumeImage;
    private TextView volumeStatus;
    private Switch panelSwitch, cameraSwitch;
    private Button backTap;
    private boolean wantPanel; // turned on, and went to allow "Display over other apps"
    private Bitmap volumeBitmap;
    private Switch homeSwitch, lockSwitch;
    private View setupCard;
    private Button update;
    private View wallpapersHeader;
    private GlassCard favCard;
    private String latestVersion; // set once GitHub has a newer release than this one
    private Button setTapOff;
    private final TextView[] tabs = new TextView[2];
    private View indicator;
    private int tabW;
    private int tab = -1;

    static Intent chooser(Context c) {
        return new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).putExtra(
            WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(c, TapWallpaper.class));
    }

    @Override protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        buildUi(saved != null ? saved.getInt("tab", 0) : 0);
        bg.execute(() -> Wallpapers.load(this, col -> runOnUiThread(() -> {
            collections.add(col);
            addCard(col);
        })));
        checkForUpdate();
    }

    @Override protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putInt("tab", tab);
    }

    // Light/dark changed (the manifest routes uiMode here instead of restarting the screen): repaint in place,
    // on the same tab and scroll position, reusing the wallpapers already loaded.
    @Override public void onConfigurationChanged(android.content.res.Configuration c) {
        super.onConfigurationChanged(c);
        int keep = tab, feedY = feed.getScrollY(), settingsY = settings.getScrollY();
        // Snapshot the old look, repaint underneath it, then fade the snapshot away.
        FrameLayout content = findViewById(android.R.id.content);
        Bitmap before = null;
        if (content.getWidth() > 0 && content.getHeight() > 0) {
            before = Bitmap.createBitmap(content.getWidth(), content.getHeight(), Bitmap.Config.ARGB_8888);
            content.draw(new android.graphics.Canvas(before));
        }
        buildUi(keep);
        feed.post(() -> feed.scrollTo(0, feedY));
        settings.post(() -> settings.scrollTo(0, settingsY));
        refreshState();
        if (before != null) {
            ImageView fade = new ImageView(this);
            fade.setImageBitmap(before);
            content.addView(fade, new FrameLayout.LayoutParams(-1, -1));
            fade.animate().alpha(0f).setStartDelay(60).setDuration(THEME_FADE_MS).withEndAction(() -> content.removeView(fade));
        }
    }

    private void buildUi(int showTab) {
        cards.clear();
        favCard = null;
        tab = -1;
        FrameLayout root = new SwipeRoot(this);
        root.setBackgroundColor(Ui.bg(this));

        feed = new ScrollView(this);
        feed.setClipToPadding(false);
        feed.setVerticalScrollBarEnabled(false);
        feedList = new LinearLayout(this);
        feedList.setOrientation(LinearLayout.VERTICAL);
        feedList.addView(title("TapOff"));
        update = Ui.pill(this, "", Ui.Pill.PRIMARY);
        update.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(RELEASES))));
        LinearLayout.LayoutParams updateLp = new LinearLayout.LayoutParams(-1, -2);
        updateLp.setMargins(Ui.dp(this, 16), 0, Ui.dp(this, 16), Ui.dp(this, 12));
        feedList.addView(update, updateLp);
        showUpdate();
        feedList.addView(lockTile());
        feedList.addView(volumeTile());
        wallpapersHeader = section("Wallpapers");
        feedList.addView(wallpapersHeader);
        feed.addView(feedList);
        feed.setOnScrollChangeListener((v, x, y, ox, oy) -> stackCards());
        feedList.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) -> stackCards());
        root.addView(feed, new FrameLayout.LayoutParams(-1, -1));

        settings = buildSettings();
        root.addView(settings, new FrameLayout.LayoutParams(-1, -1));

        // Fades the page out under the status bar.
        View scrim = new View(this);
        scrim.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
            new int[] {Ui.bg(this), Ui.bg(this) & 0x00FFFFFF}));
        scrim.setOutlineProvider(null); // no shadow, just stacked above the sliding pages
        scrim.setElevation(Ui.dp(this, 14));
        root.addView(scrim, new FrameLayout.LayoutParams(-1, 0, Gravity.TOP));

        FrameLayout tabBar = buildTabBar();
        root.addView(tabBar, new FrameLayout.LayoutParams(-2, -2, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL));

        root.setOnApplyWindowInsetsListener((v, insets) -> {
            Insets bars = insets.getInsets(WindowInsets.Type.systemBars());
            int m = Ui.dp(this, 16), tabSpace = Ui.dp(this, 96);
            feed.setPadding(0, bars.top, 0, bars.bottom + tabSpace);
            settings.setPadding(m, bars.top, m, bars.bottom + tabSpace);
            ((FrameLayout.LayoutParams) tabBar.getLayoutParams()).bottomMargin = bars.bottom + m;
            scrim.getLayoutParams().height = bars.top + Ui.dp(this, 20);
            tabBar.requestLayout();
            return insets;
        });
        setContentView(root);
        // Dark status and navigation icons on the light theme, light ones on the dark theme.
        int lightBars = android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            | android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS;
        getWindow().getInsetsController().setSystemBarsAppearance(Ui.dark(this) ? 0 : lightBars, lightBars);
        for (Wallpapers.Collection col : collections) addCard(col);
        select(showTab);
    }

    @Override protected void onResume() {
        super.onResume();
        refreshState();
    }

    private void refreshState() {
        loadTileImage();
        refreshFavourites();
        if (wantPanel && NotchPanel.allowed(this)) NotchPanel.setEnabled(this, true);
        wantPanel = false;
        panelSwitch.setChecked(NotchPanel.enabled(this) && NotchPanel.allowed(this));
        cameraSwitch.setChecked(NotchPanel.cameraTap(this));
        backTap.setText(SlidersActivity.backTapSeen(this) ? "Back tap is set up" : "Set up back tap");
        NotchPanel.changed(this); // the permission may have changed while we were away
        homeSwitch.setChecked(TapWallpaper.enabled(this));
        lockSwitch.setChecked(LockService.lockScreenOn(this));
        refreshStatus();
        setupCard.setVisibility(LockService.canLock(this) ? View.GONE : View.VISIBLE);
        setTapOff.setVisibility(Wallpapers.isActive(this) ? View.GONE : View.VISIBLE);
    }

    private String versionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    // Asks GitHub for the newest release, through the wallpaper lists' cache (so at most twice a day, and quiet
    // offline). A newer one shows an Update button under the title.
    private void checkForUpdate() {
        bg.execute(() -> {
            try {
                String latest = new org.json.JSONObject(Wallpapers.list(this, "release", RELEASES_API))
                    .getString("tag_name").replaceFirst("^v", "");
                if (!newer(latest, versionName())) return;
                runOnUiThread(() -> {
                    latestVersion = latest;
                    showUpdate();
                });
            } catch (Exception e) {
                Log.w("TapOff", "update check failed", e);
            }
        });
    }

    private void showUpdate() {
        update.setVisibility(latestVersion == null ? View.GONE : View.VISIBLE);
        if (latestVersion != null) update.setText("Update to TapOff " + latestVersion);
    }

    // True if version a (like "1.8" or "1.7.1") is later than version b.
    static boolean newer(String a, String b) {
        String[] x = a.split("\\."), y = b.split("\\.");
        for (int i = 0; i < Math.max(x.length, y.length); i++) {
            int p = i < x.length ? number(x[i]) : 0, q = i < y.length ? number(y[i]) : 0;
            if (p != q) return p > q;
        }
        return false;
    }

    private static int number(String part) {
        String digits = part.replaceAll("\\D", "");
        return digits.isEmpty() ? 0 : Integer.parseInt(digits);
    }

    @Override protected void onDestroy() {
        bg.shutdownNow();
        super.onDestroy();
    }

    // Persisted by the system for this app, and the screen redraws in the new mode.
    private void setAppearance(int choice) {
        getSharedPreferences("tapoff", MODE_PRIVATE).edit().putInt("appearance", choice).apply();
        getSystemService(UiModeManager.class).setApplicationNightMode(NIGHT_MODE[choice]);
    }

    private TextView title(String s) {
        TextView t = Ui.text(this, s, 32, 700, Ui.ink(this));
        t.setPadding(Ui.dp(this, 20), Ui.dp(this, 20), Ui.dp(this, 20), Ui.dp(this, 16));
        return t;
    }

    private TextView section(String s) {
        TextView t = Ui.text(this, s, 20, 600, Ui.ink(this));
        t.setPadding(Ui.dp(this, 20), Ui.dp(this, 26), Ui.dp(this, 20), Ui.dp(this, 12));
        return t;
    }

    // The app's main job, set apart from the photo cards: your wallpaper behind a dark fade, white controls on
    // top, so it reads the same in light and dark mode.
    // A feature tile: TapOff's art behind a dark fade, a glass icon chip, a title and a status line, white on top
    // so it reads the same in light and dark mode. Returns the column the switches go in.
    private LinearLayout artTile(FrameLayout tile, ImageView art, String title, int iconRes, TextView status) {
        int onText = 0xFFFFFFFF;
        tile.setBackground(Ui.shape(this, 28, 0xFF1B1C20, 0));
        tile.setClipToOutline(true);
        art.setScaleType(ImageView.ScaleType.CENTER_CROP);
        tile.addView(art, new FrameLayout.LayoutParams(-1, -1));
        View fade = new View(this);
        fade.setBackground(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] {0x99000000, 0x00000000}));
        tile.addView(fade, new FrameLayout.LayoutParams(-1, -1));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        int pad = Ui.dp(this, 18);
        content.setPadding(pad, pad, pad, Ui.dp(this, 10));
        LinearLayout head = new LinearLayout(this);
        head.setGravity(Gravity.CENTER_VERTICAL);
        ImageView icon = new ImageView(this);
        icon.setImageResource(iconRes);
        icon.setImageTintList(ColorStateList.valueOf(onText));
        icon.setBackground(Ui.glass(this, 24, 0x33FFFFFF));
        int ip = Ui.dp(this, 10);
        icon.setPadding(ip, ip, ip, ip);
        head.addView(icon, new LinearLayout.LayoutParams(Ui.dp(this, 48), Ui.dp(this, 48)));
        LinearLayout words = new LinearLayout(this);
        words.setOrientation(LinearLayout.VERTICAL);
        words.setPadding(Ui.dp(this, 14), 0, 0, 0);
        words.addView(Ui.text(this, title, 18, 600, onText));
        words.addView(status);
        head.addView(words, new LinearLayout.LayoutParams(0, -2, 1));
        content.addView(head);
        tile.addView(content, new FrameLayout.LayoutParams(-1, -2));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        int m = Ui.dp(this, 16);
        lp.setMargins(m, 0, m, 0);
        tile.setLayoutParams(lp);
        return content;
    }

    // Fills whatever height the controls give the tile; never lets the picture decide it.
    private ImageView tileArtView() {
        return new ImageView(this) {
            @Override protected void onMeasure(int wSpec, int hSpec) {
                setMeasuredDimension(MeasureSpec.getSize(wSpec),
                    MeasureSpec.getMode(hSpec) == MeasureSpec.EXACTLY ? MeasureSpec.getSize(hSpec) : 0);
            }
        };
    }

    private FrameLayout lockTile() {
        int onText = 0xFFFFFFFF, soft = 0xCCFFFFFF;
        FrameLayout tile = new FrameLayout(this);
        tileImage = tileArtView();
        lockStatus = Ui.text(this, "", 13, 400, soft);
        LinearLayout content = artTile(tile, tileImage, "Double-tap to turn off", R.drawable.ic_tap, lockStatus);

        homeSwitch = switchRow(content, "Home screen", "Empty space, with a tap you feel", onText, soft);
        homeSwitch.setOnCheckedChangeListener((b, checked) -> {
            TapWallpaper.setEnabled(this, checked);
            refreshStatus();
        });
        lockSwitch = switchRow(content, "Lock screen", "Pixel's own double-tap, no vibration", onText, soft);
        lockSwitch.setOnCheckedChangeListener((b, checked) -> {
            try {
                LockService.setLockScreen(this, checked);
            } catch (SecurityException e) {
                b.setChecked(!checked);
                Toast.makeText(this, "Needs the one-time setup in Settings first", Toast.LENGTH_LONG).show();
            }
            refreshStatus();
        });
        return tile;
    }

    // Brightness and volume sliders that grow out of the camera. They open with a double-tap on the back of the phone
    // (Pixel's Quick Tap, pointed at SlidersActivity) or, in landscape, a tap on the camera, which can be switched off
    // on its own. Needs "Display over other apps", which only the user can allow.
    private FrameLayout volumeTile() {
        int onText = 0xFFFFFFFF, soft = 0xCCFFFFFF;
        FrameLayout tile = new FrameLayout(this);
        volumeImage = tileArtView();
        volumeStatus = Ui.text(this, "", 13, 400, soft);
        LinearLayout content = artTile(tile, volumeImage, "Brightness and volume", R.drawable.ic_back_tap, volumeStatus);
        panelSwitch = switchRow(content, "Sliders", "Double-tap the back of the phone", onText, soft);
        panelSwitch.setOnCheckedChangeListener((b, checked) -> {
            if (checked && !NotchPanel.allowed(this)) {
                b.setChecked(false);
                wantPanel = true;
                Toast.makeText(this, "Allow TapOff here, then come back", Toast.LENGTH_LONG).show();
                startActivity(new Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())));
                return;
            }
            NotchPanel.setEnabled(this, checked);
            refreshStatus();
        });
        cameraSwitch = switchRow(content, "Tap the camera", "In landscape, marked by a faint ring", onText, soft);
        cameraSwitch.setOnCheckedChangeListener((b, checked) -> NotchPanel.setCameraTap(this, checked));
        backTap = Ui.pill(this, "Set up back tap", Ui.Pill.ON_PHOTO);
        backTap.setOnClickListener(v -> {
            if (!SlidersActivity.backTapSeen(this)) Toast.makeText(this, "Open app → TapOff Sliders", Toast.LENGTH_LONG).show();
            try {
                startActivity(new Intent(QUICK_TAP_SETTINGS));
            } catch (android.content.ActivityNotFoundException e) { // not a Pixel with Quick Tap
                Toast.makeText(this, "This phone doesn't have Quick Tap", Toast.LENGTH_LONG).show();
            }
        });
        LinearLayout.LayoutParams backLp = new LinearLayout.LayoutParams(-1, -2);
        backLp.topMargin = Ui.dp(this, 12);
        content.addView(backTap, backLp);
        ((LinearLayout.LayoutParams) tile.getLayoutParams()).topMargin = Ui.dp(this, 12);
        return tile;
    }

    // Behind the tile: TapOff's own art, a dark LED grid lit up by tap ripples around a big pixel hand.
    private void loadTileImage() {
        if (tileBitmap != null) {
            tileImage.setImageBitmap(tileBitmap);
            volumeImage.setImageBitmap(volumeBitmap);
            return;
        }
        int w = getResources().getDisplayMetrics().widthPixels - 2 * Ui.dp(this, 16), h = Ui.dp(this, 200);
        bg.execute(() -> {
            Bitmap b = tileArt(w, h), v = volumeArt(w, h);
            runOnUiThread(() -> {
                tileBitmap = b;
                volumeBitmap = v;
                tileImage.setImageBitmap(b);
                volumeImage.setImageBitmap(v);
            });
        });
    }

    // The volume tile's art: the same LED grid, with a column on the right lit like a volume meter.
    private Bitmap volumeArt(int w, int h) {
        Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        android.graphics.Canvas c = new android.graphics.Canvas(b);
        android.graphics.Paint p = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        p.setShader(new android.graphics.LinearGradient(0, 0, w, h, 0xFF24242C, 0xFF0B0B0E, android.graphics.Shader.TileMode.CLAMP));
        c.drawRect(0, 0, w, h, p);
        p.setShader(null);
        float step = Ui.dp(this, 11), dot = Ui.dp(this, 3);
        int cols = (int) (w / step);
        for (int col = 0; col < cols; col++) {
            float x = step / 2 + col * step;
            // Towards the right, columns rise like a volume meter; further left they fade to the plain grid.
            float level = Math.max(0, (float) (col - cols * 0.55) / (cols * 0.45f));
            for (float y = step / 2; y < h; y += step) {
                boolean lit = y > h * (1 - level * 0.85f);
                p.setColor(((lit ? (int) (40 + 150 * level) : 14) << 24) | 0xFFFFFF);
                c.drawRect(x - dot / 2, y - dot / 2, x + dot / 2, y + dot / 2, p);
            }
        }
        return b;
    }

    private Bitmap tileArt(int w, int h) {
        Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        android.graphics.Canvas c = new android.graphics.Canvas(b);
        android.graphics.Paint p = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        p.setShader(new android.graphics.LinearGradient(0, 0, w, h, 0xFF24242C, 0xFF0B0B0E, android.graphics.Shader.TileMode.CLAMP));
        c.drawRect(0, 0, w, h, p);
        p.setShader(null);
        float tapX = w * 0.8f, tapY = h * 0.3f, step = Ui.dp(this, 11), dot = Ui.dp(this, 3), reach = w * 0.6f;
        for (float y = step / 2; y < h; y += step)
            for (float x = step / 2; x < w; x += step) {
                float lit = Math.max(0, 1 - (float) Math.hypot(x - tapX, y - tapY) / reach);
                p.setColor(((int) (14 + 70 * lit * lit) << 24) | 0xFFFFFF);
                c.drawRect(x - dot / 2, y - dot / 2, x + dot / 2, y + dot / 2, p);
            }
        p.setStyle(android.graphics.Paint.Style.STROKE);
        p.setStrokeWidth(Ui.dp(this, 1.5f));
        for (int i = 1; i <= 3; i++) {
            p.setColor(((90 - i * 22) << 24) | 0xFFFFFF);
            c.drawCircle(tapX, tapY, Ui.dp(this, 26) * i, p);
        }
        android.graphics.drawable.Drawable hand = getDrawable(R.drawable.ic_tap).mutate();
        hand.setTint(0xFFFFFFFF);
        hand.setAlpha(46);
        // ic_tap's fingertip sits near the top centre of its box.
        int size = Math.round(h * 1.05f), left = Math.round(tapX - size * 0.5f), top = Math.round(tapY - size * 0.08f);
        hand.setBounds(left, top, left + size, top + size);
        hand.draw(c);
        return b;
    }

    private Switch switchRow(LinearLayout tile, String name, String what, int onText, int soft) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setMinimumHeight(Ui.dp(this, 56));
        row.setPadding(0, Ui.dp(this, 8), 0, 0);
        LinearLayout words = new LinearLayout(this);
        words.setOrientation(LinearLayout.VERTICAL);
        words.addView(Ui.text(this, name, 15, 600, onText));
        words.addView(Ui.text(this, what, 12.5f, 400, soft));
        row.addView(words, new LinearLayout.LayoutParams(0, -2, 1));
        Switch sw = new Switch(this);
        sw.setContentDescription(name + ", " + what);
        switchArt(sw, onText, soft);
        row.addView(sw);
        row.setOnClickListener(v -> sw.toggle());
        tile.addView(row);
        return sw;
    }

    // On: a solid white track with a big dark knob. Off: a dim outlined track with a small light knob. Clear over
    // the art. Both are drawn from how far the knob has slid, so while Switch animates it across, the knob grows or
    // shrinks and the colours blend along the way. The 32dp-square knob also sizes the track: 64x32dp.
    private void switchArt(Switch sw, int onText, int soft) {
        android.graphics.Rect track = new android.graphics.Rect(), thumb = new android.graphics.Rect();
        float dp = Ui.dp(this, 1);
        java.util.function.DoubleSupplier slid = () -> {
            int travel = track.width() - thumb.width();
            return travel <= 0 ? 0 : Math.max(0, Math.min(1, (thumb.left - track.left) / (float) travel));
        };
        android.graphics.Paint paint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        sw.setTrackDrawable(new SwitchPart(track, 0) {
            @Override public void draw(android.graphics.Canvas c) {
                float p = (float) slid.getAsDouble(), r = getBounds().height() / 2f, inset = dp;
                paint.setStyle(android.graphics.Paint.Style.FILL);
                paint.setColor(Ui.blend(0x40000000, onText, p));
                c.drawRoundRect(getBounds().left + inset, getBounds().top + inset, getBounds().right - inset,
                    getBounds().bottom - inset, r, r, paint);
                paint.setStyle(android.graphics.Paint.Style.STROKE);
                paint.setStrokeWidth(2 * dp);
                paint.setColor(Ui.blend(soft, onText, p));
                c.drawRoundRect(getBounds().left + inset, getBounds().top + inset, getBounds().right - inset,
                    getBounds().bottom - inset, r, r, paint);
            }
        });
        sw.setThumbDrawable(new SwitchPart(thumb, Math.round(32 * dp)) {
            @Override public void draw(android.graphics.Canvas c) {
                float p = (float) slid.getAsDouble();
                paint.setStyle(android.graphics.Paint.Style.FILL);
                paint.setColor(Ui.blend(soft, 0xFF16161A, p));
                c.drawCircle(getBounds().exactCenterX(), getBounds().exactCenterY(), (8 + 4 * p) * dp, paint);
            }
        });
        sw.setSwitchMinWidth(0);
    }

    // A drawable that remembers where Switch put it, so the other half of the switch can tell how far it slid.
    private abstract static class SwitchPart extends android.graphics.drawable.Drawable {
        private final android.graphics.Rect where;
        private final int size;

        SwitchPart(android.graphics.Rect where, int size) {
            this.where = where;
            this.size = size;
        }

        @Override protected void onBoundsChange(android.graphics.Rect b) { where.set(b); }
        @Override public int getIntrinsicWidth() { return size > 0 ? size : -1; }
        @Override public int getIntrinsicHeight() { return size > 0 ? size : -1; }
        @Override public void setAlpha(int a) {}
        @Override public void setColorFilter(android.graphics.ColorFilter f) {}
        @Override public int getOpacity() { return android.graphics.PixelFormat.TRANSLUCENT; }
    }

    private void refreshStatus() {
        if (!panelSwitch.isChecked()) volumeStatus.setText("Off");
        else if (!Wallpapers.isActive(this)) volumeStatus.setText("Works while TapOff is your wallpaper");
        else if (!LockService.canLock(this)) volumeStatus.setText("On for volume. Brightness needs the one-time setup");
        else volumeStatus.setText("On for brightness and volume");
        if (!LockService.canLock(this)) lockStatus.setText("Needs a one-time setup, see Settings");
        else if (homeSwitch.isChecked() && lockSwitch.isChecked()) lockStatus.setText("On for the home and lock screen");
        else if (homeSwitch.isChecked()) lockStatus.setText("On for the home screen");
        else if (lockSwitch.isChecked()) lockStatus.setText("On for the lock screen");
        else lockStatus.setText("Off");
    }

    // Favourites come first among the wallpapers and follow what's saved, so the card is rebuilt on every return.
    private void refreshFavourites() {
        if (favCard != null) {
            feedList.removeView(favCard);
            cards.remove(favCard);
            favCard = null;
        }
        List<Wallpapers.Item> saved = Wallpapers.favourites(this);
        if (saved.isEmpty()) return;
        favCard = addCard(new Wallpapers.Collection("Favourites", "Saved", saved),
            feedList.indexOfChild(wallpapersHeader) + 1);
    }

    private GlassCard addCard(Wallpapers.Collection col) {
        return addCard(col, -1);
    }

    private GlassCard addCard(Wallpapers.Collection col, int at) {
        GlassCard card = new GlassCard(this);
        int n = col.items.size();
        card.bind(col.name, n + (n == 1 ? " wallpaper" : " wallpapers") + " · " + col.source, cardIcon(col.name));
        card.setOnClickListener(v -> startActivity(PreviewActivity.intent(this, col, 0)));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        int m = Ui.dp(this, 16);
        lp.setMargins(m, 0, m, Ui.dp(this, CARD_GAP_DP));
        feedList.addView(card, at, lp);
        cards.add(card);
        // Keyed by the first picture too, since the favourites card's first picture changes.
        String coverKey = col.name + "|" + Wallpapers.key(col.items.get(0));
        Bitmap cached = covers.get(coverKey);
        if (cached != null) {
            card.setPhoto(cached);
            return card;
        }
        int w = Math.max(1, getResources().getDisplayMetrics().widthPixels - 2 * m);
        bg.execute(() -> {
            try {
                Bitmap b = Wallpapers.decode(this, col.items.get(0), true, w, Math.round(w * 0.62f));
                runOnUiThread(() -> {
                    covers.put(coverKey, b);
                    card.setPhoto(b);
                });
            } catch (Exception e) {
                Log.w("TapOff", "no cover for " + col.name, e);
            }
        });
        return card;
    }

    private static int cardIcon(String name) {
        switch (name) {
            case "Favourites": return R.drawable.ic_heart_fill;
            case "Pixel": return R.drawable.ic_card_pixel;
            case "Cutout": return R.drawable.ic_card_cutout;
            case "Today": return R.drawable.ic_card_today;
            case "Abstract": return R.drawable.ic_card_abstract;
            case "Minimal": return R.drawable.ic_card_minimal;
            case "Dreamscape": return R.drawable.ic_card_dream;
            case "NASA": return R.drawable.ic_card_space;
            default: return R.drawable.ic_grid;
        }
    }

    // Cards that scroll up stack into a deck at the top: each older one sits a little higher, smaller and
    // darker, peeking out above the card that slid over it.
    private void stackCards() {
        int pile = feed.getPaddingTop() + Ui.dp(this, 34), peek = Ui.dp(this, PEEK_DP);
        for (GlassCard card : cards) {
            float over = pile - (feed.getPaddingTop() + card.getTop() - feed.getScrollY());
            float depth = over <= 0 ? 0 : over / (card.getHeight() + Ui.dp(this, CARD_GAP_DP));
            float d = Math.min(depth, DECK_DEPTH);
            card.setPivotX(card.getWidth() / 2f);
            card.setPivotY(0);
            card.setTranslationY(over <= 0 ? 0 : over - d * peek);
            card.setScaleX(1 - 0.06f * d);
            card.setScaleY(1 - 0.06f * d);
            card.setDim(0.18f * d);
            card.setVisibility(depth > DECK_DEPTH ? View.INVISIBLE : View.VISIBLE);
        }
    }

    private ScrollView buildSettings() {
        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        TextView h = title("Settings");
        h.setPadding(Ui.dp(this, 4), Ui.dp(this, 20), Ui.dp(this, 4), Ui.dp(this, 16));
        list.addView(h);

        LinearLayout setup = card(list);
        setup.addView(Ui.text(this, "One-time setup", 18, 600, Ui.ink(this)));
        TextView how = Ui.text(this, "Turning the screen off needs one permission that only a computer can give. "
            + "Turn on USB debugging, plug the phone in, and run this on the computer:", 14, 400, Ui.muted(this));
        how.setPadding(0, Ui.dp(this, 6), 0, Ui.dp(this, 10));
        setup.addView(how);
        TextView cmd = Ui.text(this, GRANT, 12, 400, Ui.ink(this));
        cmd.setTypeface(android.graphics.Typeface.MONOSPACE);
        cmd.setTextIsSelectable(true);
        cmd.setBackground(Ui.shape(this, 12, Ui.dark(this) ? 0x14FFFFFF : 0x0D000000, 0));
        int pad = Ui.dp(this, 12);
        cmd.setPadding(pad, pad, pad, pad);
        setup.addView(cmd, new LinearLayout.LayoutParams(-1, -2));
        Button copy = Ui.pill(this, "Copy command", Ui.Pill.PRIMARY);
        copy.setOnClickListener(v -> getSystemService(android.content.ClipboardManager.class)
            .setPrimaryClip(android.content.ClipData.newPlainText("TapOff setup", GRANT)));
        LinearLayout.LayoutParams copyLp = new LinearLayout.LayoutParams(-1, -2);
        copyLp.topMargin = Ui.dp(this, 12);
        setup.addView(copy, copyLp);
        Button guide = Ui.pill(this, "Step-by-step guide", Ui.Pill.SECONDARY);
        guide.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(SETUP_GUIDE))));
        LinearLayout.LayoutParams guideLp = new LinearLayout.LayoutParams(-1, -2);
        guideLp.topMargin = Ui.dp(this, 8);
        setup.addView(guide, guideLp);
        setupCard = setup;

        LinearLayout look = card(list);
        look.addView(Ui.text(this, "Appearance", 18, 600, Ui.ink(this)));
        LinearLayout seg = new LinearLayout(this);
        seg.setBackground(Ui.shape(this, 22, Ui.dark(this) ? 0x14FFFFFF : 0x0D000000, 0));
        int sp = Ui.dp(this, 4);
        seg.setPadding(sp, sp, sp, sp);
        for (int i = 0; i < APPEARANCE.length; i++) {
            TextView t = Ui.text(this, APPEARANCE[i], 14, 500, Ui.muted(this));
            t.setGravity(Gravity.CENTER);
            t.setMinHeight(Ui.dp(this, 40));
            int which = i;
            // Marks the choice straight away: picking System can leave the colours as they are.
            t.setOnClickListener(v -> {
                styleSegments(which);
                setAppearance(which);
            });
            segments[i] = t;
            seg.addView(t, new LinearLayout.LayoutParams(0, -2, 1));
        }
        styleSegments(getSharedPreferences("tapoff", MODE_PRIVATE).getInt("appearance", 0));
        LinearLayout.LayoutParams segLp = new LinearLayout.LayoutParams(-1, -2);
        segLp.topMargin = Ui.dp(this, 12);
        look.addView(seg, segLp);

        LinearLayout wallpaper = card(list);
        wallpaper.addView(Ui.text(this, "Wallpaper", 18, 600, Ui.ink(this)));
        TextView note = Ui.text(this, "Wallpapers you set go on both the home and lock screen.", 14, 400, Ui.muted(this));
        note.setPadding(0, Ui.dp(this, 6), 0, Ui.dp(this, 14));
        wallpaper.addView(note);
        Button own = Ui.pill(this, "Use your own photo", Ui.Pill.SECONDARY);
        own.setOnClickListener(v -> startActivityForResult(
            new Intent(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).setType("image/*"), PICK));
        wallpaper.addView(own, new LinearLayout.LayoutParams(-1, -2));
        setTapOff = Ui.pill(this, "Set TapOff as home wallpaper", Ui.Pill.PRIMARY);
        setTapOff.setOnClickListener(v -> startActivity(chooser(this)));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = Ui.dp(this, 10);
        wallpaper.addView(setTapOff, lp);

        LinearLayout tiles = card(list);
        tiles.addView(Ui.text(this, "Quick Settings tiles", 18, 600, Ui.ink(this)));
        tileRow(tiles, "Screen off", "Turns the screen off from anywhere", ScreenOffTile.class, R.drawable.ic_moon);
        tileRow(tiles, "Volume", "Opens the volume slider", VolumeTile.class, R.drawable.ic_volume);

        LinearLayout help = card(list);
        help.addView(Ui.text(this, "Something not working?", 18, 600, Ui.ink(this)));
        TextView where = Ui.text(this, "Message Taseen on X, @taseen_tariq_. If you can't send a DM, just reply "
            + "to any of the posts.", 14, 400, Ui.muted(this));
        where.setPadding(0, Ui.dp(this, 6), 0, Ui.dp(this, 14));
        help.addView(where);
        Button bug = Ui.pill(this, "Report a bug", Ui.Pill.SECONDARY);
        bug.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(BUG_REPORTS))));
        help.addView(bug, new LinearLayout.LayoutParams(-1, -2));

        TextView privacy = Ui.text(this, "Accessibility stays off. TapOff switches it on for a moment only while "
            + "locking, so bank apps keep working. It goes online only for wallpapers and to check GitHub for "
            + "updates.\nTapOff " + versionName(), 12.5f, 400, Ui.faint(this));
        privacy.setPadding(Ui.dp(this, 4), Ui.dp(this, 4), Ui.dp(this, 4), 0);
        list.addView(privacy);

        ScrollView scroll = new ScrollView(this);
        scroll.setClipToPadding(false);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setBackgroundColor(Ui.bg(this)); // opaque, so it covers Wallpapers as it slides over
        scroll.setElevation(Ui.dp(this, 12)); // a soft edge shadow while it moves
        scroll.addView(list);
        return scroll;
    }

    // Swiping sideways moves between Wallpapers and Settings, following the finger like the wallpaper preview.
    // It only takes over a clearly sideways drag, so vertical scrolling and taps work as before.
    private final class SwipeRoot extends FrameLayout {
        private final int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        private float downX, downY;
        private boolean dragging;
        private VelocityTracker vt;

        SwipeRoot(Context c) { super(c); }

        @Override public boolean onInterceptTouchEvent(MotionEvent e) {
            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    downX = e.getX();
                    downY = e.getY();
                    dragging = false;
                    if (vt != null) vt.recycle();
                    vt = VelocityTracker.obtain();
                    vt.addMovement(e);
                    return false;
                case MotionEvent.ACTION_MOVE:
                    if (vt == null) return false;
                    vt.addMovement(e);
                    float mx = e.getX() - downX, my = e.getY() - downY;
                    boolean towardsOther = tab == 0 ? mx < 0 : mx > 0;
                    if (towardsOther && Math.abs(mx) > slop * 2 && Math.abs(mx) > Math.abs(my) * 1.5f) {
                        dragging = true;
                        downX = e.getX();
                        beginPageDrag();
                        return true;
                    }
                    return false;
                default:
                    return false;
            }
        }

        @Override public boolean onTouchEvent(MotionEvent e) {
            if (!dragging || vt == null) return false;
            vt.addMovement(e);
            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_MOVE:
                    dragPages(e.getX() - downX);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    vt.computeCurrentVelocity(1000);
                    releasePages(e.getX() - downX, vt.getXVelocity());
                    dragging = false;
                    return true;
                default:
                    return true;
            }
        }
    }

    private void beginPageDrag() {
        float w = getResources().getDisplayMetrics().widthPixels;
        for (View v : new View[] {feed, settings}) v.animate().setListener(null).cancel();
        feed.setVisibility(View.VISIBLE);
        settings.setVisibility(View.VISIBLE);
        if (tab == 0) {
            settings.setTranslationX(w);
        } else {
            feed.setTranslationX(-w * 0.3f);
            feed.setAlpha(0.6f);
        }
    }

    private void dragPages(float dx) {
        float w = getResources().getDisplayMetrics().widthPixels;
        float towardsSettings = tab == 0 ? Math.max(0, Math.min(1, -dx / w)) : 1 - Math.max(0, Math.min(1, dx / w));
        indicator.setTranslationX(towardsSettings * tabW);
        if (tab == 0) {
            float d = Math.max(-w, Math.min(0, dx));
            settings.setTranslationX(w + d);
            feed.setTranslationX(d * 0.3f);
            feed.setAlpha(1 + 0.4f * d / w);
        } else {
            float d = Math.max(0, Math.min(w, dx));
            settings.setTranslationX(d);
            feed.setTranslationX(-w * 0.3f + d * 0.3f);
            feed.setAlpha(0.6f + 0.4f * d / w);
        }
    }

    // Far or fast enough: finish the move. Otherwise spring back to where it started.
    private void releasePages(float dx, float vx) {
        float w = getResources().getDisplayMetrics().widthPixels;
        boolean go = tab == 0 ? (dx < -w * 0.3f || vx < -800) : (dx > w * 0.3f || vx > 800);
        if (go) {
            select(1 - tab, true);
            return;
        }
        android.view.animation.DecelerateInterpolator ease = new android.view.animation.DecelerateInterpolator(1.6f);
        indicator.animate().translationX(tab * tabW).setDuration(TAB_SLIDE_MS).setInterpolator(ease);
        if (tab == 0) {
            feed.animate().translationX(0).alpha(1f).setDuration(TAB_SLIDE_MS).setInterpolator(ease);
            settings.animate().translationX(w).setDuration(TAB_SLIDE_MS).setInterpolator(ease).withEndAction(() -> park(settings));
        } else {
            settings.animate().translationX(0).setDuration(TAB_SLIDE_MS).setInterpolator(ease);
            feed.animate().translationX(-w * 0.3f).alpha(0.6f).setDuration(TAB_SLIDE_MS).setInterpolator(ease)
                .withEndAction(() -> park(feed));
        }
    }

    // Hidden and reset, ready for the next slide.
    private void park(View page) {
        page.setVisibility(View.GONE);
        page.setTranslationX(0);
        page.setAlpha(1f);
    }

    private void styleSegments(int chosen) {
        for (int i = 0; i < segments.length; i++) {
            boolean on = i == chosen;
            segments[i].setTypeface(Ui.font(on ? 600 : 500));
            segments[i].setTextColor(on ? Ui.ink(this) : Ui.muted(this));
            segments[i].setBackground(on ? Ui.shape(this, 18, Ui.dark(this) ? 0x33FFFFFF : 0xFFFFFFFF, 0) : null);
        }
    }

    private LinearLayout card(LinearLayout parent) {
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setBackground(Ui.shape(this, 24, Ui.surface(this), Ui.line(this)));
        int pad = Ui.dp(this, 20);
        c.setPadding(pad, pad, pad, pad);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.bottomMargin = Ui.dp(this, 12);
        parent.addView(c, lp);
        return c;
    }

    private void tileRow(LinearLayout parent, String name, String what, Class<?> tile, int icon) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, Ui.dp(this, 14), 0, 0);
        LinearLayout words = new LinearLayout(this);
        words.setOrientation(LinearLayout.VERTICAL);
        words.addView(Ui.text(this, name, 15, 600, Ui.ink(this)));
        words.addView(Ui.text(this, what, 13, 400, Ui.muted(this)));
        row.addView(words, new LinearLayout.LayoutParams(0, -2, 1));
        Button add = Ui.pill(this, "Add", Ui.Pill.SECONDARY);
        add.setMinHeight(Ui.dp(this, 40));
        add.setMinimumHeight(Ui.dp(this, 40));
        add.setOnClickListener(v -> getSystemService(StatusBarManager.class).requestAddTileService(
            new ComponentName(this, tile), name, Icon.createWithResource(this, icon), getMainExecutor(), result -> {
                if (result == StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED)
                    Toast.makeText(this, name + " is already in Quick Settings", Toast.LENGTH_SHORT).show();
            }));
        row.addView(add, new LinearLayout.LayoutParams(Ui.dp(this, 80), -2));
        parent.addView(row);
    }

    // The floating tab bar. The highlight is its own view behind the tabs, so it can slide between them.
    private FrameLayout buildTabBar() {
        FrameLayout bar = new FrameLayout(this);
        bar.setBackground(Ui.shape(this, 32, Ui.dark(this) ? 0xE61A1B1E : 0xF2FFFFFF, Ui.line(this)));
        bar.setElevation(Ui.dp(this, 16)); // above the Settings page as it slides
        int p = Ui.dp(this, 6);
        bar.setPadding(p, p, p, p);
        indicator = new View(this);
        indicator.setBackground(Ui.shape(this, 26, Ui.dark(this) ? 0x26FFFFFF : 0x12000000, 0));
        bar.addView(indicator, new FrameLayout.LayoutParams(0, 0)); // sized to the tabs once they're measured
        LinearLayout row = new LinearLayout(this);
        String[] names = {"Wallpapers", "Settings"};
        int[] icons = {R.drawable.ic_grid, R.drawable.ic_sliders};
        for (int i = 0; i < 2; i++) {
            TextView t = Ui.text(this, names[i], 11, 500, Ui.ink(this));
            t.setGravity(Gravity.CENTER);
            t.setCompoundDrawablesWithIntrinsicBounds(0, icons[i], 0, 0);
            t.setCompoundDrawablePadding(Ui.dp(this, 2));
            t.setPadding(Ui.dp(this, 20), Ui.dp(this, 8), Ui.dp(this, 20), Ui.dp(this, 8));
            int which = i;
            t.setOnClickListener(v -> select(which));
            tabs[i] = t;
            row.addView(t);
        }
        bar.addView(row);
        // Once measured, give both tabs the wider one's width so the highlight only has to move, not resize.
        tabW = 0;
        row.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) -> {
            int widest = Math.max(tabs[0].getWidth(), tabs[1].getWidth());
            if (widest == 0 || (widest == tabW && indicator.getHeight() == v.getHeight())) return;
            tabW = widest;
            v.post(() -> {
                for (TextView tv : tabs) tv.getLayoutParams().width = tabW;
                indicator.getLayoutParams().width = tabW;
                indicator.getLayoutParams().height = v.getHeight();
                indicator.setTranslationX(Math.max(tab, 0) * tabW);
                row.requestLayout();
            });
        });
        return bar;
    }

    // Like swiping between wallpapers: Settings slides in from the right over Wallpapers, which drifts a third as
    // far to the left and dims; going back runs the same motion in reverse.
    private void select(int which) {
        select(which, false);
    }

    // fromDrag: the pages are already part-way, so animate on from where the finger left them.
    private void select(int which, boolean fromDrag) {
        if (which == tab) return;
        float w = getResources().getDisplayMetrics().widthPixels;
        for (View v : new View[] {feed, settings}) v.animate().setListener(null).cancel();
        if (tab == -1) {
            feed.setVisibility(which == 0 ? View.VISIBLE : View.GONE);
            settings.setVisibility(which == 1 ? View.VISIBLE : View.GONE);
        } else {
            android.view.animation.DecelerateInterpolator ease = new android.view.animation.DecelerateInterpolator(1.6f);
            feed.setVisibility(View.VISIBLE);
            settings.setVisibility(View.VISIBLE);
            if (which == 1) {
                if (!fromDrag) settings.setTranslationX(w);
                settings.animate().translationX(0).setDuration(TAB_SLIDE_MS).setInterpolator(ease);
                feed.animate().translationX(-w * 0.3f).alpha(0.6f).setDuration(TAB_SLIDE_MS).setInterpolator(ease)
                    .withEndAction(() -> park(feed));
            } else {
                if (!fromDrag) {
                    feed.setTranslationX(-w * 0.3f);
                    feed.setAlpha(0.6f);
                }
                feed.animate().translationX(0).alpha(1f).setDuration(TAB_SLIDE_MS).setInterpolator(ease);
                settings.animate().translationX(w).setDuration(TAB_SLIDE_MS).setInterpolator(ease)
                    .withEndAction(() -> park(settings));
            }
        }
        if (tab == -1) indicator.setTranslationX(which * tabW);
        else indicator.animate().translationX(which * tabW).setDuration(TAB_SLIDE_MS)
            .setInterpolator(new android.view.animation.DecelerateInterpolator(1.6f));
        tab = which;
        for (int i = 0; i < tabs.length; i++) {
            boolean on = i == which;
            int color = on ? Ui.ink(this) : Ui.faint(this);
            tabs[i].setTextColor(color);
            tabs[i].setCompoundDrawableTintList(ColorStateList.valueOf(color));
            tabs[i].setSelected(on);
        }
    }

    @Override protected void onActivityResult(int request, int result, Intent data) {
        if (request != PICK || result != RESULT_OK || data == null) return;
        List<Wallpapers.Item> one = new ArrayList<>();
        one.add(new Wallpapers.Item("Your photo", data.getData().toString(), null, "", 0));
        startActivity(PreviewActivity.intent(this, new Wallpapers.Collection("Your photo", "You", one), 0)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION));
    }
}
