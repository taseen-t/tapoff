---
tags: [tapoff, log]
---
# Progress log

Newest first. Each entry: done / blocked / next.

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
- Shizuku PR only with Taseen's yes (candidate: a second `attachApplication` from one process crashes the server).
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
