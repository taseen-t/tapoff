---
tags: [tapoff, tasks]
---
# Tasks

Every request goes here the moment it's asked. Tick subtasks as they land. Never delete; mark done or dropped with why.

## Done before this vault existed (v1.0–1.6)
- [x] Double-tap home screen to turn off, with haptic; bank apps keep working
- [x] Lock-screen double-tap (Pixel setting), separate switches, no haptic there
- [x] Screen off and Volume Quick Settings tiles
- [x] Wallpaper browser: glass cards deck, full-screen swipe preview, home + lock, caching, curated sources
- [x] 13 Cutout wallpapers around the camera hole, including Tap tap
- [x] Pixel-hand icon, light / dark / system theme with smooth switch
- [x] Settings slides in, swipe between tabs, sliding tab highlight
- [x] Report a bug via X
- [x] Open source (MIT) and the Vercel site with story, screenshots, FAQ accordions

## 2026-09-30
- [x] Slide the left edge for volume, like VLC
  - [x] Test on the phone after "Display over other apps" was allowed
  - [x] Fix the first half-second of each slide being ignored
  - [x] Check the back gesture still works from the edge
- [x] Slide the right edge for brightness
  - [x] Works with no new permission
  - [x] Tried SystemUI's brightness panel as the indicator (works, but can't be animated, pauses the app)
  - [x] Own slider that grows out of the camera hole and shrinks back in
  - [x] Adaptive brightness doesn't block it and stays on
- [x] Tile: "Slide the edges" with Volume and Brightness switches
  - [x] Status line like the Double-tap tile (On for volume and brightness / On for volume / Off)
  - [x] Sliding-hand icon instead of the volume icon
- [x] Switches in both tiles easier to read
  - [x] Knob glides and morphs between off and on
- [x] Website
  - [x] Brag about the edge sliders (section with animated phone, store card, FAQ, hero line)
  - [x] Replace outdated screenshots (light main had the old cards, preview said 10 / 12)
  - [x] Fix "Five minutes, once" on phones (page scrolled sideways)
  - [x] Replace the generic No login / No ads chips (pixel-font spec strip)
  - [x] Explain the double-tap animation (animated phone that taps, buzzes and fades off)
  - [x] Use real icon libraries (Lucide on the site, Material Symbols in the app), fix the fingerprint icon
- [x] Document everything in an Obsidian vault and a Graphify graph of the whole codebase
- [x] Release 1.7 and deploy the site
- [x] 1.7.1 follow-ups
  - [x] Last hand-drawn icons gone (the pill's sun in the app, the demo's sun on the site)
  - [x] Adaptive brightness measured with a real slide; unverified "learns" wording removed
  - [x] Right-edge back gesture tested
  - [x] Setup section mentions "Display over other apps"
  - [x] Screenshots retaken on the final build
- [x] Site: "What happens when you double-tap" moved up to where the edge sliders were, edge sliders moved down to its old spot
- [ ] Videos: brag, full walkthrough, case study, each vertical and landscape, ElevenLabs voice, playful pixel style
  - [ ] Taseen picks a voice, films the case clips, answers the case-study questions
    - [x] Answers (2026-09-30): male voice that sounds most human (not AI-like), bank not named, first person with
          handle only (no face), brag video first; setup videos still wanted, now showing Shizuku
    - [x] Voice picked from samples: Jerry B. – Hyper-Real (eleven_v3); Mark and Matt heard, Arthur needs a paid tier
    - [x] Case clips: Taseen said to make them as animations instead of filming (no downloaded footage, for copyright)
    - [x] Brag video (portrait + landscape) first: 23 s, Jerry voiceover, chiptune written in code, real recordings
          of the double-tap screen-off and the sliders, animated acrylic case; weather/city blurred off the home
          screen; in brag-output-2026-09-30-2030/ (not committed: large files)
    - [ ] Taseen watches the brag video; re-roll anything
      - [x] Bezels too wide: now a thin clear-case rim (1.8% of width) and a Pixel-like bezel (2.4%)
      - [x] Back of the phone looked flat: Aborsoft 3D's free Pixel 7 Pro model (Sketchfab, CC BY 4.0), downloaded by
            Taseen, rendered with three.js; frame recoloured from Hazel gold to Snow silver; credit on the outro card and
            in the share copy
        - [x] The recordings play on the model's screen (a canvas texture fitted to the screen's UV range, which uses
              only 6–94% / 8–96% of the texture; before the fit the bottom of each recording was cut off)
        - [x] Clear acrylic case: thin rim plus a back plate on the glass; the camera bar pokes out through a
              full-width cutout (Taseen: "this camera bump should be out of the case")
        - [x] Gentle sway so the phone feels held; the hand's tap points come from the 3D projection
        - [x] TAP · TAP label and knock rings dark on the white back (white again after the switch to black)
        - [x] Camera bar stands out: phone tilts top-towards-you while its back shows, soft shadows, polished bar
        - [x] Case shines and is see-through (Taseen: not matte): clear material that shows only reflections, thin
              studio softbox strips, and a shine band on the case back that slides with the phone's turn
        - [x] No reflection on the screen, only on the case (Taseen); back rendered at 2x so it's as clear as possible
        - [x] Phone switched to black glass with the silver frame and bar, so the bar stands out (Taseen)
        - [x] Hook caption moved right in landscape so the hand doesn't overlap it
      - [x] Re-rendered both formats and sent (23 s each)
  - [x] Record TapOff on the phone over USB (brag: screen-off and sliders; more for the walkthrough later)
  - [ ] Build, check stills, render all six

## 2026-09-30 (later) · 1.8
- [x] Fix issues from Taseen's screenshots
  - [x] Lock and home screen framed the same wallpaper differently (crop hint)
  - [x] Setup command couldn't be copied (code block, Copy and guide buttons)
  - [x] Play Protect blocked the install on a second phone (install from a computer; site, README, FAQ)
- [x] Automatic update check (GitHub latest release, Update button)
- [x] Remove duplicate wallpapers (across Wallhaven sets, repeat nebulae)
- [x] NASA wallpapers
  - [x] Tried APOD (needs a key, rate-limited) and the image library's 1920px copies (pixelated)
  - [x] Hand-picked full-resolution originals, no text on them (text rule applies to NASA only)
- [x] Favourites
  - [x] Heart next to the wallpaper's name
  - [x] Double-tap to favourite, red gradient heart that wiggles like Instagram's
  - [x] Hand animation hint (double-tap, heart floats up) after the swipe hint, instead of text
  - [x] Favourites card first in the feed
- [x] Brightness and volume from the camera instead of the edges
  - [x] Both sliders in the brightness slider's style, finger-draggable, centred in landscape
  - [x] Tapping the camera: blocked by the status bar in portrait; kept in landscape with a faint ring, own switch
  - [x] Back tap (Quick Tap → TapOff Sliders), chosen by Taseen
  - [x] Rest of the screen usable while the sliders are out
  - [x] Tile icon: pixel hand tapping the back of a phone, no outline showing through the hand
  - [x] Button says "Back tap is set up" once a back tap has opened the sliders, and opens Quick Tap's settings
- [x] Site: double-tap section moved up, sliders section rewritten for back tap
- [x] Screen off: darkness closes in on the tapped spot instead of a quick fade
- [x] Back tap button greys out once set up, still opens Quick Tap settings
- [x] Remove the lock-screen double-tap option and its code
- [x] Site: back tap shown on the back of the phone, the phone flips, then the sliders
- [x] Site: new screenshots; the strip shows only the app, no wallpaper shots
- [x] Site: hero shows only the pixel hand tapping with ripples, not the whole icon
- [x] Remove the camera-tap setting and its code
- [x] Button stops saying "Back tap is set up" when Quick Tap is changed or turned off
- [ ] Back tap not opening the sliders on Taseen's phone: Quick Tap was off; to retest with it on (and with "Take
      screenshot" to rule out the acrylic case)
- [x] Landscape: sliders open at the top centre and slide up into the top edge to close
- [x] Decide the no-PC setup path: Taseen chose Shizuku (built-in wireless-debugging pairing needs SPAKE2 + TLS export + AES-GCM, too big for the no-Gradle app)
- [x] Grant WRITE_SECURE_SETTINGS through Shizuku, no computer needed
  - [x] Settings setup card: "Set up without a computer" option
  - [x] Shizuku not installed: link to its Play Store page and guide
  - [x] Shizuku running: ask its permission, run `pm grant com.taseen.tapoff android.permission.WRITE_SECURE_SETTINGS` as shell
  - [x] No Gradle: vendored the trimmed Apache-2.0 Shizuku AIDL + BinderContainer, compiled with build-tools' aidl
  - [x] Site setup, README, docs, Graphify (installing from the web still hits Play Protect in some regions, so the computer install stays there)
  - [x] Tested end to end on the Pixel (revoked, then Set up without a computer → Shizuku's Allow → granted, card gone)
  - [x] Look through Shizuku's code for bugs worth a PR: a second attachApplication from one process crashes the server. Taseen said yes; opened https://github.com/RikkaApps/Shizuku/pull/2537 (found by reading, not reproduced)
  - [x] Release with the landscape sliders (755a74d) as 1.9, deploy the site
- [ ] Setup videos (portrait + landscape): still wanted (Taseen, 2026-09-30), now showing the Shizuku setup
- [ ] Guide video, portrait only (Taseen, 2026-09-30): the complete path from installing TapOff, through setting up
      Shizuku, to granting the permission and being good to go
  - [x] Plan and script; Taseen OKs the phone actions it needs: redo the permission flow (revoke over adb, switch
        TapOff off in Shizuku, film Set up without a computer → Allow), download the APK in Chrome and open the
        installer (stop if Play Protect blocks), view Settings/Developer options/Wireless debugging/Shizuku/Play Store,
        restart Shizuku on camera; voiceover about 90 s in Jerry's voice; ends with the back-tap sliders
  - [ ] Record the real screens on the Pixel (Do Not Disturb on, screenshot before every tap)
    - [x] Site in Chrome → Get the APK → Downloads → installer ("Update this app?"); Play Protect then blocked it
          ("App blocked to protect your device"), filmed as a still; TapOff unchanged
    - [x] Shizuku's Play Store page
    - [x] About phone left out: it shows device identifiers; the Build number step will be a drawn card instead
    - [x] Developer options → Wireless debugging; Shizuku pairing and Start (done by Taseen on camera, including one
          failed pairing and the retry); Play Protect → Pause app scanning (done by Taseen on camera; a security
          setting, so not tapped by Claude); install succeeding once scanning was paused ("App updated"); permission
          flow (Shizuku remembered TapOff, so its Allow dialog comes from the 2026-09-30 test screenshot); wallpaper
          preview and chooser (backed out, nothing set); Sliders switch and Quick Tap → Open app → TapOff
    - [x] Taseen asked for the Play Protect workaround in the guide (Play Store → profile → Play Protect → gear →
          switch off; he chose Pause, which turns back on by itself the next day)
  - [ ] Voiceover: 8 of 12 lines made (intro, install, Play Protect, Shizuku, Developer options, Start, permission,
        wallpaper); ElevenLabs then disabled the account's free tier ("unusual activity"), probably from 12 requests at
        once; Taseen chose: sliders and ending lines reused from the brag voiceover, Wireless debugging and Pairing
        carried by captions
    - [x] Retried later at Taseen's request, one line per request, waiting for each: the block had lifted. Made
          Wireless debugging, Pairing, the Play Protect workaround (Play Store → profile → Play Protect → gear →
          pause scanning → install again) and a shorter sliders line; the old "use the computer install instead"
          line is dropped. About 1,250 credits for the guide in total, as approved. The ending reuses the brag take.
  - [x] Method from Taseen's UplinkeSIM video docs (~/Desktop/uplink-video/docs): on-device OCR (Apple Vision) finds
        personal text, blur only the frames the edit uses, re-scan to prove 0 leaks, check contact sheets by eye,
        captions for every step with a STEP counter, keep text out of the right-hand Reels/TikTok button strip,
        mix at about −14 LUFS. Vision's "accurate" text model fails on this macOS build (e5rt error 13), so the
        scan uses "fast" mode, and every blurred range is also checked by eye
- [x] Remote Control turned on for this session (Taseen asked)
  - [ ] Voiceover (Jerry), build in the brag video's 3D style, check stills, render, send
- [ ] Taseen tests on a second Android, then fixes
- [ ] Refresh site screenshots (tile changed) with the video footage

- [ ] Play Protect blocked the install on Taseen's second phone (Samsung): "App blocked to protect your device"
  - [x] Root cause: Google's enhanced fraud protection blocks any browser/file-manager install of an app declaring an
        accessibility service (its four-permission list: READ_SMS, RECEIVE_SMS, notification listener, accessibility).
        Now on in 185 markets, not just a few countries. Nothing in the manifest can dodge it without losing the lock.
  - [x] No-computer path reordered: Shizuku first, then InstallerX Revived (authorizer Shizuku) installs TapOff as
        shell, the same exemption `adb install` has (README, site steps, site FAQ)
  - [x] Decision #38, progress log, Graphify refreshed
  - [ ] Taseen confirms the Shizuku + InstallerX install goes through on the blocked phone. If it is blocked anyway,
        the install may be attributed to InstallerX instead of shell: check with `adb shell dumpsys package
        com.taseen.tapoff | grep installerPackageName` (an `adb install` leaves it `null`, checked on the Pixel) and
        set InstallerX's installer package to `com.android.shell` in its settings
  - [ ] Also worth a try there, 30 seconds: Play Store → Play Protect → gear → turn off app scanning → install → turn
        it back on. If that works, add it to the FAQ as the shortest route (unverified, so it isn't in the docs yet)
  - [ ] Play Protect appeal for TapOff (free, slow, the only thing that unblocks everyone): the service reads nothing —
        no event types, `canRetrieveWindowContent="false"`
  - [x] In-app updater through Shizuku `pm install` instead of opening the releases page: considered, dropped. Whoever
        can update that way already has Shizuku and InstallerX, which does the same job; it would only work while
        Shizuku is running, and it can't help the first install anyway

## Open
- [ ] Play Store listing? Costs $25 once. Waiting on Taseen (never spend without a yes).
- [ ] Custom domain? Waiting on whether Taseen is a student (GitHub Student Pack has free domains).
