# Graph Report - tapoff  (2026-09-30)

## Corpus Check
- 24 files · ~31,488 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 41 file(s) not represented in the graph (top: .xml 34, (none) 4, .aidl 3)

## Summary
- 458 nodes · 1186 edges · 19 communities (16 shown, 3 thin omitted)
- Extraction: 97% EXTRACTED · 3% INFERRED · 0% AMBIGUOUS · INFERRED: 34 edges (avg confidence: 0.92)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `46bb77fe`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- MainActivity
- MainActivity.java
- TapWallpaper
- android.graphics.Paint
- android.content.Context
- android.graphics.Canvas
- Progress log
- Wallpapers.java
- Wallpapers
- Override
- log
- TapOff
- LockService.java
- build.sh
- ShizukuSetup.java
- uri
- imagebutton
- overshootinterpolator
- NotchPanel.java

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
- `Processes` --references--> `MainActivity`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/MainActivity.java
- `Processes` --references--> `PreviewActivity`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/PreviewActivity.java
- `Decisions` --references--> `ShizukuSetup`  [INFERRED]
  docs/Decisions.md → src/com/taseen/tapoff/ShizukuSetup.java
- `Brightness and volume sliders (`NotchPanel.java`)` --references--> `SlidersActivity`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/SlidersActivity.java
- `Decisions` --references--> `SlidersActivity`  [INFERRED]
  docs/Decisions.md → src/com/taseen/tapoff/SlidersActivity.java

## Import Cycles
- None detected.

## Communities (19 total, 3 thin omitted)

### Community 0 - "MainActivity"
Cohesion: 0.09
Nodes (15): android.graphics.drawable.GradientDrawable, android.widget.FrameLayout, android.widget.ImageView, android.widget.LinearLayout, android.widget.Switch, android.widget.TextView, ArgbEvaluator, UI (+7 more)

### Community 1 - "MainActivity.java"
Cohesion: 0.06
Nodes (31): android.app.Activity, android.content.Intent, android.graphics.Typeface, android.view.MotionEvent, android.view.VelocityTracker, android.widget.Button, android.widget.ImageButton, android.widget.ProgressBar (+23 more)

### Community 2 - "TapWallpaper"
Cohesion: 0.08
Nodes (23): android.accessibilityservice.AccessibilityService, android.app.WallpaperColors, android.service.wallpaper.WallpaperService, android.view.accessibility.AccessibilityEvent, android.view.SurfaceHolder, BroadcastReceiver, ContentObserver, Architecture (+15 more)

### Community 3 - "android.graphics.Paint"
Cohesion: 0.17
Nodes (13): acceleratedecelerateinterpolator, android.animation.ValueAnimator, android.graphics.drawable.Drawable, android.graphics.Matrix, android.graphics.Paint, android.view.View, lineargradient, path (+5 more)

### Community 4 - "android.content.Context"
Cohesion: 0.11
Nodes (8): android.content.Context, android.graphics.RectF, android.view.animation.OvershootInterpolator, LayoutParams, Override, NotchPanel, Panel, Override

### Community 5 - "android.graphics.Canvas"
Cohesion: 0.36
Nodes (3): android.graphics.Canvas, CutoutArt, Override

### Community 6 - "Progress log"
Cohesion: 0.09
Nodes (21): Before every commit, Checks, Release, Session start, Taseen's own checks (every task, unasked), Testing on the phone, Keeping this current, Notes (+13 more)

### Community 7 - "Wallpapers.java"
Cohesion: 0.11
Nodes (18): arrays, comparator, consumer, fileoutputstream, httpurlconnection, imagedecoder, inputstream, jsonarray (+10 more)

### Community 8 - "Wallpapers"
Cohesion: 0.11
Nodes (9): android.content.res.Resources, android.graphics.Bitmap, Wallpapers, GlassCard, Override, Collection, Item, Rect (+1 more)

### Community 10 - "Override"
Cohesion: 0.24
Nodes (5): ColorFilter, Drawable, Override, Rect, SwitchPart

### Community 11 - "log"
Cohesion: 0.22
Nodes (7): android.media.AudioManager, android.service.quicksettings.TileService, log, Override, ScreenOffTile, Override, VolumeTile

### Community 12 - "TapOff"
Cohesion: 0.15
Nodes (12): Bugs, Build from source, Credits, How it works, Install, License, Privacy, TapOff (+4 more)

### Community 13 - "LockService.java"
Cohesion: 0.14
Nodes (14): android.view.WindowManager, arraylist, componentname, contentresolver, dashpatheffect, displaycutout, files, list (+6 more)

### Community 14 - "build.sh"
Cohesion: 0.50
Nodes (3): JAVA_HOME, PATH, build.sh script

### Community 15 - "ShizukuSetup.java"
Cohesion: 0.09
Nodes (21): android.content.ContentProvider, android.content.ContentValues, android.database.Cursor, android.net.Uri, android.os.Bundle, android.os.Handler, android.os.IBinder, android.os.Parcel (+13 more)

### Community 19 - "NotchPanel.java"
Cohesion: 0.13
Nodes (17): bitmapfactory, canvas, color, display, displaymanager, file, intent, ioexception (+9 more)

## Knowledge Gaps
- **35 isolated node(s):** `build.sh script`, `JAVA_HOME`, `PATH`, `PRIMARY`, `SECONDARY` (+30 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 102 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **3 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `MainActivity` connect `MainActivity` to `MainActivity.java`, `TapWallpaper`, `android.graphics.Paint`, `Wallpapers`, `Override`, `ShizukuSetup.java`?**
  _High betweenness centrality (0.153) - this node is a cross-community bridge._
- **Why does `Architecture` connect `TapWallpaper` to `MainActivity`, `Wallpapers`, `Progress log`, `ShizukuSetup.java`?**
  _High betweenness centrality (0.081) - this node is a cross-community bridge._
- **Why does `NotchPanel` connect `android.content.Context` to `log`, `TapWallpaper`, `NotchPanel.java`, `LockService.java`?**
  _High betweenness centrality (0.060) - this node is a cross-community bridge._
- **What connects `build.sh script`, `JAVA_HOME`, `PATH` to the rest of the system?**
  _35 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `MainActivity` be split into smaller, more focused modules?**
  _Cohesion score 0.09471153846153846 - nodes in this community are weakly interconnected._
- **Should `MainActivity.java` be split into smaller, more focused modules?**
  _Cohesion score 0.05807622504537205 - nodes in this community are weakly interconnected._
- **Should `TapWallpaper` be split into smaller, more focused modules?**
  _Cohesion score 0.07676767676767676 - nodes in this community are weakly interconnected._