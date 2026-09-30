---
tags: [tapoff, log]
---
# Progress log

Newest first. Each entry: done / blocked / next.

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

**Blocked**
- Play Store and domain decisions wait on Taseen.

**Next**
- Watch for reports of the brightness pill clashing with apps that draw near the camera.

## Before 2026-09-30 · 1.0 to 1.6
Built the double-tap screen-off, lock-screen switch, Quick Settings tiles, wallpaper browser with Cutout designs,
theming, tab swiping, the website, and open-sourced it. See the git history (`git log`) for each release.
