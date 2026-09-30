package com.taseen.tapoff;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.VibrationAttributes;
import android.os.VibrationEffect;
import android.os.VibratorManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import java.io.File;
import java.io.IOException;

// Slide up or down along the left edge of the screen to change the volume, like in VLC. An invisible strip drawn
// over other apps; it lives in the wallpaper's process, which Android keeps running while TapOff is the wallpaper.
final class EdgeVolume {
    static final String CHANGED = "com.taseen.tapoff.EDGE_VOLUME_CHANGED";
    // Tuning knobs: strip width, how much of the screen height it covers, and finger travel per volume step.
    private static final float WIDTH_DP = 14, HEIGHT_SHARE = 0.6f, STEP_DP = 28;

    // Off unless turned on. A marker file, so the app and the wallpaper process both see changes at once.
    static boolean enabled(Context c) {
        return new File(c.getFilesDir(), "edge_volume_on").exists();
    }

    static void setEnabled(Context c, boolean on) {
        File f = new File(c.getFilesDir(), "edge_volume_on");
        try {
            if (on) f.createNewFile();
            else f.delete();
        } catch (IOException e) {
            Log.w("TapOff", "couldn't save the edge volume switch", e);
        }
        changed(c);
    }

    // Tells the wallpaper process to add or remove the strip.
    static void changed(Context c) {
        c.sendBroadcast(new Intent(CHANGED).setPackage(c.getPackageName()));
    }

    static boolean allowed(Context c) {
        return Settings.canDrawOverlays(c);
    }

    private final Context c;
    private final WindowManager wm;
    private final AudioManager audio;
    private final float step;
    private View strip;
    private float lastY;

    EdgeVolume(Context c) {
        this.c = c;
        wm = c.getSystemService(WindowManager.class);
        audio = c.getSystemService(AudioManager.class);
        step = STEP_DP * c.getResources().getDisplayMetrics().density;
    }

    void sync() {
        boolean want = enabled(c) && allowed(c);
        if (want && strip == null) add();
        else if (!want) remove();
    }

    void remove() {
        if (strip == null) return;
        wm.removeView(strip);
        strip = null;
    }

    private void add() {
        float dp = c.getResources().getDisplayMetrics().density;
        int height = Math.round(c.getResources().getDisplayMetrics().heightPixels * HEIGHT_SHARE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(Math.round(WIDTH_DP * dp), height,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        lp.setTitle("TapOff edge volume");
        strip = new View(c);
        strip.setOnTouchListener((v, e) -> onTouch(e));
        try {
            wm.addView(strip, lp);
        } catch (RuntimeException e) { // permission revoked between the check and now
            Log.w("TapOff", "couldn't add the edge volume strip", e);
            strip = null;
        }
    }

    // Each STEP_DP of travel is one volume step, the same one the hardware buttons would take. A sideways swipe
    // is the back gesture: the system takes it over and this just sees the touch cancelled.
    private boolean onTouch(MotionEvent e) {
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastY = e.getRawY();
                return true;
            case MotionEvent.ACTION_MOVE:
                float moved = lastY - e.getRawY();
                while (moved >= step) {
                    adjust(AudioManager.ADJUST_RAISE);
                    lastY -= step;
                    moved -= step;
                }
                while (moved <= -step) {
                    adjust(AudioManager.ADJUST_LOWER);
                    lastY += step;
                    moved += step;
                }
                return true;
            default:
                return true;
        }
    }

    private void adjust(int direction) {
        audio.adjustSuggestedStreamVolume(direction, AudioManager.USE_DEFAULT_STREAM_TYPE, AudioManager.FLAG_SHOW_UI);
        c.getSystemService(VibratorManager.class).getDefaultVibrator().vibrate(
            VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK),
            VibrationAttributes.createForUsage(VibrationAttributes.USAGE_TOUCH));
    }
}
