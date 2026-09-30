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
  - [ ] Record TapOff on the phone over USB
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
- [x] Remove the camera-tap setting and its code
- [x] Button stops saying "Back tap is set up" when Quick Tap is changed or turned off
- [ ] Back tap not opening the sliders on Taseen's phone: Quick Tap was off; to retest with it on (and with "Take
      screenshot" to rule out the acrylic case)
- [ ] Taseen tests on a second Android, then fixes
- [ ] Refresh site screenshots (tile changed) with the video footage

## Open
- [ ] Play Store listing? Costs $25 once. Waiting on Taseen (never spend without a yes).
- [ ] Custom domain? Waiting on whether Taseen is a student (GitHub Student Pack has free domains).
