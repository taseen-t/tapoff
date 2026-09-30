# Graph Report - tapoff  (2026-09-30)

## Corpus Check
- 22 files · ~27,356 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 38 file(s) not represented in the graph (top: .xml 34, (none) 4)

## Summary
- 419 nodes · 1114 edges · 20 communities (17 shown, 3 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 27 edges (avg confidence: 0.92)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `1dae3cfa`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- android.content.Context
- MainActivity.java
- TapWallpaper
- android.view.View
- NotchPanel
- android.graphics.Canvas
- Checks.md
- Wallpapers.java
- PreviewActivity
- NotchPanel.java
- Override
- log
- TapOff
- CutoutArt.java
- build.sh
- Pill
- android.graphics.Bitmap
- imagebutton
- overshootinterpolator
- SlidersActivity.java

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
- `Processes` --references--> `MainActivity`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/MainActivity.java
- `Processes` --references--> `PreviewActivity`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/PreviewActivity.java
- `Brightness and volume sliders (`NotchPanel.java`)` --references--> `SlidersActivity`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/SlidersActivity.java
- `Decisions` --references--> `SlidersActivity`  [INFERRED]
  docs/Decisions.md → src/com/taseen/tapoff/SlidersActivity.java
- `Processes` --references--> `LockService`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/LockService.java

## Import Cycles
- None detected.

## Communities (20 total, 3 thin omitted)

### Community 0 - "android.content.Context"
Cohesion: 0.10
Nodes (16): android.content.Context, android.graphics.drawable.GradientDrawable, android.graphics.Typeface, android.widget.Button, android.widget.FrameLayout, android.widget.ImageView, android.widget.LinearLayout, android.widget.ScrollView (+8 more)

### Community 1 - "MainActivity.java"
Cohesion: 0.10
Nodes (20): android.content.Intent, android.os.Bundle, android.view.MotionEvent, android.view.VelocityTracker, android.widget.ImageButton, android.widget.ProgressBar, colorstatelist, decelerateinterpolator (+12 more)

### Community 2 - "TapWallpaper"
Cohesion: 0.07
Nodes (23): android.accessibilityservice.AccessibilityService, android.app.WallpaperColors, android.service.wallpaper.WallpaperService, android.view.accessibility.AccessibilityEvent, android.view.SurfaceHolder, BroadcastReceiver, Architecture, Brightness and volume sliders (`NotchPanel.java`) (+15 more)

### Community 3 - "android.view.View"
Cohesion: 0.10
Nodes (18): acceleratedecelerateinterpolator, android.animation.ValueAnimator, android.graphics.drawable.Drawable, android.graphics.Matrix, android.graphics.Paint, android.view.animation.OvershootInterpolator, android.view.View, lineargradient (+10 more)

### Community 4 - "NotchPanel"
Cohesion: 0.12
Nodes (4): android.graphics.RectF, LayoutParams, NotchPanel, Override

### Community 6 - "Checks.md"
Cohesion: 0.11
Nodes (18): Before every commit, Checks, Release, Session start, Taseen's own checks (every task, unasked), Testing on the phone, Keeping this current, Notes (+10 more)

### Community 7 - "Wallpapers.java"
Cohesion: 0.10
Nodes (20): arrays, comparator, consumer, fileoutputstream, files, httpurlconnection, imagedecoder, inputstream (+12 more)

### Community 8 - "PreviewActivity"
Cohesion: 0.12
Nodes (6): android.content.res.Resources, Override, PreviewActivity, Collection, Item, Wallpapers

### Community 9 - "NotchPanel.java"
Cohesion: 0.16
Nodes (13): bitmapfactory, canvas, configuration, display, displaymanager, gravity, linearinterpolator, systemclock (+5 more)

### Community 10 - "Override"
Cohesion: 0.20
Nodes (6): ColorFilter, Drawable, Configuration, Override, Rect, SwitchPart

### Community 11 - "log"
Cohesion: 0.28
Nodes (6): android.media.AudioManager, android.service.quicksettings.TileService, log, ScreenOffTile, Override, VolumeTile

### Community 12 - "TapOff"
Cohesion: 0.20
Nodes (9): Bugs, Build from source, Credits, How it works, Install, License, Privacy, TapOff (+1 more)

### Community 13 - "CutoutArt.java"
Cohesion: 0.12
Nodes (15): android.view.WindowManager, arraylist, color, componentname, contentresolver, dashpatheffect, displaycutout, list (+7 more)

### Community 14 - "build.sh"
Cohesion: 0.50
Nodes (3): JAVA_HOME, PATH, build.sh script

### Community 15 - "Pill"
Cohesion: 0.50
Nodes (4): Pill, ON_PHOTO, PRIMARY, SECONDARY

### Community 16 - "android.graphics.Bitmap"
Cohesion: 0.20
Nodes (3): android.graphics.Bitmap, GlassCard, Override

### Community 19 - "SlidersActivity.java"
Cohesion: 0.20
Nodes (9): android.app.Activity, Decisions, file, intent, ioexception, Context, SlidersActivity, toast (+1 more)

## Knowledge Gaps
- **30 isolated node(s):** `build.sh script`, `JAVA_HOME`, `PATH`, `PRIMARY`, `SECONDARY` (+25 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 93 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **3 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `MainActivity` connect `android.content.Context` to `MainActivity.java`, `TapWallpaper`, `android.view.View`, `NotchPanel`, `Override`, `android.graphics.Bitmap`, `SlidersActivity.java`?**
  _High betweenness centrality (0.169) - this node is a cross-community bridge._
- **Why does `Architecture` connect `TapWallpaper` to `android.content.Context`, `Checks.md`?**
  _High betweenness centrality (0.104) - this node is a cross-community bridge._
- **Why does `NotchPanel` connect `NotchPanel` to `android.content.Context`, `TapWallpaper`, `android.view.View`, `NotchPanel.java`, `log`, `CutoutArt.java`?**
  _High betweenness centrality (0.078) - this node is a cross-community bridge._
- **What connects `build.sh script`, `JAVA_HOME`, `PATH` to the rest of the system?**
  _30 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `android.content.Context` be split into smaller, more focused modules?**
  _Cohesion score 0.09745390693590869 - nodes in this community are weakly interconnected._
- **Should `MainActivity.java` be split into smaller, more focused modules?**
  _Cohesion score 0.09686609686609686 - nodes in this community are weakly interconnected._
- **Should `TapWallpaper` be split into smaller, more focused modules?**
  _Cohesion score 0.07123034227567067 - nodes in this community are weakly interconnected._