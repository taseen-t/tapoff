---
tags: [tapoff, architecture]
---
# Architecture

Plain Java, no Gradle, no libraries. `build.sh` runs aapt2 → javac 17 → d8 → zipalign → apksigner. minSdk 33,
targetSdk 35. Package `com.taseen.tapoff`, sources in `src/com/taseen/tapoff/`.

## Processes
- **App process**: `MainActivity`, `PreviewActivity`, the Quick Settings tiles, `LockService`.
- **`:wallpaper` process**: `TapWallpaper` and the edge sliders it hosts. Kept separate so an app crash or reinstall
  can't kill the wallpaper twice in ~10 s, which makes Android reset the live wallpaper (see [[Decisions]]).
- The two share state through **marker files** in `files/` (`home_off`, `edge_volume_on`, `edge_brightness_on`)
  plus a package-only broadcast (`EdgeSlider.CHANGED`, `RECEIVER_NOT_EXPORTED`) to re-sync.

## Screen off
| Step | Where |
|---|---|
| Pixel Launcher sends `android.wallpaper.tap` for taps on empty home-screen space | `TapWallpaper.TapEngine.onCommand` |
| Two taps within `DOUBLE_TAP_MS` (400) and double-tap slop → heavy-click haptic | `TapWallpaper` |
| Adds itself to `ENABLED_ACCESSIBILITY_SERVICES` (needs `WRITE_SECURE_SETTINGS`, granted once over adb) | `LockService.lock` |
| On connect: black overlay fades in (150 ms), `GLOBAL_ACTION_LOCK_SCREEN`, then `disableSelf()` after 1 s | `LockService.onServiceConnected` |
| Lock-screen double-tap: Pixel's hidden Secure setting `double_tap_to_sleep`, write-only, state kept in prefs | `LockService.setLockScreen` |

## Edge sliders (`EdgeSlider.java`)
- One instance per edge, both created in `TapWallpaper.onCreate`. Each adds an invisible 14dp × 60%-height
  `TYPE_APPLICATION_OVERLAY` strip (needs "Display over other apps").
- **Left, volume:** every 28dp of travel → `adjustStreamVolume` on `STREAM_MUSIC` (or `STREAM_VOICE_CALL` during a
  call) with `FLAG_SHOW_UI`, plus a tick haptic.
- **Right, brightness:** writes `Settings.System.SCREEN_BRIGHTNESS` through Android's slider curve (HLG,
  `toLinear` / `toPosition`), full range over 320dp of travel.
- **Brightness level** (sun: Material `light_mode`, reused from `ic_card_today`): the `Notch` view, a touchable overlay centred on the camera hole (`CutoutArt.hole`). It grows
  out of the hole (320 ms overshoot), styled like the Quick Settings slider in Material You colours, and shrinks back
  in 900 ms after release.
- Sideways swipes are the system back gesture; the strip just sees `ACTION_CANCEL`.

## Wallpapers
| Part | Where |
|---|---|
| Sources: Pixel pack (read in place), Cutout, Bing, curated Wallhaven themes; lists cached 12 h, images capped at 400 MB | `Wallpapers.java` |
| Cutout designs drawn around the real camera hole | `CutoutArt.java` |
| Draws `files/wallpaper.jpg`, Material You colours via `onComputeColors` | `TapWallpaper` |
| Applying: atomic write of `wallpaper.jpg`, then `setBitmap(FLAG_LOCK)` for the lock screen | `Wallpapers.apply` |

## UI
| Part | Where |
|---|---|
| Feed (Double-tap tile, Slide the edges tile, wallpaper deck), Settings page, tab bar, swipe between tabs, theme crossfade | `MainActivity.java` |
| Switches: track and knob drawn from how far the knob has slid, so they morph while Switch animates | `MainActivity.switchArt` |
| Wallpaper cards | `GlassCard.java` |
| Full-screen preview with drag pager and swipe hint | `PreviewActivity.java`, `SwipeHint.java` |
| Colours, shapes, text, pills, `blend` | `Ui.java` |
| Quick Settings tiles | `ScreenOffTile.java`, `VolumeTile.java` |

## Icons
- App: Material Symbols Rounded as vector drawables (`res/drawable/ic_*.xml`, viewport 960). The pixel hand
  (`ic_tap`, `ic_slide`, launcher icon) is TapOff's own art from `res/values/paths.xml`.
- Site: Lucide SVGs inlined in `site/index.html`.

## Website (`site/`)
One static `index.html` (no build step, no trackers), images in `site/img/`. Deployed with
`npx vercel@latest deploy --prod --yes` from `site/`. Sections: hero with spec strip, store-style screenshots, brags,
double-tap demo, cutout gallery, story, edge sliders demo, setup, FAQ.
