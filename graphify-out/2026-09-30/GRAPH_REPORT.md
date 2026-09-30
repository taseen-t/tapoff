# Graph Report - tapoff  (2026-09-30)

## Corpus Check
- 20 files · ~22,907 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 31 file(s) not represented in the graph (top: .xml 27, (none) 4)

## Summary
- 367 nodes · 937 edges · 16 communities (15 shown, 1 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 22 edges (avg confidence: 0.92)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `3095d21a`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- android.content.Context
- MainActivity.java
- TapWallpaper
- CutoutArt.java
- EdgeSlider
- android.graphics.Canvas
- Checks.md
- Wallpapers.java
- Wallpapers
- EdgeSlider.java
- Override
- log
- TapOff
- LockService.java
- build.sh
- Pill

## God Nodes (most connected - your core abstractions)
1. `MainActivity` - 50 edges
2. `CutoutArt` - 25 edges
3. `PreviewActivity` - 22 edges
4. `EdgeSlider` - 21 edges
5. `Wallpapers` - 18 edges
6. `Ui` - 17 edges
7. `GlassCard` - 16 edges
8. `TapWallpaper` - 14 edges
9. `TapEngine` - 11 edges
10. `Notch` - 10 edges

## Surprising Connections (you probably didn't know these)
- `Processes` --references--> `MainActivity`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/MainActivity.java
- `Processes` --references--> `PreviewActivity`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/PreviewActivity.java
- `Processes` --references--> `LockService`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/LockService.java
- `Processes` --references--> `TapWallpaper`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/TapWallpaper.java
- `Screen off` --references--> `TapWallpaper`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/TapWallpaper.java

## Import Cycles
- None detected.

## Communities (16 total, 1 thin omitted)

### Community 0 - "android.content.Context"
Cohesion: 0.13
Nodes (12): android.content.Context, android.graphics.drawable.GradientDrawable, android.widget.FrameLayout, android.widget.ImageView, android.widget.LinearLayout, android.widget.ScrollView, android.widget.Switch, android.widget.TextView (+4 more)

### Community 1 - "MainActivity.java"
Cohesion: 0.07
Nodes (30): android.app.Activity, android.content.Intent, android.content.res.Configuration, android.graphics.Typeface, android.os.Bundle, android.view.VelocityTracker, android.widget.Button, android.widget.ProgressBar (+22 more)

### Community 2 - "TapWallpaper"
Cohesion: 0.08
Nodes (21): android.accessibilityservice.AccessibilityService, android.app.WallpaperColors, android.graphics.Paint, android.service.wallpaper.WallpaperService, android.view.accessibility.AccessibilityEvent, android.view.SurfaceHolder, BroadcastReceiver, Architecture (+13 more)

### Community 3 - "CutoutArt.java"
Cohesion: 0.08
Nodes (21): acceleratedecelerateinterpolator, android.animation.ValueAnimator, android.graphics.Bitmap, android.graphics.drawable.Drawable, android.graphics.Matrix, android.view.View, arraylist, dashpatheffect (+13 more)

### Community 4 - "EdgeSlider"
Cohesion: 0.10
Nodes (10): android.graphics.RectF, android.view.MotionEvent, CompoundButton, Edge sliders (`EdgeSlider.java`), UI, LayoutParams, EdgeSlider, Drawable (+2 more)

### Community 6 - "Checks.md"
Cohesion: 0.11
Nodes (17): Before every commit, Checks, Release, Session start, Taseen's own checks (every task, unasked), Testing on the phone, Decisions, Keeping this current (+9 more)

### Community 7 - "Wallpapers.java"
Cohesion: 0.10
Nodes (20): arrays, comparator, consumer, fileoutputstream, files, httpurlconnection, imagedecoder, inputstream (+12 more)

### Community 8 - "Wallpapers"
Cohesion: 0.23
Nodes (4): android.content.res.Resources, Collection, Item, Wallpapers

### Community 9 - "EdgeSlider.java"
Cohesion: 0.15
Nodes (14): bitmapfactory, canvas, display, displaymanager, file, intent, ioexception, overshootinterpolator (+6 more)

### Community 10 - "Override"
Cohesion: 0.16
Nodes (5): ColorFilter, Drawable, Rect, Override, SwitchPart

### Community 11 - "log"
Cohesion: 0.28
Nodes (6): android.media.AudioManager, android.service.quicksettings.TileService, log, ScreenOffTile, Override, VolumeTile

### Community 12 - "TapOff"
Cohesion: 0.20
Nodes (9): Bugs, Build from source, Credits, How it works, Install, License, Privacy, TapOff (+1 more)

### Community 13 - "LockService.java"
Cohesion: 0.22
Nodes (8): android.view.WindowManager, color, componentname, contentresolver, manifest, packagemanager, pixelformat, settings

### Community 14 - "build.sh"
Cohesion: 0.50
Nodes (3): JAVA_HOME, PATH, build.sh script

### Community 15 - "Pill"
Cohesion: 0.50
Nodes (4): Pill, ON_PHOTO, PRIMARY, SECONDARY

## Knowledge Gaps
- **29 isolated node(s):** `build.sh script`, `JAVA_HOME`, `PATH`, `PRIMARY`, `SECONDARY` (+24 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 92 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **1 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `MainActivity` connect `android.content.Context` to `MainActivity.java`, `TapWallpaper`, `CutoutArt.java`, `EdgeSlider`, `Override`?**
  _High betweenness centrality (0.184) - this node is a cross-community bridge._
- **Why does `Architecture` connect `TapWallpaper` to `EdgeSlider`, `Checks.md`?**
  _High betweenness centrality (0.126) - this node is a cross-community bridge._
- **Why does `Processes` connect `TapWallpaper` to `android.content.Context`, `MainActivity.java`?**
  _High betweenness centrality (0.079) - this node is a cross-community bridge._
- **What connects `build.sh script`, `JAVA_HOME`, `PATH` to the rest of the system?**
  _29 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `android.content.Context` be split into smaller, more focused modules?**
  _Cohesion score 0.13425253991291727 - nodes in this community are weakly interconnected._
- **Should `MainActivity.java` be split into smaller, more focused modules?**
  _Cohesion score 0.06976744186046512 - nodes in this community are weakly interconnected._
- **Should `TapWallpaper` be split into smaller, more focused modules?**
  _Cohesion score 0.08292682926829269 - nodes in this community are weakly interconnected._