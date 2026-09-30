package com.taseen.tapoff;

import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.VibrationAttributes;
import android.os.VibrationEffect;
import android.os.VibratorManager;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.ViewConfiguration;
import java.io.File;

// The launcher sends "android.wallpaper.tap" for taps on empty home-screen space; two quick ones lock.
public class TapWallpaper extends WallpaperService {
    // Tuning knob: max gap between the two taps. Raise it if double-taps get missed.
    static final long DOUBLE_TAP_MS = 400;

    static File photo(android.content.Context c) { return new File(c.getFilesDir(), "wallpaper.jpg"); }

    // The home-screen switch; on unless turned off. A marker file rather than SharedPreferences, because the
    // wallpaper runs in its own process and a file is seen by both at once.
    static boolean enabled(android.content.Context c) {
        return !new File(c.getFilesDir(), "home_off").exists();
    }

    static void setEnabled(android.content.Context c, boolean on) {
        File f = new File(c.getFilesDir(), "home_off");
        try {
            if (on) f.delete();
            else f.createNewFile();
        } catch (java.io.IOException e) {
            Log.w("TapOff", "couldn't save the home-screen switch", e);
        }
    }

    // The camera-tap sliders live here: Android keeps the wallpaper's process running, so no notification is needed.
    private NotchPanel notch;
    private final android.content.BroadcastReceiver notchChanged = new android.content.BroadcastReceiver() {
        @Override public void onReceive(android.content.Context c, android.content.Intent i) {
            if (NotchPanel.OPEN.equals(i.getAction())) notch.open();
            else notch.sync();
        }
    };

    @Override public void onCreate() {
        super.onCreate();
        notch = new NotchPanel(this);
        notch.sync();
        android.content.IntentFilter f = new android.content.IntentFilter(NotchPanel.CHANGED);
        f.addAction(NotchPanel.OPEN);
        registerReceiver(notchChanged, f, RECEIVER_NOT_EXPORTED);
    }

    @Override public void onConfigurationChanged(android.content.res.Configuration config) {
        super.onConfigurationChanged(config);
        notch.rotated();
    }

    @Override public void onDestroy() {
        unregisterReceiver(notchChanged);
        notch.remove();
        super.onDestroy();
    }

    @Override public Engine onCreateEngine() { return new TapEngine(); }

    class TapEngine extends Engine {
        private final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        private Bitmap bmp;
        private long loadedAt = -1;
        private long lastTap;
        private int lastX, lastY;

        // Returns true if the photo changed since the last load.
        private boolean load() {
            File f = photo(TapWallpaper.this);
            if (f.lastModified() == loadedAt) return false;
            loadedAt = f.lastModified();
            bmp = f.exists() ? BitmapFactory.decodeFile(f.getPath()) : null;
            return true;
        }

        private void draw() {
            SurfaceHolder h = getSurfaceHolder();
            Canvas c = h.lockCanvas();
            if (c == null) return;
            c.drawColor(Color.BLACK);
            if (bmp != null) { // centre-crop to fill the screen
                float s = Math.max((float) c.getWidth() / bmp.getWidth(), (float) c.getHeight() / bmp.getHeight());
                Matrix m = new Matrix();
                m.setScale(s, s);
                m.postTranslate((c.getWidth() - bmp.getWidth() * s) / 2, (c.getHeight() - bmp.getHeight() * s) / 2);
                c.drawBitmap(bmp, m, paint);
            }
            h.unlockCanvasAndPost(c);
        }

        @Override public void onSurfaceRedrawNeeded(SurfaceHolder holder) {
            load();
            draw();
        }

        // Picks up a new photo chosen in the app when you come back to the home screen.
        @Override public void onVisibilityChanged(boolean visible) {
            if (visible && load()) {
                draw();
                notifyColorsChanged();
            }
        }

        // Lets Material You colours follow the photo.
        @Override public WallpaperColors onComputeColors() {
            load();
            return bmp == null ? null : WallpaperColors.fromBitmap(bmp);
        }

        // The launcher reports taps on empty home-screen space. (The lock screen reports none; its double-tap is
        // Pixel's own setting, switched from the app.)
        @Override public Bundle onCommand(String action, int x, int y, int z, Bundle extras, boolean resultRequested) {
            if (!WallpaperManager.COMMAND_TAP.equals(action) || !enabled(TapWallpaper.this)) return null;
            long now = SystemClock.uptimeMillis();
            int slop = ViewConfiguration.get(TapWallpaper.this).getScaledDoubleTapSlop();
            if (now - lastTap < DOUBLE_TAP_MS && Math.hypot(x - lastX, y - lastY) < slop) {
                lastTap = 0;
                // Instant feedback: the lock itself takes a moment to start.
                getSystemService(VibratorManager.class).getDefaultVibrator().vibrate(
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK),
                    VibrationAttributes.createForUsage(VibrationAttributes.USAGE_TOUCH));
                try {
                    LockService.lock(TapWallpaper.this);
                } catch (SecurityException e) {
                    Log.w("TapOff", "lock permission not granted yet", e);
                }
            } else {
                lastTap = now;
                lastX = x;
                lastY = y;
            }
            return null;
        }
    }
}
