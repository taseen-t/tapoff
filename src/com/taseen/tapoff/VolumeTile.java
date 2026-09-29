package com.taseen.tapoff;

import android.media.AudioManager;
import android.service.quicksettings.TileService;

// Quick Settings tile that pops up the normal volume slider. No accessibility needed.
public class VolumeTile extends TileService {
    @Override public void onClick() {
        getSystemService(AudioManager.class)
            .adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
    }
}
