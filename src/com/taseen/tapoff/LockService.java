package com.taseen.tapoff;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import java.util.ArrayList;
import java.util.List;

// Bank apps refuse to run while any accessibility service is on, so this one is switched on only
// for the moment of locking and switches itself off right after. Locking through accessibility keeps
// fingerprint unlock; device-admin lockNow() would force the PIN every time.
public class LockService extends AccessibilityService {
    // Tuning knob: how long the fade to black takes before the screen goes off.
    static final long FADE_MS = 150;
    // How long the black stays up, covering the system's lock-screen flash, before this service leaves.
    static final long HOLD_MS = 1000;

    @Override protected void onServiceConnected() {
        // The system lock action flashes the lock screen and home screen on its way off, so fade to
        // black first and lock underneath it. An accessibility overlay needs no extra permission.
        WindowManager wm = getSystemService(WindowManager.class);
        View black = new View(this);
        black.setBackgroundColor(Color.BLACK);
        black.setAlpha(0f);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT);
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
        lp.setFitInsetsTypes(0);
        wm.addView(black, lp);
        black.animate().alpha(1f).setDuration(FADE_MS).withEndAction(() -> {
            performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN);
            black.postDelayed(() -> {
                wm.removeView(black);
                disableSelf();
            }, HOLD_MS);
        });
    }

    @Override public void onAccessibilityEvent(AccessibilityEvent event) {}

    @Override public void onInterrupt() {}

    // WRITE_SECURE_SETTINGS is granted once over adb by `build.sh --install`.
    static boolean canLock(Context c) {
        return c.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
    }

    // The lock screen's double-tap is Pixel's own "Double-tap to turn off screen" setting; TapOff only switches it.
    // Apps may write that hidden setting but not read it, so the switch's state is remembered here.
    // ponytail: if it's changed in Pixel's Settings instead, this switch shows the old state until toggled.
    private static final String LOCK_SCREEN_SETTING = "double_tap_to_sleep";

    static boolean lockScreenOn(Context c) {
        return c.getSharedPreferences("tapoff", MODE_PRIVATE).getBoolean("lock_screen", true);
    }

    static void setLockScreen(Context c, boolean on) {
        Settings.Secure.putInt(c.getContentResolver(), LOCK_SCREEN_SETTING, on ? 1 : 0);
        c.getSharedPreferences("tapoff", MODE_PRIVATE).edit().putBoolean("lock_screen", on).apply();
    }

    static void lock(Context c) {
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
