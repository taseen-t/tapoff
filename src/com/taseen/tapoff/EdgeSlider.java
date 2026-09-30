package com.taseen.tapoff;

import android.content.Context;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.display.DisplayManager;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.VibrationAttributes;
import android.os.VibrationEffect;
import android.os.VibratorManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import java.io.File;
import java.io.IOException;

// Slide up or down along a screen edge, like in VLC: the left edge for volume, the right edge for brightness.
// Invisible strips drawn over other apps; they live in the wallpaper's process, which Android keeps running while
// TapOff is the wallpaper.
final class EdgeSlider {
    static final String CHANGED = "com.taseen.tapoff.EDGE_SLIDERS_CHANGED";
    static final int VOLUME = 0, BRIGHTNESS = 1;
    private static final String[] MARKER = {"edge_volume_on", "edge_brightness_on"};
    // Tuning knobs: strip width, how much of the screen height it covers, finger travel per volume step, and finger
    // travel for the whole brightness range.
    private static final float WIDTH_DP = 14, HEIGHT_SHARE = 0.6f, STEP_DP = 28, BRIGHTNESS_RANGE_DP = 320;
    private static final long LEVEL_HOLD_MS = 900; // how long the brightness level stays after you let go

    // Off unless turned on. Marker files, so the app and the wallpaper process both see changes at once.
    static boolean enabled(Context c, int kind) {
        return new File(c.getFilesDir(), MARKER[kind]).exists();
    }

    static void setEnabled(Context c, int kind, boolean on) {
        File f = new File(c.getFilesDir(), MARKER[kind]);
        try {
            if (on) f.createNewFile();
            else f.delete();
        } catch (IOException e) {
            Log.w("TapOff", "couldn't save an edge slider switch", e);
        }
        changed(c);
    }

    // Tells the wallpaper process to add or remove the strips.
    static void changed(Context c) {
        c.sendBroadcast(new Intent(CHANGED).setPackage(c.getPackageName()));
    }

    // Both draw over other apps. Brightness also writes a system setting, which the one-time setup's permission covers.
    static boolean allowed(Context c, int kind) {
        return Settings.canDrawOverlays(c) && (kind == VOLUME || LockService.canLock(c));
    }

    private final Context c;
    private final int kind;
    private final WindowManager wm;
    private final AudioManager audio;
    private final float dp;
    private View strip;
    private Notch notch;
    private final Runnable collapse = () -> { if (notch != null) notch.animateTo(0); };
    private boolean sliding;
    private float lastY, startY, startPos;

    EdgeSlider(Context c, int kind) {
        this.c = c;
        this.kind = kind;
        wm = c.getSystemService(WindowManager.class);
        audio = c.getSystemService(AudioManager.class);
        dp = c.getResources().getDisplayMetrics().density;
    }

    void sync() {
        boolean want = enabled(c, kind) && allowed(c, kind);
        if (want && strip == null) add();
        else if (!want) remove();
    }

    void remove() {
        if (notch != null) wm.removeView(notch);
        notch = null;
        if (strip == null) return;
        strip.removeCallbacks(collapse);
        wm.removeView(strip);
        strip = null;
    }

    private void add() {
        int height = Math.round(c.getResources().getDisplayMetrics().heightPixels * HEIGHT_SHARE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(Math.round(WIDTH_DP * dp), height,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT);
        lp.gravity = (kind == VOLUME ? Gravity.START : Gravity.END) | Gravity.CENTER_VERTICAL;
        lp.setTitle(kind == VOLUME ? "TapOff edge volume" : "TapOff edge brightness");
        strip = new View(c);
        strip.setOnTouchListener((v, e) -> kind == VOLUME ? onVolumeTouch(e) : onBrightnessTouch(e));
        try {
            wm.addView(strip, lp);
        } catch (RuntimeException e) { // permission revoked between the check and now
            Log.w("TapOff", "couldn't add an edge slider", e);
            strip = null;
        }
    }

    // Each STEP_DP of travel is one volume step. A sideways swipe is the back gesture: the system takes it over and
    // this just sees the touch cancelled.
    private boolean onVolumeTouch(MotionEvent e) {
        float step = STEP_DP * dp;
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastY = e.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moved = lastY - e.getRawY();
                while (moved >= step) {
                    adjustVolume(AudioManager.ADJUST_RAISE);
                    lastY -= step;
                    moved -= step;
                }
                while (moved <= -step) {
                    adjustVolume(AudioManager.ADJUST_LOWER);
                    lastY += step;
                    moved += step;
                }
                break;
        }
        return true;
    }

    // Media volume, or call volume during a call: what Pixel's buttons change by default. Not
    // adjustSuggestedStreamVolume, which ignores the first half-second of a slide while nothing is playing.
    private void adjustVolume(int direction) {
        int mode = audio.getMode();
        int stream = mode == AudioManager.MODE_IN_CALL || mode == AudioManager.MODE_IN_COMMUNICATION
            ? AudioManager.STREAM_VOICE_CALL : AudioManager.STREAM_MUSIC;
        audio.adjustStreamVolume(stream, direction, AudioManager.FLAG_SHOW_UI);
        c.getSystemService(VibratorManager.class).getDefaultVibrator().vibrate(
            VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK),
            VibrationAttributes.createForUsage(VibrationAttributes.USAGE_TOUCH));
    }

    // Brightness follows the finger smoothly. It writes the same setting as the system slider, so with adaptive
    // brightness on it also teaches adaptive brightness, just like dragging the slider in Quick Settings.
    private boolean onBrightnessTouch(MotionEvent e) {
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                startY = e.getRawY();
                startPos = toPosition((Settings.System.getInt(c.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, 128) - 1) / 254f);
                sliding = false;
                strip.removeCallbacks(collapse);
                break;
            case MotionEvent.ACTION_MOVE:
                if (!sliding && Math.abs(startY - e.getRawY()) < 8 * dp) break; // not a slide yet
                float pos = Math.max(0, Math.min(1, startPos + (startY - e.getRawY()) / (BRIGHTNESS_RANGE_DP * dp)));
                try {
                    Settings.System.putInt(c.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
                        Math.round(1 + toLinear(pos) * 254));
                } catch (SecurityException ex) {
                    Log.w("TapOff", "brightness permission missing", ex);
                    break;
                }
                sliding = true;
                showLevel(pos);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (sliding) strip.postDelayed(collapse, LEVEL_HOLD_MS);
                break;
        }
        return true;
    }

    // The level shows in a pill that grows out of the camera hole, styled like the Quick Settings brightness slider in
    // the wallpaper's Material You colours, then shrinks back into the hole. (Android's own brightness panel can't be
    // animated from here, and opening it would pause the app underneath.)
    private void showLevel(float pos) {
        if (notch == null) {
            notch = new Notch(c.createDisplayContext(
                c.getSystemService(DisplayManager.class).getDisplay(Display.DEFAULT_DISPLAY)));
            try {
                wm.addView(notch, notch.params());
            } catch (RuntimeException e) {
                notch = null;
                return;
            }
        }
        notch.pos = pos;
        notch.animateTo(1);
        notch.invalidate();
    }

    private final class Notch extends View {
        float pos;
        private float t, target; // 0: a dot hidden in the camera hole; 1: the full slider
        private ValueAnimator anim;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF pill = new RectF();
        private final float cx, cy, r, w, h, top;
        private final int track, fill, icon;

        Notch(Context dc) {
            super(dc);
            int screenW = dc.getResources().getDisplayMetrics().widthPixels;
            RectF hole = CutoutArt.hole(dc);
            if (hole == null) hole = new RectF(screenW / 2f - 10 * dp, 12 * dp, screenW / 2f + 10 * dp, 32 * dp);
            cx = hole.centerX();
            cy = hole.centerY();
            r = hole.width() / 2;
            w = Math.min(screenW - 48 * dp, 320 * dp);
            h = 44 * dp;
            top = hole.bottom + 10 * dp;
            track = dc.getColor(android.R.color.system_neutral1_800);
            fill = dc.getColor(android.R.color.system_accent1_200);
            icon = dc.getColor(android.R.color.system_accent1_800);
        }

        // A window just big enough for the full pill. Touchable, so Android draws it fully opaque; it only eats taps
        // in that small spot while it's showing.
        WindowManager.LayoutParams params() {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams(Math.round(w + 24 * dp), Math.round(top + h + 4 * dp),
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
            lp.gravity = Gravity.TOP | Gravity.LEFT;
            lp.x = Math.round(cx - (w + 24 * dp) / 2);
            lp.y = 0;
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
            lp.setTitle("TapOff brightness level");
            return lp;
        }

        // Out of the hole with a little overshoot; back in faster. Gone once it's back in.
        void animateTo(float to) {
            if (to == target) return;
            target = to;
            if (anim != null) anim.cancel();
            anim = ValueAnimator.ofFloat(t, to);
            anim.setDuration(to == 1 ? 320 : 300);
            anim.setInterpolator(to == 1 ? new OvershootInterpolator(1.2f)
                : new android.view.animation.PathInterpolator(0.3f, 0f, 0.8f, 0.15f));
            anim.addUpdateListener(a -> {
                t = (float) a.getAnimatedValue();
                invalidate();
            });
            anim.addListener(new android.animation.AnimatorListenerAdapter() {
                @Override public void onAnimationEnd(android.animation.Animator a) {
                    if (target == 0 && notch == Notch.this) {
                        wm.removeView(Notch.this);
                        notch = null;
                    }
                }
            });
            anim.start();
        }

        @Override protected void onDraw(Canvas canvas) {
            float left = getWidth() / 2f; // the window is centred on the camera
            float k = Math.max(0, t), pw = 2 * r + (w - 2 * r) * k, ph = 2 * r + (h - 2 * r) * Math.min(1, k);
            float midY = cy + (top + h / 2 - cy) * Math.min(1, k);
            pill.set(left - pw / 2, midY - ph / 2, left + pw / 2, midY + ph / 2);
            float round = ph / 2;
            // Black while it's leaving the camera, so it looks like the hole itself is stretching.
            float show = Math.max(0, Math.min(1, (k - 0.25f) / 0.5f));
            paint.setColor(Ui.blend(0xFF000000, track, show));
            canvas.drawRoundRect(pill, round, round, paint);
            if (show == 0) return;
            canvas.save();
            canvas.clipRect(pill);
            paint.setColor(Ui.blend(0xFF000000, fill, show));
            float fillRight = pill.left + ph + (pill.width() - ph) * pos; // never less than a circle, like Android's
            canvas.drawRoundRect(pill.left, pill.top, fillRight, pill.bottom, round, round, paint);
            canvas.restore();
            // The sun, inside the filled end.
            float sx = pill.left + ph / 2, sy = midY, d = dp;
            paint.setColor(Ui.blend(0x00000000, icon, show));
            canvas.drawCircle(sx, sy, 4.5f * d, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2 * d);
            paint.setStrokeCap(Paint.Cap.ROUND);
            for (int i = 0; i < 8; i++) {
                double a = i * Math.PI / 4;
                float cos = (float) Math.cos(a), sin = (float) Math.sin(a);
                canvas.drawLine(sx + cos * 7.5f * d, sy + sin * 7.5f * d, sx + cos * 10 * d, sy + sin * 10 * d, paint);
            }
            paint.setStyle(Paint.Style.FILL);
        }
    }

    // Android's own brightness slider curve (Hybrid Log-Gamma): slider position 0..1 to share of full brightness, and
    // back. Half-way on the slider is about 8% of full brightness, which is how eyes see it.
    private static final float R = 0.5f, A = 0.17883277f, B = 0.28466892f, C = 0.55991073f;

    static float toLinear(float pos) {
        return (pos <= R ? (pos / R) * (pos / R) : (float) Math.exp((pos - C) / A) + B) / 12;
    }

    static float toPosition(float linear) {
        float v = Math.max(0, linear) * 12;
        return v <= 1 ? R * (float) Math.sqrt(v) : A * (float) Math.log(v - B) + C;
    }
}
