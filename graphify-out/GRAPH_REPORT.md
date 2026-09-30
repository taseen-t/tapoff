# Graph Report - tapoff  (2026-09-30)

## Corpus Check
- 22 files · ~28,037 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 38 file(s) not represented in the graph (top: .xml 34, (none) 4)

## Summary
- 416 nodes · 1101 edges · 19 communities (16 shown, 3 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 27 edges (avg confidence: 0.91)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `a7207280`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- MainActivity
- MainActivity.java
- TapWallpaper
- android.graphics.Paint
- NotchPanel
- android.graphics.Canvas
- Checks.md
- Wallpapers.java
- android.content.Context
- TapWallpaper.java
- Override
- log
- TapOff
- LockService.java
- build.sh
- android.graphics.Bitmap
- imagebutton
- overshootinterpolator
- NotchPanel.java

## God Nodes (most connected - your core abstractions)
1. `MainActivity` - 55 edges
2. `PreviewActivity` - 29 edges
3. `CutoutArt` - 25 edges
4. `NotchPanel` - 25 edges
5. `Wallpapers` - 24 edges
6. `GlassCard` - 17 edges
7. `TapWallpaper` - 17 edges
8. `Ui` - 17 edges
9. `Panel` - 14 edges
10. `LockService` - 11 edges

## Surprising Connections (you probably didn't know these)
- `Decisions` --references--> `SlidersActivity`  [INFERRED]
  docs/Decisions.md → src/com/taseen/tapoff/SlidersActivity.java
- `Processes` --references--> `MainActivity`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/MainActivity.java
- `Processes` --references--> `PreviewActivity`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/PreviewActivity.java
- `Brightness and volume sliders (`NotchPanel.java`)` --references--> `SlidersActivity`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/SlidersActivity.java
- `Wallpapers` --references--> `TapWallpaper`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/TapWallpaper.java

## Import Cycles
- None detected.

## Communities (19 total, 3 thin omitted)

### Community 0 - "MainActivity"
Cohesion: 0.11
Nodes (15): android.graphics.drawable.GradientDrawable, android.widget.FrameLayout, android.widget.LinearLayout, android.widget.ScrollView, android.widget.Switch, android.widget.TextView, ArgbEvaluator, UI (+7 more)

### Community 1 - "MainActivity.java"
Cohesion: 0.07
Nodes (28): android.content.Intent, android.graphics.Typeface, android.os.Bundle, android.view.MotionEvent, android.view.VelocityTracker, android.widget.Button, android.widget.ImageButton, android.widget.ImageView (+20 more)

### Community 2 - "TapWallpaper"
Cohesion: 0.08
Nodes (22): android.accessibilityservice.AccessibilityService, android.app.WallpaperColors, android.service.wallpaper.WallpaperService, android.view.accessibility.AccessibilityEvent, android.view.SurfaceHolder, BroadcastReceiver, ContentObserver, Architecture (+14 more)

### Community 3 - "android.graphics.Paint"
Cohesion: 0.15
Nodes (14): acceleratedecelerateinterpolator, android.animation.ValueAnimator, android.graphics.drawable.Drawable, android.graphics.Matrix, android.graphics.Paint, android.view.View, lineargradient, path (+6 more)

### Community 4 - "NotchPanel"
Cohesion: 0.08
Nodes (10): android.app.Activity, android.graphics.RectF, android.view.animation.OvershootInterpolator, LayoutParams, Override, NotchPanel, Panel, Context (+2 more)

### Community 6 - "Checks.md"
Cohesion: 0.10
Nodes (19): Before every commit, Checks, Release, Session start, Taseen's own checks (every task, unasked), Testing on the phone, Decisions, Keeping this current (+11 more)

### Community 7 - "Wallpapers.java"
Cohesion: 0.11
Nodes (18): arrays, comparator, consumer, fileoutputstream, httpurlconnection, imagedecoder, inputstream, jsonarray (+10 more)

### Community 8 - "android.content.Context"
Cohesion: 0.19
Nodes (7): android.content.Context, android.content.res.Resources, Wallpapers, Collection, Item, Rect, Wallpapers

### Community 9 - "TapWallpaper.java"
Cohesion: 0.20
Nodes (9): bitmapfactory, canvas, color, systemclock, vibrationattributes, vibrationeffect, vibratormanager, viewconfiguration (+1 more)

### Community 10 - "Override"
Cohesion: 0.20
Nodes (6): ColorFilter, Drawable, Configuration, Override, Rect, SwitchPart

### Community 11 - "log"
Cohesion: 0.22
Nodes (7): android.media.AudioManager, android.service.quicksettings.TileService, log, Override, ScreenOffTile, Override, VolumeTile

### Community 12 - "TapOff"
Cohesion: 0.20
Nodes (9): Bugs, Build from source, Credits, How it works, Install, License, Privacy, TapOff (+1 more)

### Community 13 - "LockService.java"
Cohesion: 0.14
Nodes (14): android.view.WindowManager, arraylist, componentname, contentresolver, dashpatheffect, displaycutout, files, list (+6 more)

### Community 14 - "build.sh"
Cohesion: 0.50
Nodes (3): JAVA_HOME, PATH, build.sh script

### Community 16 - "android.graphics.Bitmap"
Cohesion: 0.18
Nodes (3): android.graphics.Bitmap, GlassCard, Override

### Community 19 - "NotchPanel.java"
Cohesion: 0.20
Nodes (10): display, displaymanager, file, intent, ioexception, linearinterpolator, pixelformat, settings (+2 more)

## Knowledge Gaps
- **30 isolated node(s):** `build.sh script`, `JAVA_HOME`, `PATH`, `PRIMARY`, `SECONDARY` (+25 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 91 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **3 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `MainActivity` connect `MainActivity` to `MainActivity.java`, `TapWallpaper`, `android.graphics.Paint`, `NotchPanel`, `Override`, `android.graphics.Bitmap`?**
  _High betweenness centrality (0.171) - this node is a cross-community bridge._
- **Why does `Architecture` connect `TapWallpaper` to `MainActivity`, `android.content.Context`, `Checks.md`?**
  _High betweenness centrality (0.105) - this node is a cross-community bridge._
- **Why does `NotchPanel` connect `NotchPanel` to `TapWallpaper`, `android.content.Context`, `log`, `LockService.java`, `NotchPanel.java`?**
  _High betweenness centrality (0.069) - this node is a cross-community bridge._
- **What connects `build.sh script`, `JAVA_HOME`, `PATH` to the rest of the system?**
  _30 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `MainActivity` be split into smaller, more focused modules?**
  _Cohesion score 0.10578609000584453 - nodes in this community are weakly interconnected._
- **Should `MainActivity.java` be split into smaller, more focused modules?**
  _Cohesion score 0.06901960784313725 - nodes in this community are weakly interconnected._
- **Should `TapWallpaper` be split into smaller, more focused modules?**
  _Cohesion score 0.07777777777777778 - nodes in this community are weakly interconnected._