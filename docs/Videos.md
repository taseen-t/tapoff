---
tags: [tapoff, videos]
---
# Videos

How TapOff's videos are made. The work folder (`brag-output-2026-09-30-2030/` in the repo root) is git-ignored and
never pushed: it holds raw phone recordings with personal details in them. This note is the recipe; see [[Decisions]]
#37 and #39–42 for the why.

## Shared style
- **Frames are HTML.** Every frame is a pure function of time, rendered by Playwright (Chrome) and encoded by ffmpeg.
  No timers or unseeded randomness, so any frame re-renders identically.
- **Look:** the site's dot grid, Silkscreen pixel type, TapOff's pixel hand, and a 3D phone (three.js, "Google Pixel 7
  Pro" by Aborsoft 3D, CC BY 4.0, credited on each video's end card) in black glass with a silver bar, inside a clear
  acrylic case that only shows reflections. No reflections on the screen.
- **Voice:** ElevenLabs `eleven_v3`, voice "Jerry B. – Hyper-Real". One line per request: asking for many at once got
  the account's free tier blocked for a while.
- **Sound:** chiptune bed and effects written in code (numpy), in key with each other, music ducked under the voice,
  normalised to about −14 LUFS.

## Brag video (portrait + landscape, 23 s)
`work/scene.html` (scene), `work/render.js` (frames), `work/mix.py` (sound). Real recordings of the double-tap
screen-off and the back-tap sliders play on the 3D phone's screen.

## Setup guide (portrait, ~99 s)
Install → Play Protect workaround → Shizuku → Developer options → Wireless debugging → pair and start Shizuku →
TapOff's permission → wallpaper → optional sliders → it works. Built from `guide/`:

| Step | File |
|---|---|
| Which source seconds the edit may use | `ranges.py` |
| Shots, voice pieces, captions, taps, zooms → `timeline.json` | `timeline.py` |
| The page (template + the brag scene's 3D phone module) | `guide.template.html`, `build_page.py` → `guide.html` |
| Frames | `render_guide.js` |
| Sound | `mix_guide.py` |

### Privacy pipeline (from the UplinkeSIM videos' method)
1. **Extract** only the used ranges, numbered by absolute frame (`fps=30,select=…,-frame_pts 1`). Input seeking on
   screen recordings drifts, because a static screen writes few frames.
2. **Scan** every frame with an on-device Apple Vision OCR tool (`tools/ocr`, built from `tools/ocr.swift`). Its
   "accurate" text model fails on this macOS build (e5rt error 13), so it runs in "fast" mode.
3. **Blur** (`tools/redact.py`): anything `tools/sensitive.py` flags (names, emails, computer and network host names,
   IP/MAC addresses, other downloads, long numbers), plus region rules (Play Store profile photo, account menu,
   everything around installer dialogs on Chrome's Downloads page). Detections are tracked across frames and held a
   few frames past their ends. Only blurred frames go to `safe/`, with an allowlist the page refuses to go outside.
4. **Prove it** (`tools/verify.py`): re-scan every safe frame; any hit is a leak and feeds the next blur pass.
   Result for the guide: 3,142 frames, 0 leaks.
5. **Check by eye:** contact sheets of every blurred range. This caught what OCR missed (a file name read with spaces,
   the edge of an account photo, faint text behind a dimmed dialog).
6. About phone is never filmed (device identifiers); the Build-number step is drawn instead, with Android's own toasts.

### On the site
`site/video/setup-guide.mp4` is a 720×1280 copy (8.7 MB) with a WebP poster, beside the setup steps; see
[[Decisions]] #42.

### Rebuild
```
python3 tools/scan.py && python3 tools/redact.py && python3 tools/verify.py
python3 timeline.py && python3 build_page.py
python3 -m http.server 8766 --bind 127.0.0.1   # from the work folder root
node render_guide.js frames && python3 mix_guide.py
```
