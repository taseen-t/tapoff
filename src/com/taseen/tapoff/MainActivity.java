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
    // Deck tuning: how far each older card peeks out above the next, and how many stay visible.
    private static final int PEEK_DP = 10, DECK_DEPTH = 3;
    // Appearance choices, stored as the index; following the system is the default.
    private static final String[] APPEARANCE = {"System", "Light", "Dark"};
    private static final int[] NIGHT_MODE = {UiModeManager.MODE_NIGHT_AUTO, UiModeManager.MODE_NIGHT_NO, UiModeManager.MODE_NIGHT_YES};

    private final ExecutorService bg = Executors.newFixedThreadPool(3);
    // ponytail: every card stays alive (no recycling) so the deck can show; fine for the ~8 collections.
    private final List<GlassCard> cards = new ArrayList<>();
    private ScrollView feed, settings;
    private LinearLayout feedList;
    private TextView lockStatus;
    private ImageView tileImage;
    private Switch homeSwitch, lockSwitch;
    private View setupCard;
    private Button setTapOff;
    private final TextView[] tabs = new TextView[2];
    private int tab = -1;

    static Intent chooser(Context c) {
        return new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).putExtra(
            WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(c, TapWallpaper.class));
    }

    @Override protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Ui.bg(this));

        feed = new ScrollView(this);
        feed.setClipToPadding(false);
        feed.setVerticalScrollBarEnabled(false);
        feedList = new LinearLayout(this);
        feedList.setOrientation(LinearLayout.VERTICAL);
        feedList.addView(title("TapOff"));
        feedList.addView(lockTile());
        feedList.addView(section("Wallpapers"));
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
        root.addView(scrim, new FrameLayout.LayoutParams(-1, 0, Gravity.TOP));

        LinearLayout tabBar = buildTabBar();
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
        select(0);

        bg.execute(() -> Wallpapers.load(this, col -> runOnUiThread(() -> addCard(col))));
    }

    @Override protected void onResume() {
        super.onResume();
        loadTileImage();
        homeSwitch.setChecked(TapWallpaper.enabled(this));
        lockSwitch.setChecked(LockService.lockScreenOn(this));
        refreshStatus();
        setupCard.setVisibility(LockService.canLock(this) ? View.GONE : View.VISIBLE);
        setTapOff.setVisibility(Wallpapers.isActive(this) ? View.GONE : View.VISIBLE);
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
    private FrameLayout lockTile() {
        int onText = 0xFFFFFFFF, soft = 0xCCFFFFFF;
        FrameLayout tile = new FrameLayout(this);
        tile.setBackground(Ui.shape(this, 28, 0xFF1B1C20, 0));
        tile.setClipToOutline(true);
        tileImage = new ImageView(this) {
            // Fill whatever height the controls give the tile; never let the photo decide it.
            @Override protected void onMeasure(int wSpec, int hSpec) {
                setMeasuredDimension(MeasureSpec.getSize(wSpec),
                    MeasureSpec.getMode(hSpec) == MeasureSpec.EXACTLY ? MeasureSpec.getSize(hSpec) : 0);
            }
        };
        tileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        tile.addView(tileImage, new FrameLayout.LayoutParams(-1, -1));
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
        icon.setImageResource(R.drawable.ic_tap);
        icon.setImageTintList(ColorStateList.valueOf(onText));
        icon.setBackground(Ui.glass(this, 24, 0x33FFFFFF));
        int ip = Ui.dp(this, 10);
        icon.setPadding(ip, ip, ip, ip);
        head.addView(icon, new LinearLayout.LayoutParams(Ui.dp(this, 48), Ui.dp(this, 48)));
        LinearLayout words = new LinearLayout(this);
        words.setOrientation(LinearLayout.VERTICAL);
        words.setPadding(Ui.dp(this, 14), 0, 0, 0);
        words.addView(Ui.text(this, "Double-tap to turn off", 18, 600, onText));
        lockStatus = Ui.text(this, "", 13, 400, soft);
        words.addView(lockStatus);
        head.addView(words, new LinearLayout.LayoutParams(0, -2, 1));
        content.addView(head);

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
        tile.addView(content, new FrameLayout.LayoutParams(-1, -2));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        int m = Ui.dp(this, 16);
        lp.setMargins(m, 0, m, 0);
        tile.setLayoutParams(lp);
        return tile;
    }

    // Behind the tile: TapOff's own art, a dark LED grid lit up by tap ripples around a big pixel hand.
    private void loadTileImage() {
        if (tileImage.getDrawable() != null) return;
        int w = getResources().getDisplayMetrics().widthPixels - 2 * Ui.dp(this, 16), h = Ui.dp(this, 200);
        bg.execute(() -> {
            Bitmap b = tileArt(w, h);
            runOnUiThread(() -> tileImage.setImageBitmap(b));
        });
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
        sw.setContentDescription("Double-tap to turn off, " + name);
        int[][] states = {{android.R.attr.state_checked}, {}};
        sw.setThumbTintList(new ColorStateList(states, new int[] {onText, soft}));
        sw.setTrackTintList(new ColorStateList(states,
            new int[] {(onText & 0x00FFFFFF) | 0x80000000, (onText & 0x00FFFFFF) | 0x33000000}));
        row.addView(sw);
        row.setOnClickListener(v -> sw.toggle());
        tile.addView(row);
        return sw;
    }

    private void refreshStatus() {
        if (!LockService.canLock(this)) lockStatus.setText("Needs a one-time setup, see Settings");
        else if (homeSwitch.isChecked() && lockSwitch.isChecked()) lockStatus.setText("On for the home and lock screen");
        else if (homeSwitch.isChecked()) lockStatus.setText("On for the home screen");
        else if (lockSwitch.isChecked()) lockStatus.setText("On for the lock screen");
        else lockStatus.setText("Off");
    }

    private void addCard(Wallpapers.Collection col) {
        GlassCard card = new GlassCard(this);
        int n = col.items.size();
        card.bind(col.name, n + (n == 1 ? " wallpaper" : " wallpapers"), col.source);
        card.setOnClickListener(v -> startActivity(PreviewActivity.intent(this, col, 0)));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        int m = Ui.dp(this, 16);
        lp.setMargins(m, 0, m, Ui.dp(this, CARD_GAP_DP));
        feedList.addView(card, lp);
        cards.add(card);
        int w = Math.max(1, getResources().getDisplayMetrics().widthPixels - 2 * m);
        bg.execute(() -> {
            try {
                Bitmap b = Wallpapers.decode(this, col.items.get(0), true, w, Math.round(w * 0.62f));
                runOnUiThread(() -> card.setPhoto(b));
            } catch (Exception e) {
                Log.w("TapOff", "no cover for " + col.name, e);
            }
        });
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
        TextView how = Ui.text(this, "Double-tap lock needs a permission only a computer can give. With the phone "
            + "plugged in, run:\nadb shell pm grant com.taseen.tapoff android.permission.WRITE_SECURE_SETTINGS",
            14, 400, Ui.muted(this));
        how.setTextIsSelectable(true);
        how.setPadding(0, Ui.dp(this, 6), 0, 0);
        setup.addView(how);
        setupCard = setup;

        LinearLayout look = card(list);
        look.addView(Ui.text(this, "Appearance", 18, 600, Ui.ink(this)));
        LinearLayout seg = new LinearLayout(this);
        seg.setBackground(Ui.shape(this, 22, Ui.dark(this) ? 0x14FFFFFF : 0x0D000000, 0));
        int sp = Ui.dp(this, 4);
        seg.setPadding(sp, sp, sp, sp);
        int chosen = getSharedPreferences("tapoff", MODE_PRIVATE).getInt("appearance", 0);
        for (int i = 0; i < APPEARANCE.length; i++) {
            boolean on = i == chosen;
            TextView t = Ui.text(this, APPEARANCE[i], 14, on ? 600 : 500, on ? Ui.ink(this) : Ui.muted(this));
            t.setGravity(Gravity.CENTER);
            t.setMinHeight(Ui.dp(this, 40));
            if (on) t.setBackground(Ui.shape(this, 18, Ui.dark(this) ? 0x33FFFFFF : 0xFFFFFFFF, 0));
            int which = i;
            t.setOnClickListener(v -> { if (which != chosen) setAppearance(which); });
            seg.addView(t, new LinearLayout.LayoutParams(0, -2, 1));
        }
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
            + "locking, so bank apps keep working.", 12.5f, 400, Ui.faint(this));
        privacy.setPadding(Ui.dp(this, 4), Ui.dp(this, 4), Ui.dp(this, 4), 0);
        list.addView(privacy);

        ScrollView scroll = new ScrollView(this);
        scroll.setClipToPadding(false);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.addView(list);
        return scroll;
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

    private LinearLayout buildTabBar() {
        LinearLayout bar = new LinearLayout(this);
        bar.setBackground(Ui.shape(this, 32, Ui.dark(this) ? 0xE61A1B1E : 0xF2FFFFFF, Ui.line(this)));
        bar.setElevation(Ui.dp(this, 6));
        int p = Ui.dp(this, 6);
        bar.setPadding(p, p, p, p);
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
            bar.addView(t);
        }
        return bar;
    }

    // The outgoing page fades down a little while the new one rises into place.
    private void select(int which) {
        if (which == tab) return;
        View in = which == 0 ? feed : settings, out = which == 0 ? settings : feed;
        float shift = Ui.dp(this, 14);
        if (tab == -1) {
            out.setVisibility(View.GONE);
            in.setVisibility(View.VISIBLE);
        } else {
            out.animate().alpha(0f).translationY(shift).setStartDelay(0).setDuration(140)
                .withEndAction(() -> out.setVisibility(View.GONE));
            in.setAlpha(0f);
            in.setTranslationY(shift);
            in.setVisibility(View.VISIBLE);
            in.animate().alpha(1f).translationY(0).setStartDelay(60).setDuration(240);
        }
        tab = which;
        for (int i = 0; i < tabs.length; i++) {
            boolean on = i == which;
            tabs[i].setBackground(on ? Ui.shape(this, 26, Ui.dark(this) ? 0x26FFFFFF : 0x12000000, 0) : null);
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
