package com.taseen.tapoff;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.os.VibrationAttributes;
import android.os.VibrationEffect;
import android.os.VibratorManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import java.io.File;
import java.io.IOException;

// Brightness and volume sliders in the style of Quick Settings' brightness slider, in the wallpaper's Material You
// colours. Double-tap the back of the phone (Pixel's Quick Tap, opening SlidersActivity): they grow out of the camera
// hole and follow your finger; in landscape they sit in the middle of the screen. An overlay drawn from the
// wallpaper's process, which Android keeps running while TapOff is the wallpaper (needs "Display over other apps").
final class NotchPanel {
    static final String CHANGED = "com.taseen.tapoff.NOTCH_PANEL_CHANGED", OPEN = "com.taseen.tapoff.OPEN_SLIDERS";
    // Tuning knobs: slider size, and how long the sliders stay after the last touch.
    private static final float PILL_H_DP = 48, PILL_GAP_DP = 12, PILL_MAX_W_DP = 340;
    private static final long HIDE_AFTER_MS = 3000;
    private static final int BRIGHTNESS = 0, VOLUME = 1;

    // Off unless turned on. A marker file, so the app and the wallpaper process see changes at once.
    static boolean enabled(Context c) {
        return new File(c.getFilesDir(), "notch_panel_on").exists();
    }

    static void setEnabled(Context c, boolean on) {
        File f = new File(c.getFilesDir(), "notch_panel_on");
        try {
            if (on) f.createNewFile();
            else f.delete();
        } catch (IOException e) {
            Log.w("TapOff", "couldn't save the sliders switch", e);
        }
        changed(c);
    }

    // Tells the wallpaper process to update.
    static void changed(Context c) {
        c.sendBroadcast(new Intent(CHANGED).setPackage(c.getPackageName()));
    }

    // Asks the wallpaper process to show the sliders.
    static void requestOpen(Context c) {
        c.sendBroadcast(new Intent(OPEN).setPackage(c.getPackageName()));
    }

    static boolean allowed(Context c) {
        return Settings.canDrawOverlays(c);
    }

    private final Context c;
    private final WindowManager wm;
    private final AudioManager audio;
    private final float dp;
    private Panel panel;

    NotchPanel(Context c) {
        this.c = c;
        wm = c.getSystemService(WindowManager.class);
        audio = c.getSystemService(AudioManager.class);
        dp = c.getResources().getDisplayMetrics().density;
    }

    void sync() {
        if (!enabled(c) || !allowed(c)) remove();
    }

    // Also when the screen turns: the camera is somewhere else now.
    void remove() {
        if (panel != null) wm.removeView(panel);
        panel = null;
    }

    private Context display() {
        return c.createDisplayContext(c.getSystemService(DisplayManager.class).getDisplay(Display.DEFAULT_DISPLAY));
    }

    private RectF hole(Context dc) {
        RectF hole = CutoutArt.hole(dc);
        float mid = dc.getResources().getDisplayMetrics().widthPixels / 2f;
        return hole != null ? hole : new RectF(mid - 10 * dp, 12 * dp, mid + 10 * dp, 32 * dp);
    }

    private static int statusBar(Context dc) {
        int id = dc.getResources().getIdentifier("status_bar_height", "dimen", "android");
        DisplayCutout cut = dc.getDisplay().getCutout();
        return Math.max(id == 0 ? 0 : dc.getResources().getDimensionPixelSize(id), cut == null ? 0 : cut.getSafeInsetTop());
    }

    private static boolean portrait(Context dc) {
        return dc.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    // Touchable, so Android draws it fully opaque (it forces untouchable app overlays see-through).
    private static WindowManager.LayoutParams overlay(int w, int h) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(w, h,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT);
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
        return lp;
    }

    // The window covers just the sliders (and, in portrait, the way up to the camera they grow from), so the rest of
    // the screen keeps working; a touch anywhere else also puts them away.
    void open() {
        if (panel != null || !enabled(c) || !allowed(c)) return;
        Context dc = display();
        android.graphics.Point size = new android.graphics.Point();
        dc.getDisplay().getRealSize(size);
        panel = new Panel(dc, hole(dc), statusBar(dc), size.x, size.y);
        RectF f = panel.frame;
        WindowManager.LayoutParams lp = overlay(Math.round(f.width()), Math.round(f.height()));
        lp.flags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        lp.gravity = Gravity.TOP | Gravity.LEFT;
        lp.x = Math.round(f.left);
        lp.y = Math.round(f.top);
        lp.setTitle("TapOff sliders");
        try {
            wm.addView(panel, lp);
        } catch (RuntimeException e) {
            Log.w("TapOff", "couldn't show the sliders", e);
            panel = null;
            return;
        }
        panel.animateTo(1);
    }

    // The two sliders, drawn in the window's own coordinates.
    private final class Panel extends View {
        final RectF frame; // the window, on screen
        private final RectF from; // where they grow from: the camera, or in landscape the top centre of the screen
        private final int start; // their colour as they leave it: black like the camera hole, or clear in landscape
        private final int[] kind;
        private final float[] level;
        private final RectF[] target;
        private final Drawable[] icons;
        private final int track, fill;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF pill = new RectF();
        private final OvershootInterpolator overshoot = new OvershootInterpolator(1.2f);
        private final Runnable hide = () -> animateTo(0);
        private float t, goal;
        private ValueAnimator anim;
        private int dragging = -1;
        private boolean downOutside;

        Panel(Context dc, RectF hole, float statusBar, int w, int h) {
            super(dc);
            // Brightness writes a system setting, which needs the one-time setup; volume always works.
            kind = LockService.canLock(dc) ? new int[] {BRIGHTNESS, VOLUME} : new int[] {VOLUME};
            level = new float[kind.length];
            target = new RectF[kind.length];
            icons = new Drawable[kind.length];
            int iconColor = dc.getColor(android.R.color.system_accent1_800);
            for (int i = 0; i < kind.length; i++) {
                level[i] = read(kind[i]);
                target[i] = new RectF();
                icons[i] = dc.getDrawable(kind[i] == BRIGHTNESS ? R.drawable.ic_card_today : R.drawable.ic_volume).mutate();
                icons[i].setTint(iconColor);
            }
            track = dc.getColor(android.R.color.system_neutral1_800);
            fill = dc.getColor(android.R.color.system_accent1_200);

            // Portrait: stacked under the camera. Landscape: centred at the top of the screen (the camera is off to the
            // side), sliding down from the top edge and back up into it.
            float ph = PILL_H_DP * dp, gap = PILL_GAP_DP * dp, pw = Math.min(w - 48 * dp, PILL_MAX_W_DP * dp);
            float total = kind.length * ph + (kind.length - 1) * gap, left, top;
            boolean landscape = w > h;
            start = landscape ? 0x00000000 : 0xFF000000;
            if (landscape) {
                left = (w - pw) / 2;
                top = statusBar + 12 * dp;
            } else {
                left = Math.max(24 * dp, Math.min(w - 24 * dp - pw, hole.centerX() - pw / 2));
                top = Math.max(hole.bottom, statusBar) + 14 * dp;
            }
            frame = new RectF(left - 16 * dp, top - 8 * dp, left + pw + 16 * dp, top + total + 8 * dp); // room to overshoot
            if (landscape) {
                float r = hole.width() / 2, cx = w / 2f;
                from = new RectF(cx - r, -r, cx + r, r);
            } else {
                from = new RectF(hole);
            }
            frame.union(from);
            frame.top = Math.max(0, frame.top);
            from.offset(-frame.left, -frame.top);
            for (int i = 0; i < kind.length; i++) {
                target[i].set(left, top + i * (ph + gap), left + pw, top + i * (ph + gap) + ph);
                target[i].offset(-frame.left, -frame.top);
            }
        }

        // Out of the camera with a little overshoot (each slider a moment after the one above), back in faster.
        void animateTo(float to) {
            removeCallbacks(hide);
            if (to == 1) postDelayed(hide, HIDE_AFTER_MS);
            if (to == goal && anim != null) return;
            goal = to;
            if (anim != null) anim.cancel();
            anim = ValueAnimator.ofFloat(t, to);
            anim.setDuration(to == 1 ? 480 : 320);
            anim.setInterpolator(new LinearInterpolator());
            anim.addUpdateListener(a -> {
                t = (float) a.getAnimatedValue();
                invalidate();
            });
            anim.addListener(new android.animation.AnimatorListenerAdapter() {
                @Override public void onAnimationEnd(android.animation.Animator a) {
                    if (goal == 0 && panel == Panel.this) {
                        wm.removeView(Panel.this);
                        panel = null;
                    }
                }
            });
            anim.start();
        }

        @Override protected void onDraw(Canvas canvas) {
            float stagger = 0.15f;
            for (int i = 0; i < kind.length; i++) {
                float p = Math.max(0, Math.min(1, (t - i * stagger) / (1 - stagger * (kind.length - 1))));
                float e = goal == 1 ? overshoot.getInterpolation(p) : p * p * (3 - 2 * p), m = Math.min(1, e);
                RectF to = target[i];
                float w = from.width() + (to.width() - from.width()) * e, h = from.height() + (to.height() - from.height()) * m;
                float cx = from.centerX() + (to.centerX() - from.centerX()) * m;
                float cy = from.centerY() + (to.centerY() - from.centerY()) * m;
                pill.set(cx - w / 2, cy - h / 2, cx + w / 2, cy + h / 2);
                float fade = start == 0 ? e / 0.4f : (e - 0.25f) / 0.5f; // clear ones fade in from the start
                drawPill(canvas, i, Math.max(0, Math.min(1, fade)));
            }
        }

        // Black while it leaves the camera, so it looks like the hole itself stretching; then the colours come in.
        // In landscape they come from the top edge instead, fading in as they slide down.
        private void drawPill(Canvas canvas, int i, float show) {
            float r = pill.height() / 2;
            paint.setColor(Ui.blend(start, track, show));
            canvas.drawRoundRect(pill, r, r, paint);
            if (show == 0) return;
            canvas.save();
            canvas.clipRect(pill);
            paint.setColor(Ui.blend(start, fill, show));
            float right = pill.left + pill.height() + (pill.width() - pill.height()) * level[i]; // never less than a circle
            canvas.drawRoundRect(pill.left, pill.top, right, pill.bottom, r, r, paint);
            canvas.restore();
            int half = Math.round(11 * dp), x = Math.round(pill.left + r), y = Math.round(pill.centerY());
            icons[i].setBounds(x - half, y - half, x + half, y + half);
            icons[i].setAlpha(Math.round(255 * show));
            icons[i].draw(canvas);
        }

        @Override public boolean onTouchEvent(MotionEvent e) {
            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_OUTSIDE: // a touch elsewhere, which goes on to the app as normal
                    animateTo(0);
                    break;
                case MotionEvent.ACTION_DOWN:
                    removeCallbacks(hide);
                    dragging = t > 0.9f ? hit(e.getX(), e.getY()) : -1;
                    downOutside = dragging < 0;
                    if (dragging >= 0) set(dragging, e.getX());
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (dragging >= 0) set(dragging, e.getX());
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (downOutside) animateTo(0);
                    else postDelayed(hide, HIDE_AFTER_MS);
                    dragging = -1;
                    break;
            }
            return true;
        }

        private int hit(float x, float y) {
            for (int i = 0; i < kind.length; i++) {
                RectF to = target[i];
                if (x >= to.left && x <= to.right && y >= to.top - 8 * dp && y <= to.bottom + 8 * dp) return i;
            }
            return -1;
        }

        // Like the Quick Settings slider: the level jumps to your finger and follows it.
        private void set(int i, float x) {
            RectF to = target[i];
            float p = Math.max(0, Math.min(1, (x - to.left - to.height() / 2) / (to.width() - to.height())));
            level[i] = p;
            write(kind[i], p);
            invalidate();
        }
    }

    private float read(int kind) {
        if (kind == VOLUME) {
            int stream = stream();
            return (float) audio.getStreamVolume(stream) / audio.getStreamMaxVolume(stream);
        }
        return toPosition((Settings.System.getInt(c.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 128) - 1) / 254f);
    }

    private void write(int kind, float p) {
        if (kind == VOLUME) {
            int stream = stream(), index = Math.round(p * audio.getStreamMaxVolume(stream));
            if (index == audio.getStreamVolume(stream)) return;
            audio.setStreamVolume(stream, index, 0);
            c.getSystemService(VibratorManager.class).getDefaultVibrator().vibrate(
                VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK),
                VibrationAttributes.createForUsage(VibrationAttributes.USAGE_TOUCH));
            return;
        }
        // The same setting as the system slider, so adaptive brightness stays on and keeps the level picked.
        try {
            Settings.System.putInt(c.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, Math.round(1 + toLinear(p) * 254));
        } catch (SecurityException e) {
            Log.w("TapOff", "brightness permission missing", e);
        }
    }

    // Media volume, or call volume during a call: what Pixel's buttons change by default.
    private int stream() {
        int mode = audio.getMode();
        return mode == AudioManager.MODE_IN_CALL || mode == AudioManager.MODE_IN_COMMUNICATION
            ? AudioManager.STREAM_VOICE_CALL : AudioManager.STREAM_MUSIC;
    }

    // Android's own brightness slider curve (Hybrid Log-Gamma): slider position 0..1 to share of full brightness, and
    // back. Half-way on the slider is about 8% of full brightness, which is how eyes see it.
    private static final float HLG_R = 0.5f, HLG_A = 0.17883277f, HLG_B = 0.28466892f, HLG_C = 0.55991073f;

    static float toLinear(float pos) {
        return (pos <= HLG_R ? (pos / HLG_R) * (pos / HLG_R) : (float) Math.exp((pos - HLG_C) / HLG_A) + HLG_B) / 12;
    }

    static float toPosition(float linear) {
        float v = Math.max(0, linear) * 12;
        return v <= 1 ? HLG_R * (float) Math.sqrt(v) : HLG_A * (float) Math.log(v - HLG_B) + HLG_C;
    }
}
