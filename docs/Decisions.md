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
    System settings, and TapOff already has it from the one-time setup. Measured with a real edge slide and adaptive
    brightness on: the screen went 0.15 → 0.04 at once and was still 0.04 six seconds later, and adaptive stayed on
    (`screen_brightness_mode` 1). Whether adaptive *learns* from it is not verified, so the site doesn't claim it. The
    current max can be capped (0.5 in normal light), so the top of the slide may do nothing. (2026-09-30)
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
15. **Official icon sets only.** Material Symbols in the app (including the sun in the brightness pill), Lucide on
    the site (including the sun in the phone demo). Hand-drawn icons looked off, the fingerprint especially.
    (2026-09-30)
16. **Site stays one static file.** No build step, no analytics, no "built with Claude" line. Setup commands must
    wrap on phones (`min-width: 0` on flex and grid children) or they push the page sideways. (2026-09-30)
17. **1.7.1, not a replaced 1.7.** Screenshots and icons changed after 1.7 was published; a new version keeps the
    1.7 download honest. (2026-09-30)
18. **Install from a computer.** Play Protect's enhanced fraud protection (on in Pakistan, India and others) blocks
    installing any app with an accessibility service from a browser or file manager ("App blocked to protect your
    device"), with no way past it. `adb install` isn't blocked, and setup already needs adb, so the site and README
    now install and grant in one command. (2026-09-30)
19. **Lock screen gets a crop hint.** Without one Android framed the lock screen its own way, zoomed differently from the
    home screen's centre crop (seen on a second phone). `setBitmap` now gets the same centre-crop rect. (2026-09-30)
20. **Update check through the lists' cache.** GitHub's latest-release JSON is fetched like a wallpaper list (12 h
    cache, quiet offline), so no new networking code; the privacy note says so. (2026-09-30)
21. **NASA as hand-picked originals.** The picture-of-the-day API needs a key and its demo key allows 10 requests an
    hour per IP (shared behind mobile carrier NAT). The image library needs no key, but its 1920px "large" copies look
    pixelated cropped to a phone, and its search can't tell original sizes (some 50 MB). So: 16 ids, each at least
    2,200px tall, under 12 MB, no text on them. It replaced Wallhaven's nebula set, mostly re-uploaded Hubble photos
    (one nebula appeared three times). (2026-09-30)
22. **No duplicates across wallpaper sets.** Wallhaven searches overlap (5 pictures were in both Abstract and Minimal),
    so a picture shows only in the first set that has it; the Pixel pack is de-duplicated by resource. (2026-09-30)
23. **Sliders open by back tap, not by tapping the camera (in portrait).** Taseen wanted a tap on the camera, but the
    status bar window sits above every app overlay and owns the top 136px, camera included, so the tap never reaches
    TapOff (measured with `dumpsys input`). Options offered: a spot under the camera, keep edge slides, a Quick
    Settings tile, or Pixel's Quick Tap. He chose Quick Tap. The camera tap stays in landscape, where the status bar
    isn't over it. The VLC-style edge strips are gone. (2026-09-30)
24. **The sliders' window covers only the sliders.** A full-screen window made the phone unusable while they were
    out. Now `FLAG_NOT_TOUCH_MODAL` + `FLAG_WATCH_OUTSIDE_TOUCH`: touches elsewhere reach apps and also close the
    sliders. (2026-09-30)
25. **Favourites like Instagram.** Double-tap only adds (never removes), with a red gradient heart that pops, wiggles
    and floats away; the heart by the name toggles. A double-tap hint (hand, heart floating up) follows the swipe hint
    instead of text. (2026-09-30)
26. **"Back tap is set up" comes from use, not from settings.** Apps can't read Quick Tap's `columbus_*` settings
    (hidden Secure settings, same as `double_tap_to_sleep`). So `SlidersActivity` notes the first time something other
    than the home screen's app icon opens it (checked with the referrer), and the button changes then. It still opens
    Quick Tap's settings page. (2026-09-30)
27. **Screen off closes in on the tap.** A flat 150 ms fade looked like a snap. The darkness now shrinks to where the
    home screen was double-tapped (the wallpaper writes the point to a file the lock service reads, since they run in
    different processes). (2026-09-30)
28. **Lock-screen double-tap switch removed.** Taseen doesn't need it; the switch, its prefs and the code writing
    `double_tap_to_sleep` are gone (supersedes #3). The Android setting it turned on stays as it is on phones that had
    it. (2026-09-30)
29. **The site's screenshot strip shows only the app.** Wallpaper shots (Sunflower, Lollipop preview) moved out; the
    wallpapers have their own gallery further down. (2026-09-30)
30. **Camera tap removed.** It only ever worked in landscape and Taseen asked to drop it: the landscape spot, its ring,
    the switch and the code are gone. Back tap is the one way in; in landscape the sliders still open centred.
    (2026-09-30)
31. **Know when Quick Tap changes, even without reading it.** A `ContentObserver` on the `columbus_*` settings (in the
    always-running wallpaper process) forgets `back_tap_seen` on any change, so turning Quick Tap off makes the button
    offer setup again; the next back tap that opens the sliders marks it set up again. (2026-09-30)
32. **Landscape sliders sit at the top centre.** In landscape the camera is off to one side, so the sliders no longer
    grow from it; they slide down from the top-centre edge (fading in) and scale back up into it. Portrait still grows
    them from the camera hole. (2026-09-30)
33. **No-computer setup through Shizuku, not built-in wireless-debugging pairing.** Pairing with adbd ourselves needs
    SPAKE2, TLS keying-material export and AES-GCM, far too much for a ~100 KB no-Gradle app. Shizuku does the pairing and
    runs as shell; TapOff asks it to run the same `pm grant` a computer would. The computer install stays, because Play
    Protect's fraud protection blocks browser installs of accessibility apps in some regions and `adb install` is
    exempt (#18). (2026-09-30)
34. **Shizuku's interfaces vendored as AIDL, compiled by build-tools' `aidl`.** No Gradle means no Maven library, and
    hand-written transaction codes are easy to get wrong (AIDL's `= N` is `FIRST_CALL_TRANSACTION + N` on the wire).
    Only the four `IShizukuService` calls TapOff uses are kept, with their ids; `IRemoteProcess` has no ids, so it's
    verbatim. `BinderContainer` keeps its `moe.shizuku.api` name because Shizuku parcels its binder in it. All Apache
    2.0, credited in the README. (2026-09-30)
35. **New exported provider and permission for Shizuku.** Shizuku sends its binder only to apps that request
    `moe.shizuku.manager.permission.API_V23`, by calling their `<package>.shizuku` provider. `ShizukuSetup` is exported
    (Shizuku runs as shell, another uid) but guarded by `INTERACT_ACROSS_USERS_FULL`, which shell and system hold and
    normal apps can't get; it only stores the binder and has no data. Known side effects, from Shizuku's server source:
    while Shizuku runs it may start TapOff's main process to deliver the binder, and if the provider ever looks dead it
    force-stops the app once and retries (that could also restart the wallpaper process). (2026-09-30)
36. **Attach once per Shizuku binder.** Shizuku keeps one client record per process with the app binder from the first
    `attachApplication`; a second attach from the same process crashes in the server (null client record) and the old
    binder would get the permission result anyway. So one static app binder handles results for whatever is pending.
    (2026-09-30)
37. **Videos use a free 3D Pixel 7 Pro, credited.** No free, license-clean model of the regular Pixel 7 could be
    downloaded (the one on Sketchfab was deleted, Printables sits behind a bot check, the good one is paid). Taseen
    chose Aborsoft 3D's Pixel 7 Pro (CC BY 4.0) over a hand-built model or buying one: recoloured to Snow, credited on
    each video's outro card and in its share copy. It shows the Pro's three lenses, not the Pixel 7's two. The model
    file stays out of the repo. (2026-09-30)
