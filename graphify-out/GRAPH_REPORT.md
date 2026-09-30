# Graph Report - tapoff  (2026-09-30)

## Corpus Check
- 20 files · ~23,186 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 31 file(s) not represented in the graph (top: .xml 27, (none) 4)

## Summary
- 367 nodes · 938 edges · 14 communities (12 shown, 2 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 22 edges (avg confidence: 0.92)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `c723c301`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- android.content.Context
- MainActivity.java
- LockService.java
- CutoutArt.java
- EdgeSlider
- android.graphics.Canvas
- Checks.md
- Wallpapers.java
- Wallpapers
- .onDraw
- Override
- TapOff
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

## Communities (14 total, 2 thin omitted)

### Community 0 - "android.content.Context"
Cohesion: 0.13
Nodes (12): android.content.Context, android.graphics.drawable.GradientDrawable, android.widget.FrameLayout, android.widget.ImageView, android.widget.LinearLayout, android.widget.ScrollView, android.widget.Switch, android.widget.TextView (+4 more)

### Community 1 - "MainActivity.java"
Cohesion: 0.07
Nodes (30): android.app.Activity, android.content.Intent, android.content.res.Configuration, android.graphics.Typeface, android.os.Bundle, android.view.VelocityTracker, android.widget.Button, android.widget.ProgressBar (+22 more)

### Community 2 - "LockService.java"
Cohesion: 0.07
Nodes (28): android.accessibilityservice.AccessibilityService, android.app.WallpaperColors, android.service.wallpaper.WallpaperService, android.view.accessibility.AccessibilityEvent, android.view.SurfaceHolder, android.view.WindowManager, BroadcastReceiver, color (+20 more)

### Community 3 - "CutoutArt.java"
Cohesion: 0.07
Nodes (26): acceleratedecelerateinterpolator, android.animation.ValueAnimator, android.graphics.Bitmap, android.graphics.drawable.Drawable, android.graphics.Matrix, android.graphics.Paint, android.graphics.RectF, android.view.View (+18 more)

### Community 4 - "EdgeSlider"
Cohesion: 0.18
Nodes (4): android.view.MotionEvent, CompoundButton, Edge sliders (`EdgeSlider.java`), EdgeSlider

### Community 6 - "Checks.md"
Cohesion: 0.11
Nodes (17): Before every commit, Checks, Release, Session start, Taseen's own checks (every task, unasked), Testing on the phone, Decisions, Keeping this current (+9 more)

### Community 7 - "Wallpapers.java"
Cohesion: 0.05
Nodes (40): android.media.AudioManager, android.service.quicksettings.TileService, arrays, bitmapfactory, canvas, comparator, consumer, display (+32 more)

### Community 8 - "Wallpapers"
Cohesion: 0.23
Nodes (4): android.content.res.Resources, Collection, Item, Wallpapers

### Community 10 - "Override"
Cohesion: 0.16
Nodes (5): ColorFilter, Drawable, Rect, Override, SwitchPart

### Community 12 - "TapOff"
Cohesion: 0.20
Nodes (9): Bugs, Build from source, Credits, How it works, Install, License, Privacy, TapOff (+1 more)

### Community 14 - "build.sh"
Cohesion: 0.50
Nodes (3): JAVA_HOME, PATH, build.sh script

### Community 15 - "Pill"
Cohesion: 0.50
Nodes (4): Pill, ON_PHOTO, PRIMARY, SECONDARY

## Knowledge Gaps
- **29 isolated node(s):** `build.sh script`, `JAVA_HOME`, `PATH`, `PRIMARY`, `SECONDARY` (+24 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 92 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **2 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `MainActivity` connect `android.content.Context` to `MainActivity.java`, `LockService.java`, `CutoutArt.java`, `EdgeSlider`, `Override`?**
  _High betweenness centrality (0.184) - this node is a cross-community bridge._
- **Why does `Architecture` connect `LockService.java` to `.onDraw`, `EdgeSlider`, `Checks.md`?**
  _High betweenness centrality (0.126) - this node is a cross-community bridge._
- **Why does `Processes` connect `LockService.java` to `android.content.Context`, `MainActivity.java`?**
  _High betweenness centrality (0.079) - this node is a cross-community bridge._
- **What connects `build.sh script`, `JAVA_HOME`, `PATH` to the rest of the system?**
  _29 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `android.content.Context` be split into smaller, more focused modules?**
  _Cohesion score 0.13425253991291727 - nodes in this community are weakly interconnected._
- **Should `MainActivity.java` be split into smaller, more focused modules?**
  _Cohesion score 0.06976744186046512 - nodes in this community are weakly interconnected._
- **Should `LockService.java` be split into smaller, more focused modules?**
  _Cohesion score 0.0663265306122449 - nodes in this community are weakly interconnected._