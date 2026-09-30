package com.taseen.tapoff;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

// Bank apps refuse to run while any accessibility service is on, so this one is switched on only
// for the moment of locking and switches itself off right after. Locking through accessibility keeps
// fingerprint unlock; device-admin lockNow() would force the PIN every time.
public class LockService extends AccessibilityService {
    // Tuning knobs: how long the darkness takes to close in, and how soft its edge is.
    static final long CLOSE_MS = 420;
    static final float EDGE_DP = 70;
    // How long the black stays up, covering the system's lock-screen flash, before this service leaves.
    static final long HOLD_MS = 1000;

    @Override protected void onServiceConnected() {
        // The system lock action flashes the lock screen and home screen on its way off, so darken the screen
        // first and lock underneath it: the darkness closes in on the spot that was double-tapped, like an iris.
        // An accessibility overlay needs no extra permission.
        WindowManager wm = getSystemService(WindowManager.class);
        float[] at = tapPoint(this);
        Iris black = new Iris(this, at[0], at[1]);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT);
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
        lp.setFitInsetsTypes(0);
        wm.addView(black, lp);
        black.close(() -> {
            performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN);
            black.postDelayed(() -> {
                wm.removeView(black);
                disableSelf();
            }, HOLD_MS);
        });
    }

    // Black everywhere outside a soft-edged circle that shrinks to the tap point until the whole screen is dark.
    private static final class Iris extends View {
        private final Paint paint = new Paint();
        private float cx, cy, outer = -1;

        Iris(Context c, float x, float y) {
            super(c);
            cx = x;
            cy = y;
        }

        void close(Runnable done) {
            post(() -> { // once laid out, so the size is known
                if (cx < 0) { // no tap point (the Quick Settings tile): close on the middle
                    cx = getWidth() / 2f;
                    cy = getHeight() / 2f;
                }
                float edge = EDGE_DP * getResources().getDisplayMetrics().density;
                float far = (float) Math.hypot(Math.max(cx, getWidth() - cx), Math.max(cy, getHeight() - cy)) + edge;
                android.animation.ValueAnimator a = android.animation.ValueAnimator.ofFloat(far, 0);
                a.setDuration(CLOSE_MS);
                a.setInterpolator(new android.view.animation.PathInterpolator(0.3f, 0f, 0.2f, 1f));
                a.addUpdateListener(v -> {
                    outer = (float) v.getAnimatedValue();
                    invalidate();
                });
                a.addListener(new android.animation.AnimatorListenerAdapter() {
                    @Override public void onAnimationEnd(android.animation.Animator anim) { done.run(); }
                });
                a.start();
            });
        }

        @Override protected void onDraw(Canvas c) {
            if (outer < 0) return;
            if (outer < 1) {
                c.drawColor(0xFF000000);
                return;
            }
            float edge = EDGE_DP * getResources().getDisplayMetrics().density, inner = Math.max(0, outer - edge);
            paint.setShader(new RadialGradient(cx, cy, outer, new int[] {0, 0, 0xFF000000},
                new float[] {0, inner / outer, 1}, Shader.TileMode.CLAMP));
            c.drawRect(0, 0, getWidth(), getHeight(), paint);
        }
    }

    // Where the home screen was double-tapped, handed over from the wallpaper's process in a small file.
    private static File tapFile(Context c) {
        return new File(c.getFilesDir(), "tap_point");
    }

    private static float[] tapPoint(Context c) {
        try {
            String[] p = new String(Files.readAllBytes(tapFile(c).toPath()), StandardCharsets.UTF_8).trim().split(" ");
            return new float[] {Float.parseFloat(p[0]), Float.parseFloat(p[1])};
        } catch (IOException | RuntimeException e) {
            return new float[] {-1, -1};
        }
    }

    @Override public void onAccessibilityEvent(AccessibilityEvent event) {}

    @Override public void onInterrupt() {}

    // WRITE_SECURE_SETTINGS is granted once over adb by `build.sh --install`.
    static boolean canLock(Context c) {
        return c.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
    }

    // x, y: where the double-tap was, or -1, -1 for none.
    static void lock(Context c, float x, float y) {
        try {
            Files.write(tapFile(c).toPath(), (x + " " + y).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            android.util.Log.w("TapOff", "couldn't save the tap point", e);
        }
        ContentResolver r = c.getContentResolver();
        String key = Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES;
        String me = new ComponentName(c, LockService.class).flattenToString();
        String cur = Settings.Secure.getString(r, key);
        List<String> others = new ArrayList<>();
        if (cur != null) for (String s : cur.split(":")) if (!s.isEmpty() && !s.equals(me)) others.add(s);
        // A leftover entry would not rebind, so drop it first and add it back.
        if (cur != null && cur.contains(me)) Settings.Secure.putString(r, key, String.join(":", others));
        others.add(me);
        Settings.Secure.putString(r, key, String.join(":", others));
        Settings.Secure.putInt(r, Settings.Secure.ACCESSIBILITY_ENABLED, 1);
    }
}
