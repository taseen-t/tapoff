---
tags: [tapoff, decisions]
---
# Decisions

Newest at the bottom. Each one says why, so nobody undoes it by accident.

1. **Accessibility on for a second, not all day.** Banking apps refuse to open while any accessibility service is on.
   TapOff enables its service only to press "lock", then calls `disableSelf()`.
2. **Taps come through the wallpaper.** Pixel Launcher forwards taps on empty space to the live wallpaper
   (`android.wallpaper.tap`), so no always-on service is needed to hear them.
3. **Lock screen uses Pixel's own `double_tap_to_sleep`.** Wallpapers get no taps on the lock screen (only
   goingtosleep / wakingup). Apps can write that setting but not read it, so the switch state lives in prefs.
4. **Fade overlay before locking.** Without it the lock screen flashed during screen-off. A black
   `TYPE_ACCESSIBILITY_OVERLAY` fades in over 150 ms first. Verified by screen recording.
5. **Wallpaper in its own process, state in marker files.** Android resets a live wallpaper whose process dies twice
   in about 10 s. SharedPreferences don't sync across processes; files do.
6. **Never force-stop right after an install.** Same reset rule. Wait ~12 s and relaunch with
   `am start -f 0x10008000`, never `am start -S`.
7. **No Gradle.** A shell script with the SDK tools keeps the repo tiny and the build obvious.
8. **Curated Wallhaven, no people.** Searches exclude people and AI portraits; bad ids are blocked by hand.
9. **Cutout wallpapers are drawn on the phone.** No free API has punch-hole art; drawing around the real
   `DisplayCutout` path lines up on any phone.
10. **Volume uses `adjustStreamVolume`, not `adjustSuggestedStreamVolume`.** The suggested-stream call ignores
    adjustments for about half a second while nothing is playing (the "first press shows the panel" rule): a slide
    sent 8 steps and only 3 applied. Media stream (call stream during calls) matches Pixel's buttons. (2026-09-30)
11. **Brightness writes `SCREEN_BRIGHTNESS` with no extra permission.** Holders of `WRITE_SECURE_SETTINGS` may write
    System settings, and TapOff already has it from the one-time setup. Measured: with adaptive brightness on, the
    write applies at once and adaptive stays on, treating it as the user's adjustment, like the Quick Settings slider.
    The current max can be capped (0.5 in normal light), so the top of the slide may do nothing. (2026-09-30)
12. **Own brightness pill instead of SystemUI's brightness dialog.** The dialog
    (`com.android.intent.action.SHOW_BRIGHTNESS_DIALOG`, with `android.intent.extra.FROM_BRIGHTNESS_KEY` it closes
    itself after about 3 s) works, but it's an activity, so it pauses the app underneath, and its open and close
    animations can't be changed. Taseen wanted it to grow out of the camera hole and shrink back in. (2026-09-30)
13. **The pill's window is touchable.** Android forces untouchable app overlays to 0.8 alpha (untrusted-touch rule),
    which made the slider see-through. A small touchable window stays opaque and only eats taps in that spot
    while it shows. (2026-09-30)
14. **Switch parts are custom drawables, not state lists.** A `StateListDrawable` crossfade left a muddy double knob
    mid-slide, and `InsetDrawable` padding made Switch measure itself too small and clip. The track and knob now
    compute how far the knob has slid and draw size and colour from that. (2026-09-30)
15. **Official icon sets only.** Material Symbols in the app, Lucide on the site. Hand-drawn icons looked off
    (the fingerprint especially). (2026-09-30)
16. **Site stays one static file.** No build step, no analytics, no "built with Claude" line. Setup commands must
    wrap on phones (`min-width: 0` on flex and grid children) or they push the page sideways. (2026-09-30)
