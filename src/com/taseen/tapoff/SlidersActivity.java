package com.taseen.tapoff;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;

// Shows the brightness and volume sliders over whatever's on screen, then gets out of the way. Pixel's Quick Tap
// launches it: Settings → System → Gestures → Quick Tap → Open app → TapOff Sliders.
public class SlidersActivity extends Activity {
    // TapOff can't read Quick Tap's setting, so it remembers the first time a back tap (anything but the home screen's
    // app icon) opened this, and the app then says back tap is set up.
    static boolean backTapSeen(android.content.Context c) {
        return new File(c.getFilesDir(), "back_tap_seen").exists();
    }

    // Quick Tap's setting changed (turned off, or pointed elsewhere): until the next back tap opens this, the app goes
    // back to offering to set it up. TapOff can't read the setting, but it is told when it changes.
    static void forgetBackTap(android.content.Context c) {
        new File(c.getFilesDir(), "back_tap_seen").delete();
    }

    // The Quick Tap settings to watch: on/off, which action, which app.
    static final String[] QUICK_TAP_KEYS = {"columbus_enabled", "columbus_action", "columbus_launch_app"};

    @Override protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        if (!fromLauncher()) {
            try {
                new File(getFilesDir(), "back_tap_seen").createNewFile();
            } catch (IOException e) {
                android.util.Log.w("TapOff", "couldn't note the back tap", e);
            }
        }
        if (!NotchPanel.enabled(this) || !NotchPanel.allowed(this)) {
            Toast.makeText(this, "Turn on Sliders in TapOff first", Toast.LENGTH_LONG).show();
        } else if (!Wallpapers.isActive(this)) {
            Toast.makeText(this, "The sliders work while TapOff is your wallpaper", Toast.LENGTH_LONG).show();
        } else {
            NotchPanel.requestOpen(this);
        }
        finish();
    }

    private boolean fromLauncher() {
        Uri from = getReferrer();
        android.content.pm.ResolveInfo home = getPackageManager().resolveActivity(
            new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0);
        return from != null && home != null && home.activityInfo.packageName.equals(from.getHost());
    }
}
