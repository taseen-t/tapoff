# Graph Report - tapoff  (2026-09-30)

## Corpus Check
- 22 files · ~27,160 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 38 file(s) not represented in the graph (top: .xml 34, (none) 4)

## Summary
- 416 nodes · 1103 edges · 19 communities (16 shown, 3 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 26 edges (avg confidence: 0.92)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `1dae3cfa`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- android.content.Context
- MainActivity.java
- TapWallpaper
- android.graphics.Bitmap
- NotchPanel
- android.graphics.Canvas
- Checks.md
- Wallpapers.java
- Wallpapers
- NotchPanel.java
- Override
- log
- TapOff
- LockService.java
- build.sh
- Pill
- LockService
- imagebutton
- overshootinterpolator

## God Nodes (most connected - your core abstractions)
1. `MainActivity` - 55 edges
2. `NotchPanel` - 31 edges
3. `PreviewActivity` - 29 edges
4. `CutoutArt` - 25 edges
5. `Wallpapers` - 24 edges
6. `GlassCard` - 17 edges
7. `Ui` - 17 edges
8. `TapWallpaper` - 16 edges
9. `Panel` - 14 edges
10. `TapEngine` - 11 edges

## Surprising Connections (you probably didn't know these)
- `Processes` --references--> `LockService`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/LockService.java
- `Processes` --references--> `MainActivity`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/MainActivity.java
- `Processes` --references--> `PreviewActivity`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/PreviewActivity.java
- `Brightness and volume sliders (`NotchPanel.java`)` --references--> `SlidersActivity`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/SlidersActivity.java
- `Brightness and volume sliders (`NotchPanel.java`)` --references--> `TapWallpaper`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/TapWallpaper.java

## Import Cycles
- None detected.

## Communities (19 total, 3 thin omitted)

### Community 0 - "android.content.Context"
Cohesion: 0.09
Nodes (12): android.content.Context, android.graphics.drawable.GradientDrawable, android.widget.FrameLayout, android.widget.ImageView, android.widget.LinearLayout, android.widget.Switch, android.widget.TextView, ArgbEvaluator (+4 more)

### Community 1 - "MainActivity.java"
Cohesion: 0.06
Nodes (33): android.app.Activity, android.content.Intent, android.graphics.Typeface, android.os.Bundle, android.view.MotionEvent, android.view.VelocityTracker, android.widget.Button, android.widget.ImageButton (+25 more)

### Community 2 - "TapWallpaper"
Cohesion: 0.10
Nodes (19): android.app.WallpaperColors, android.service.wallpaper.WallpaperService, android.view.SurfaceHolder, BroadcastReceiver, Architecture, Brightness and volume sliders (`NotchPanel.java`), Icons, Processes (+11 more)

### Community 3 - "android.graphics.Bitmap"
Cohesion: 0.06
Nodes (28): acceleratedecelerateinterpolator, android.animation.ValueAnimator, android.graphics.Bitmap, android.graphics.drawable.Drawable, android.graphics.Matrix, android.graphics.Paint, android.view.animation.OvershootInterpolator, android.view.View (+20 more)

### Community 4 - "NotchPanel"
Cohesion: 0.13
Nodes (4): android.graphics.RectF, LayoutParams, NotchPanel, Override

### Community 6 - "Checks.md"
Cohesion: 0.10
Nodes (19): Before every commit, Checks, Release, Session start, Taseen's own checks (every task, unasked), Testing on the phone, Decisions, Keeping this current (+11 more)

### Community 7 - "Wallpapers.java"
Cohesion: 0.10
Nodes (20): arrays, comparator, consumer, fileoutputstream, files, httpurlconnection, imagedecoder, inputstream (+12 more)

### Community 8 - "Wallpapers"
Cohesion: 0.20
Nodes (4): android.content.res.Resources, Collection, Item, Wallpapers

### Community 9 - "NotchPanel.java"
Cohesion: 0.16
Nodes (14): bitmapfactory, canvas, display, displaymanager, file, intent, ioexception, linearinterpolator (+6 more)

### Community 10 - "Override"
Cohesion: 0.24
Nodes (5): ColorFilter, Drawable, Override, Rect, SwitchPart

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

### Community 16 - "LockService"
Cohesion: 0.32
Nodes (4): android.accessibilityservice.AccessibilityService, android.view.accessibility.AccessibilityEvent, Override, LockService

## Knowledge Gaps
- **31 isolated node(s):** `build.sh script`, `JAVA_HOME`, `PATH`, `PRIMARY`, `SECONDARY` (+26 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 94 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **3 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `MainActivity` connect `android.content.Context` to `Override`, `MainActivity.java`, `TapWallpaper`, `android.graphics.Bitmap`?**
  _High betweenness centrality (0.172) - this node is a cross-community bridge._
- **Why does `Architecture` connect `TapWallpaper` to `android.content.Context`, `Checks.md`?**
  _High betweenness centrality (0.120) - this node is a cross-community bridge._
- **Why does `NotchPanel` connect `NotchPanel` to `android.content.Context`, `TapWallpaper`, `android.graphics.Bitmap`, `NotchPanel.java`, `log`, `LockService.java`?**
  _High betweenness centrality (0.079) - this node is a cross-community bridge._
- **What connects `build.sh script`, `JAVA_HOME`, `PATH` to the rest of the system?**
  _31 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `android.content.Context` be split into smaller, more focused modules?**
  _Cohesion score 0.09233176838810642 - nodes in this community are weakly interconnected._
- **Should `MainActivity.java` be split into smaller, more focused modules?**
  _Cohesion score 0.06009783368273934 - nodes in this community are weakly interconnected._
- **Should `TapWallpaper` be split into smaller, more focused modules?**
  _Cohesion score 0.09915966386554621 - nodes in this community are weakly interconnected._