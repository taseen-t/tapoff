# Graph Report - tapoff  (2026-10-03)

## Corpus Check
- 25 files · ~37,909 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 41 file(s) not represented in the graph (top: .xml 34, (none) 4, .aidl 3)

## Summary
- 472 nodes · 1204 edges · 22 communities (18 shown, 4 thin omitted)
- Extraction: 97% EXTRACTED · 3% INFERRED · 0% AMBIGUOUS · INFERRED: 34 edges (avg confidence: 0.92)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `57505eef`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- MainActivity
- MainActivity.java
- android.graphics.Bitmap
- android.graphics.Paint
- android.content.Context
- android.graphics.Canvas
- Progress log
- Wallpapers.java
- PreviewActivity
- GlassCard
- NotchPanel.java
- log
- TapOff
- Override
- build.sh
- ShizukuSetup.java
- uri
- imagebutton
- overshootinterpolator
- LockService
- LockService.java
- CutoutArt.java

## God Nodes (most connected - your core abstractions)
1. `MainActivity` - 56 edges
2. `PreviewActivity` - 29 edges
3. `CutoutArt` - 25 edges
4. `NotchPanel` - 25 edges
5. `Wallpapers` - 24 edges
6. `GlassCard` - 17 edges
7. `ShizukuSetup` - 17 edges
8. `TapWallpaper` - 17 edges
9. `Ui` - 17 edges
10. `Panel` - 14 edges

## Surprising Connections (you probably didn't know these)
- `Processes` --references--> `LockService`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/LockService.java
- `Processes` --references--> `MainActivity`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/MainActivity.java
- `Processes` --references--> `PreviewActivity`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/PreviewActivity.java
- `Decisions` --references--> `ShizukuSetup`  [INFERRED]
  docs/Decisions.md → src/com/taseen/tapoff/ShizukuSetup.java
- `Brightness and volume sliders (`NotchPanel.java`)` --references--> `SlidersActivity`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/SlidersActivity.java

## Import Cycles
- None detected.

## Communities (22 total, 4 thin omitted)

### Community 0 - "MainActivity"
Cohesion: 0.09
Nodes (16): android.graphics.drawable.GradientDrawable, android.widget.FrameLayout, android.widget.ImageView, android.widget.LinearLayout, android.widget.ScrollView, android.widget.Switch, android.widget.TextView, ArgbEvaluator (+8 more)

### Community 1 - "MainActivity.java"
Cohesion: 0.09
Nodes (23): android.content.Intent, android.graphics.Typeface, android.view.MotionEvent, android.view.VelocityTracker, android.widget.Button, colorstatelist, configuration, decelerateinterpolator (+15 more)

### Community 2 - "android.graphics.Bitmap"
Cohesion: 0.10
Nodes (20): android.app.WallpaperColors, android.graphics.Bitmap, android.service.wallpaper.WallpaperService, android.view.SurfaceHolder, BroadcastReceiver, ContentObserver, Architecture, Brightness and volume sliders (`NotchPanel.java`) (+12 more)

### Community 3 - "android.graphics.Paint"
Cohesion: 0.25
Nodes (8): acceleratedecelerateinterpolator, android.animation.ValueAnimator, android.graphics.drawable.Drawable, android.graphics.Paint, android.view.View, DoubleTapHint, Override, SwipeHint

### Community 4 - "android.content.Context"
Cohesion: 0.09
Nodes (11): android.app.Activity, android.content.Context, android.graphics.RectF, android.view.animation.OvershootInterpolator, LayoutParams, Override, NotchPanel, Panel (+3 more)

### Community 5 - "android.graphics.Canvas"
Cohesion: 0.36
Nodes (3): android.graphics.Canvas, CutoutArt, Override

### Community 6 - "Progress log"
Cohesion: 0.06
Nodes (34): Before every commit, Checks, Release, Session start, Taseen's own checks (every task, unasked), Testing on the phone, Keeping this current, Notes (+26 more)

### Community 7 - "Wallpapers.java"
Cohesion: 0.11
Nodes (18): arrays, comparator, consumer, fileoutputstream, httpurlconnection, imagedecoder, inputstream, jsonarray (+10 more)

### Community 8 - "PreviewActivity"
Cohesion: 0.11
Nodes (8): android.content.res.Resources, android.widget.ImageButton, android.widget.ProgressBar, Override, PreviewActivity, Collection, Item, Wallpapers

### Community 10 - "NotchPanel.java"
Cohesion: 0.12
Nodes (18): bitmapfactory, canvas, color, display, displaymanager, file, intent, ioexception (+10 more)

### Community 11 - "log"
Cohesion: 0.22
Nodes (7): android.media.AudioManager, android.service.quicksettings.TileService, log, Override, ScreenOffTile, Override, VolumeTile

### Community 12 - "TapOff"
Cohesion: 0.15
Nodes (12): Bugs, Build from source, Credits, How it works, Install, License, Privacy, TapOff (+4 more)

### Community 13 - "Override"
Cohesion: 0.24
Nodes (5): ColorFilter, Drawable, Override, Rect, SwitchPart

### Community 14 - "build.sh"
Cohesion: 0.50
Nodes (3): JAVA_HOME, PATH, build.sh script

### Community 15 - "ShizukuSetup.java"
Cohesion: 0.09
Nodes (21): android.content.ContentProvider, android.content.ContentValues, android.database.Cursor, android.net.Uri, android.os.Bundle, android.os.Handler, android.os.IBinder, android.os.Parcel (+13 more)

### Community 19 - "LockService"
Cohesion: 0.22
Nodes (6): android.accessibilityservice.AccessibilityService, android.view.accessibility.AccessibilityEvent, Decisions, Iris, Override, LockService

### Community 20 - "LockService.java"
Cohesion: 0.29
Nodes (6): android.view.WindowManager, componentname, contentresolver, files, packagemanager, standardcharsets

### Community 21 - "CutoutArt.java"
Cohesion: 0.16
Nodes (12): android.graphics.Matrix, arraylist, dashpatheffect, displaycutout, lineargradient, list, path, radialgradient (+4 more)

## Knowledge Gaps
- **46 isolated node(s):** `build.sh script`, `JAVA_HOME`, `PATH`, `PRIMARY`, `SECONDARY` (+41 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 113 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **4 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `MainActivity` connect `MainActivity` to `MainActivity.java`, `android.graphics.Bitmap`, `android.graphics.Paint`, `android.content.Context`, `GlassCard`, `Override`, `ShizukuSetup.java`?**
  _High betweenness centrality (0.149) - this node is a cross-community bridge._
- **Why does `Decisions` connect `LockService` to `android.content.Context`, `Progress log`, `ShizukuSetup.java`?**
  _High betweenness centrality (0.085) - this node is a cross-community bridge._
- **Why does `Architecture` connect `android.graphics.Bitmap` to `MainActivity`, `Progress log`, `ShizukuSetup.java`?**
  _High betweenness centrality (0.079) - this node is a cross-community bridge._
- **What connects `build.sh script`, `JAVA_HOME`, `PATH` to the rest of the system?**
  _46 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `MainActivity` be split into smaller, more focused modules?**
  _Cohesion score 0.0937062937062937 - nodes in this community are weakly interconnected._
- **Should `MainActivity.java` be split into smaller, more focused modules?**
  _Cohesion score 0.08522727272727272 - nodes in this community are weakly interconnected._
- **Should `android.graphics.Bitmap` be split into smaller, more focused modules?**
  _Cohesion score 0.1 - nodes in this community are weakly interconnected._