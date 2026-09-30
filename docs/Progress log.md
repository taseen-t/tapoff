---
tags: [tapoff, log]
---
# Progress log

Newest first. Each entry: done / blocked / next.

## 2026-09-30 (late) · Play Protect block: a no-computer way in

**Done**
- Second phone hit "App blocked to protect your device" on install. Root cause is Google's enhanced fraud protection:
  a browser or file manager may not install any app that declares an accessibility service, and it is now on in 185
  markets (the docs still said "Pakistan, India and others"). The dialog has only an OK button.
- The exemption is the installing uid, not the app: installs run as shell aren't checked. Shizuku runs as shell, so the
  no-computer setup now goes Shizuku → InstallerX Revived (authorizer Shizuku) → TapOff → grant, instead of asking the
  phone to install the APK first and then hitting the wall. README, site steps and site FAQ rewritten; decision #38.
- No code change: the accessibility service is the screen-off feature, so no manifest edit can dodge the classifier.

**Blocked**
- Unconfirmed on the blocked phone — Taseen has it, the Pixel here can't reproduce the block.

**Next**
- Confirm the Shizuku + InstallerX install there; try the Play Protect "turn off app scanning" toggle as a shorter
  route and add it to the FAQ if it works; consider the free Play Protect appeal.

## 2026-10-01 · Setup guide video
**Done**
- Guide video (portrait, 98.9 s): install, the Play Protect workaround Taseen asked for, Shizuku, Developer options,
  Wireless debugging, pairing and starting Shizuku, TapOff's permission, wallpaper, optional sliders, it working.
  Recorded on the Pixel with Do Not Disturb on; Taseen did the Shizuku pairing and the Play Protect switch on camera.
- Voice: Jerry, 11 new lines. ElevenLabs blocked the free tier after a burst of 12 requests; one line per request
  worked later. About 1,264 credits for the guide (about 1,250 approved).
- Privacy, following Taseen's UplinkeSIM video method: OCR scan, blur, re-scan to 0 leaks (3,142 source frames, then
  1,483 final frames), and contact sheets by eye, which caught three things OCR missed. About phone was drawn, not
  filmed. New [[Videos]] note with the whole recipe.
- Remote Control turned on for this session.

**Found**
- Pausing Play Protect's app scanning lets a browser install of TapOff through ([[Decisions]] #39), so #18's "no way
  past it" was wrong. The site and README don't say so yet.
- Apple Vision's "accurate" OCR fails on this macOS build (e5rt error 13); "fast" works.

**Next**
- Taseen reviews the guide. Site/README Play Protect workaround with Taseen's OK. Then the walkthrough and case study.

## 2026-09-30 (night) · Shizuku PR, brag video
**Done**
- Shizuku PR https://github.com/RikkaApps/Shizuku/pull/2537 (re-attach crash; found by reading, not reproduced).
- Voice: Jerry B. – Hyper-Real (eleven_v3), picked from three samples. Brag video in portrait and landscape: real
  phone recordings (double-tap screen-off, back-tap sliders), animated acrylic-case hook, site art, chiptune and
  sound effects written in code. About 850 ElevenLabs credits used in total (3 samples at 161 + one 365-credit voiceover take).
- Privacy: the home screen's At a Glance line showed the city, so it is blurred in every frame.

**Notes**
- Shell volume changes (`cmd media_session volume --set`) are ignored on the Pixel; a double volume-key press restored
  the level after recording.
- One timing-test tap went to the phone (status-bar corner) before its screenshot was looked at; nothing happened.
  Screenshot first, every time.

- Review round 1 (Taseen): bezels thinned to a real phone's; the flat CSS phone replaced by a 3D Pixel 7 Pro
  (Aborsoft 3D, CC BY 4.0, downloaded by Taseen) in three.js with the recordings on its screen, a clear shiny acrylic
  case with the camera bar out through a cutout, a tilt and shadows so the bar reads as a bump, black glass with a
  silver bar, no screen reflections. Bugs found on the way: the model's screen UVs cover only part of the texture
  (recordings were cut off at the bottom and sides until fitted), the camera's matrices weren't ready before the first
  render, and the model's root carries a transform the raw mesh numbers don't show.

**Next**
- Taseen reviews the new cut; then the walkthrough, case study and Shizuku setup videos.

## 2026-09-30 (evening) · 1.9: setup without a computer
**Done**
- Settings' setup card has **Set up without a computer**: guide + Play Store link when Shizuku is missing, opens
  Shizuku when it isn't started, otherwise asks Shizuku's permission and runs `pm grant` as shell.
- Shizuku's AIDL vendored (trimmed, Apache 2.0) and compiled by build-tools' `aidl`; transaction codes checked against
  the generated stubs. APK still ~109 KB.
- Tested on the Pixel 7: revoked the permission, tapped the button, allowed TapOff in Shizuku, permission back, card
  gone, wallpaper intact.
- Site setup, FAQ and README describe both paths; the computer install stays for Play Protect regions.
- Released 1.9 together with the landscape sliders.

**Not tested**
- The "Shizuku not installed" dialog and "not started" paths on a device (Shizuku was already installed and running).

**Next**
- Shizuku PR opened with Taseen's yes: https://github.com/RikkaApps/Shizuku/pull/2537 (one-line fix for the re-attach crash).
- Videos (setup videos now show Shizuku), second-phone test, retests.

## 2026-09-30 (later) · 1.8: back-tap sliders, favourites, NASA
**Done**
- Fixed a second phone's issues: lock/home framing, copyable setup command, Play Protect block (computer install).
- Update check, duplicate wallpapers removed, NASA full-resolution set, favourites with an Instagram-style heart.
- Replaced the edge strips with sliders that grow out of the camera. Measured that the status bar owns camera taps in
  portrait; back tap via Quick Tap opens them (Taseen set it up on his phone), camera tap kept in landscape.
- One wrong tap of mine switched Taseen's lock-screen double-tap off; noticed and switched back on straight away.

**Blocked**
- Landscape camera tap not tested on a device yet (rotating needs a system setting change).

**Next**
- Taseen tests on his second Android; fix what comes up; then retake screenshots and make the three videos.

## 2026-09-30 · 1.7: slide the edges
**Done**
- Left-edge volume tested on the Pixel 7. Found Android swallowing the first ~0.5 s of each slide when nothing
  plays (8 steps sent, 3 applied); switched to `adjustStreamVolume`, now 8 of 8. Back gesture still works.
- Right-edge brightness, no new permission. Tried SystemUI's brightness dialog as the indicator, then built our own
  slider that grows out of the camera hole and shrinks back in (Taseen's idea). Adaptive brightness stays on.
- "Slide the edges" tile with a sliding-hand icon and a status line; switches redrawn so on/off reads clearly and the
  knob morphs as it slides.
- Icons: app on Material Symbols, site on Lucide.
- Site: edge sliders section with an animated phone, double-tap demo, spec strip instead of chips, new screenshots,
  setup section fixed on phones.
- This vault and the Graphify graph.
- 1.7.1: remaining hand-drawn suns replaced with Material Symbols / Lucide, adaptive brightness measured with a real
  slide (holds, mode stays on), right-edge back gesture tested, setup step for "Display over other apps", screenshots
  retaken on the final build.

**Blocked**
- Play Store and domain decisions wait on Taseen.

**Next**
- Taseen to open a bank app with both edge sliders on and tap around: some banking apps block taps while another
  app draws an overlay.
- Watch for reports of the brightness pill clashing with apps that draw near the camera.
- Graphify doesn't parse XML, so `AndroidManifest.xml` and `res/` aren't in the code graph; [[Architecture]] covers them.

## Before 2026-09-30 · 1.0 to 1.6
Built the double-tap screen-off, lock-screen switch, Quick Settings tiles, wallpaper browser with Cutout designs,
theming, tab swiping, the website, and open-sourced it. See the git history (`git log`) for each release.
