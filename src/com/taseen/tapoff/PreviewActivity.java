package com.taseen.tapoff;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Insets;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Full-screen look at a wallpaper before setting it. Drag sideways through its collection: the next one slides
// in over the current, which drifts back and dims. Tap to hide the controls.
public class PreviewActivity extends Activity {
    private static final long SETTLE_MS = 280;
    private final ExecutorService bg = Executors.newFixedThreadPool(2);
    private final List<Wallpapers.Item> items = new ArrayList<>();
    // Decoded wallpapers for the current one and its two neighbours.
    private final Map<Integer, Bitmap> bitmaps = new HashMap<>();
    private final Set<Integer> loading = new HashSet<>();
    private int index, peekFor = -1;
    private ImageView cur, peek;
    private ProgressBar spinner;
    private TextView counter, title, credit;
    private Button set;
    private View top, bottom;
    private int screenW, screenH, slop;
    private float downX, dx;
    private boolean dragging, animating;
    private VelocityTracker vt;

    static Intent intent(Context c, Wallpapers.Collection col, int index) {
        int n = col.items.size();
        String[] titles = new String[n], fulls = new String[n], credits = new String[n];
        int[] res = new int[n];
        for (int i = 0; i < n; i++) {
            Wallpapers.Item it = col.items.get(i);
            titles[i] = it.title;
            fulls[i] = it.full;
            credits[i] = it.credit;
            res[i] = it.res;
        }
        return new Intent(c, PreviewActivity.class).putExtra("titles", titles).putExtra("fulls", fulls)
            .putExtra("credits", credits).putExtra("res", res).putExtra("index", index);
    }

    @Override protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        Intent in = getIntent();
        String[] titles = in.getStringArrayExtra("titles"), fulls = in.getStringArrayExtra("fulls"),
            credits = in.getStringArrayExtra("credits");
        int[] res = in.getIntArrayExtra("res");
        for (int i = 0; i < titles.length; i++) items.add(new Wallpapers.Item(titles[i], fulls[i], null, credits[i], res[i]));
        screenW = getWindowManager().getMaximumWindowMetrics().getBounds().width();
        screenH = getWindowManager().getMaximumWindowMetrics().getBounds().height();
        slop = ViewConfiguration.get(this).getScaledTouchSlop();

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Ui.PHOTO_BG);
        cur = image(root);
        peek = image(root); // added second, so it slides in on top
        peek.setVisibility(View.INVISIBLE);
        spinner = new ProgressBar(this);
        root.addView(spinner, new FrameLayout.LayoutParams(Ui.dp(this, 40), Ui.dp(this, 40), Gravity.CENTER));

        LinearLayout topBar = new LinearLayout(this);
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        ImageButton back = new ImageButton(this);
        back.setImageResource(R.drawable.ic_back);
        back.setBackground(Ui.glass(this, 22, 0x33000000));
        back.setContentDescription("Back");
        back.setOnClickListener(v -> finish());
        topBar.addView(back, new LinearLayout.LayoutParams(Ui.dp(this, 44), Ui.dp(this, 44)));
        topBar.addView(new View(this), new LinearLayout.LayoutParams(0, 1, 1));
        counter = Ui.text(this, "", 13, 500, 0xFFFFFFFF);
        counter.setBackground(Ui.glass(this, 16, 0x33000000));
        counter.setPadding(Ui.dp(this, 14), Ui.dp(this, 6), Ui.dp(this, 14), Ui.dp(this, 6));
        topBar.addView(counter);
        top = topBar;
        root.addView(topBar, new FrameLayout.LayoutParams(-1, -2, Gravity.TOP));

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setBackground(Ui.glass(this, 28, 0x8C0A0A0B));
        int pad = Ui.dp(this, 20);
        panel.setPadding(pad, pad, pad, pad);
        title = Ui.text(this, "", 22, 700, 0xFFFFFFFF);
        credit = Ui.text(this, "", 12.5f, 400, 0xB3FFFFFF);
        credit.setMaxLines(2);
        credit.setPadding(0, Ui.dp(this, 4), 0, 0);
        set = Ui.pill(this, "Set wallpaper", Ui.Pill.ON_PHOTO);
        set.setOnClickListener(v -> apply());
        panel.addView(title);
        panel.addView(credit);
        LinearLayout.LayoutParams setLp = new LinearLayout.LayoutParams(-1, -2);
        setLp.topMargin = Ui.dp(this, 16);
        panel.addView(set, setLp);
        bottom = panel;
        root.addView(panel, new FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM));

        // A hand swipes across once or twice, then fades away.
        if (items.size() > 1) {
            SwipeHint[] hint = new SwipeHint[1];
            hint[0] = new SwipeHint(this, () -> hint[0].animate().alpha(0f).setDuration(400)
                .withEndAction(() -> root.removeView(hint[0])));
            root.addView(hint[0], new FrameLayout.LayoutParams(Ui.dp(this, 220), Ui.dp(this, 90), Gravity.CENTER));
        }

        root.setOnApplyWindowInsetsListener((v, insets) -> {
            Insets bars = insets.getInsets(WindowInsets.Type.systemBars());
            int m = Ui.dp(this, 16);
            ((FrameLayout.LayoutParams) topBar.getLayoutParams()).setMargins(m, bars.top + Ui.dp(this, 8), m, 0);
            ((FrameLayout.LayoutParams) panel.getLayoutParams()).setMargins(m, 0, m, bars.bottom + m);
            topBar.requestLayout();
            return insets;
        });
        root.setOnTouchListener((v, e) -> onTouch(e));
        setContentView(root);
        // Light status bar icons over the photo, whatever the app's theme.
        getWindow().getInsetsController().setSystemBarsAppearance(0,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS);
        show(in.getIntExtra("index", 0));
    }

    private ImageView image(FrameLayout root) {
        ImageView v = new ImageView(this);
        v.setScaleType(ImageView.ScaleType.CENTER_CROP);
        v.setBackgroundColor(Ui.PHOTO_BG);
        root.addView(v, new FrameLayout.LayoutParams(-1, -1));
        return v;
    }

    private boolean onTouch(MotionEvent e) {
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (animating) return true;
                downX = e.getX();
                dx = 0;
                dragging = false;
                vt = VelocityTracker.obtain();
                vt.addMovement(e);
                return true;
            case MotionEvent.ACTION_MOVE:
                if (vt == null) return true;
                vt.addMovement(e);
                dx = e.getX() - downX;
                if (!dragging && Math.abs(dx) > slop) dragging = true;
                if (dragging) drag(dx);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (vt == null) return true;
                vt.addMovement(e);
                vt.computeCurrentVelocity(1000);
                float vx = vt.getXVelocity();
                vt.recycle();
                vt = null;
                if (dragging) settle(dx, vx);
                else if (e.getActionMasked() == MotionEvent.ACTION_UP) toggleControls();
                return true;
            default:
                return false;
        }
    }

    private void toggleControls() {
        float a = top.getAlpha() > 0.5f ? 0f : 1f;
        top.animate().alpha(a).setDuration(180);
        bottom.animate().alpha(a).setDuration(180);
    }

    // Follows the finger: the neighbour slides in over the current one, which drifts a third as far and dims.
    private void drag(float dx) {
        int dir = dx < 0 ? 1 : -1, next = index + dir;
        boolean has = next >= 0 && next < items.size();
        if (!has) { // rubber band at either end
            cur.setTranslationX(dx * 0.25f);
            peek.setVisibility(View.INVISIBLE);
            return;
        }
        cur.setTranslationX(dx * 0.3f);
        cur.setAlpha(1 - 0.4f * Math.abs(dx) / screenW);
        if (peekFor != next) {
            peekFor = next;
            peek.setImageBitmap(bitmaps.get(next));
        }
        peek.setVisibility(View.VISIBLE);
        peek.setTranslationX(dx + dir * screenW);
    }

    private void settle(float dx, float vx) {
        int dir = dx < 0 ? 1 : -1, next = index + dir;
        boolean has = next >= 0 && next < items.size();
        boolean go = has && (Math.abs(dx) > screenW * 0.25f || (Math.abs(vx) > 800 && Math.signum(vx) == Math.signum(dx)));
        animating = true;
        DecelerateInterpolator ease = new DecelerateInterpolator(1.6f);
        if (go) {
            peek.animate().translationX(0).setDuration(SETTLE_MS).setInterpolator(ease);
            cur.animate().translationX(-dir * screenW * 0.3f).alpha(0.6f).setDuration(SETTLE_MS).setInterpolator(ease)
                .withEndAction(() -> {
                    show(next); // puts the new wallpaper in cur before peek is hidden, so nothing flickers
                    cur.setTranslationX(0);
                    cur.setAlpha(1f);
                    peek.setVisibility(View.INVISIBLE);
                    peekFor = -1;
                    animating = false;
                });
        } else {
            cur.animate().translationX(0).alpha(1f).setDuration(SETTLE_MS).setInterpolator(ease)
                .withEndAction(() -> animating = false);
            if (has) peek.animate().translationX(dir * screenW).setDuration(SETTLE_MS).setInterpolator(ease)
                .withEndAction(() -> peek.setVisibility(View.INVISIBLE));
        }
    }

    private void show(int i) {
        index = i;
        Wallpapers.Item it = items.get(i);
        counter.setText((i + 1) + " / " + items.size());
        counter.setVisibility(items.size() > 1 ? View.VISIBLE : View.GONE);
        title.setText(it.title);
        credit.setText(it.credit);
        credit.setVisibility(it.credit.isEmpty() ? View.GONE : View.VISIBLE);
        Bitmap b = bitmaps.get(i);
        cur.setImageBitmap(b);
        spinner.setVisibility(b == null ? View.VISIBLE : View.GONE);
        set.setEnabled(b != null);
        bitmaps.keySet().removeIf(k -> Math.abs(k - i) > 1);
        load(i);
        load(i + 1);
        load(i - 1);
    }

    private void load(int j) {
        if (j < 0 || j >= items.size() || bitmaps.containsKey(j) || !loading.add(j)) return;
        Wallpapers.Item it = items.get(j);
        bg.execute(() -> {
            try {
                Bitmap b = Wallpapers.decode(this, it, false, screenW, screenH);
                runOnUiThread(() -> {
                    loading.remove(j);
                    if (Math.abs(j - index) > 1) return; // swiped away meanwhile
                    bitmaps.put(j, b);
                    if (j == index) {
                        cur.setImageBitmap(b);
                        spinner.setVisibility(View.GONE);
                        set.setEnabled(true);
                    }
                    if (j == peekFor) peek.setImageBitmap(b);
                });
            } catch (Exception e) {
                Log.w("TapOff", "couldn't load wallpaper " + it.full, e);
                runOnUiThread(() -> {
                    loading.remove(j);
                    if (j != index) return;
                    spinner.setVisibility(View.GONE);
                    Toast.makeText(this, "Couldn't load this one. Check your connection.", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void apply() {
        Bitmap b = bitmaps.get(index);
        if (b == null) return;
        set.setEnabled(false);
        set.setText("Setting…");
        bg.execute(() -> {
            boolean ok;
            try {
                Wallpapers.apply(this, b);
                ok = true;
            } catch (Exception e) {
                Log.w("TapOff", "couldn't set wallpaper", e);
                ok = false;
            }
            boolean done = ok;
            runOnUiThread(() -> {
                set.setText("Set wallpaper");
                set.setEnabled(true);
                if (!done) {
                    Toast.makeText(this, "Couldn't set this wallpaper", Toast.LENGTH_LONG).show();
                } else if (!Wallpapers.isActive(this)) {
                    startActivity(MainActivity.chooser(this)); // home screen still needs TapOff as its wallpaper
                } else {
                    Toast.makeText(this, "Set on your home and lock screen", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    @Override protected void onDestroy() {
        bg.shutdownNow();
        super.onDestroy();
    }
}
