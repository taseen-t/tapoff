package com.taseen.tapoff;

import android.service.quicksettings.TileService;
import android.util.Log;

// Quick Settings tile that turns the screen off, for when the home screen isn't showing.
public class ScreenOffTile extends TileService {
    @Override public void onClick() {
        try {
            LockService.lock(this, -1, -1);
        } catch (SecurityException e) {
            Log.w("TapOff", "lock permission not granted yet", e);
        }
    }
}
