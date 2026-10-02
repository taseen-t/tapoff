---
tags: [tapoff, architecture]
---
# Architecture

Plain Java, no Gradle, no libraries. `build.sh` runs aapt2 → aidl → javac 17 → d8 → zipalign → apksigner. minSdk 33,
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
| On connect: darkness closes in on the tap point (soft-edged iris, 420 ms; the point comes from the wallpaper process in `files/tap_point`), `GLOBAL_ACTION_LOCK_SCREEN`, then `disableSelf()` after 1 s | `LockService.onServiceConnected`, `Iris` |

## Setup without a computer (`ShizukuSetup.java`)
| Step | Where |
|---|---|
| Settings' setup card (shown while `WRITE_SECURE_SETTINGS` is missing): **Set up without a computer** | `MainActivity.setupWithShizuku` |
| Shizuku not installed (`<queries>` lets TapOff see it): dialog with a short guide and a Play Store link | `MainActivity.setupWithShizuku` |
| Installed but no live binder: toast and open Shizuku to start it | same |
| Shizuku calls the `com.taseen.tapoff.shizuku` provider (`sendBinder`) when it starts or TapOff comes to the foreground; the binder is kept in a static | `ShizukuSetup.call` |
| `attachApplication` (once per binder), `checkSelfPermission`, else `requestPermission` → Shizuku's Allow dialog → `dispatchRequestPermissionResult` | `ShizukuSetup.grant`, `APP` |
| `newProcess(pm grant com.taseen.tapoff WRITE_SECURE_SETTINGS)` as shell, then the card re-checks `LockService.canLock` | `ShizukuSetup.pmGrant` |

Shizuku's AIDL (`src/moe/shizuku/server/*.aidl`, trimmed, Apache 2.0) is compiled by `aidl` into `out/gen`;
`src/moe/shizuku/api/BinderContainer.java` must keep its package name.

## Brightness and volume sliders (`NotchPanel.java`)
- Hosted in `TapWallpaper` (one instance). Opened by a **back tap**: Pixel's Quick Tap → Open app → **TapOff
  Sliders** launches `SlidersActivity` (translucent, finishes at once), which sends `NotchPanel.OPEN` to the wallpaper
  process. `TapWallpaper` also watches Quick Tap's `columbus_*` settings for changes (it can't read them) so the app
  can stop saying "Back tap is set up" when they change.
- The panel: one window covering only the sliders (and in portrait the way up to the camera), `FLAG_NOT_TOUCH_MODAL`
  + `FLAG_WATCH_OUTSIDE_TOUCH`, so the rest of the screen keeps working and `ACTION_OUTSIDE` puts them away. Closes
  3 s after the last touch.
- Sliders grow out of the camera (landscape: out of the middle of the stack), styled like the Quick Settings slider in
  Material You colours. Brightness writes `SCREEN_BRIGHTNESS` through Android's HLG curve (only shown when the
  one-time permission exists); volume sets `STREAM_MUSIC` (call stream in a call) with a tick per step.
- Marker files: `notch_panel_on` (sliders), `back_tap_seen` (a back tap has opened them since Quick Tap last changed).

## Wallpapers
| Part | Where |
|---|---|
| Sources: Pixel pack (read in place), Cutout, Bing, NASA (hand-picked full-resolution originals), curated Wallhaven themes; duplicates dropped across sets; lists cached 12 h, images capped at 400 MB | `Wallpapers.java` |
| Favourites: `files/favourites.json`, newest first, keyed by address (Pixel: resource id); shown as the first card | `Wallpapers.favourites`, `MainActivity.refreshFavourites` |
| Cutout designs drawn around the real camera hole | `CutoutArt.java` |
| Draws `files/wallpaper.jpg`, Material You colours via `onComputeColors` | `TapWallpaper` |
| Applying: atomic write of `wallpaper.jpg`, then `setBitmap(FLAG_LOCK)` with a crop hint matching the home screen's centre crop | `Wallpapers.apply`, `screenCrop` |

## UI
| Part | Where |
|---|---|
| Feed (Double-tap tile, Brightness and volume tile, wallpaper deck), Settings page (copyable setup command), tab bar, swipe between tabs, theme crossfade | `MainActivity.java` |
| Switches: track and knob drawn from how far the knob has slid, so they morph while Switch animates | `MainActivity.switchArt` |
| Wallpaper cards | `GlassCard.java` |
| Full-screen preview with drag pager, swipe hint, heart by the name, double-tap to favourite (red heart that wiggles), double-tap hint | `PreviewActivity.java`, `SwipeHint.java`, `DoubleTapHint.java` |
| Update check: GitHub's latest release through the lists' cache; a newer tag shows an Update button under the title | `MainActivity.checkForUpdate` |
| Colours, shapes, text, pills, `blend` | `Ui.java` |
| Quick Settings tiles | `ScreenOffTile.java`, `VolumeTile.java` |
| No-computer setup through Shizuku | `ShizukuSetup.java` |

## Icons
- App: Material Symbols Rounded as vector drawables (`res/drawable/ic_*.xml`, viewport 960); the favourite heart uses
  a gradient colour resource (`res/color/heart_gradient.xml`). The pixel hand (`ic_tap`, `ic_back_tap`, launcher icon)
  is TapOff's own art from `res/values/paths.xml`. TapOff Sliders has its own launcher icon (`ic_sliders_launcher`).
- Site: Lucide SVGs inlined in `site/index.html`.

## Website (`site/`)
One static `index.html` (no build step, no trackers), images in `site/img/`. Deployed with
`npx vercel@latest deploy --prod --yes` from `site/`. Sections: hero with spec strip, store-style screenshots, brags,
double-tap demo, cutout gallery, story, back-tap sliders demo, setup (the 99 s setup video in `site/video/` beside the steps: Shizuku on the phone, or the
computer install), FAQ.
