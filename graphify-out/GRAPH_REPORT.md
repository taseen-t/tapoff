# Graph Report - tapoff  (2026-09-30)

## Corpus Check
- 24 files · ~30,235 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 41 file(s) not represented in the graph (top: .xml 34, (none) 4, .aidl 3)

## Summary
- 456 nodes · 1183 edges · 21 communities (17 shown, 4 thin omitted)
- Extraction: 97% EXTRACTED · 3% INFERRED · 0% AMBIGUOUS · INFERRED: 33 edges (avg confidence: 0.92)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `fb4cd02a`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- android.content.Context
- MainActivity.java
- TapWallpaper
- GlassCard
- NotchPanel
- android.graphics.Canvas
- Checks.md
- Wallpapers.java
- Wallpapers
- TapWallpaper.java
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
- SlidersActivity

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
- `Wallpapers` --references--> `TapWallpaper`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/TapWallpaper.java

## Import Cycles
- None detected.

## Communities (21 total, 4 thin omitted)

### Community 0 - "android.content.Context"
Cohesion: 0.10
Nodes (17): android.content.Context, android.graphics.drawable.GradientDrawable, android.widget.FrameLayout, android.widget.LinearLayout, android.widget.ScrollView, android.widget.Switch, android.widget.TextView, ArgbEvaluator (+9 more)

### Community 1 - "MainActivity.java"
Cohesion: 0.07
Nodes (28): android.content.Intent, android.graphics.Typeface, android.os.Bundle, android.view.MotionEvent, android.view.VelocityTracker, android.widget.Button, android.widget.ImageButton, android.widget.ImageView (+20 more)

### Community 2 - "TapWallpaper"
Cohesion: 0.09
Nodes (20): android.app.WallpaperColors, android.service.wallpaper.WallpaperService, android.view.accessibility.AccessibilityEvent, android.view.SurfaceHolder, BroadcastReceiver, ContentObserver, Architecture, Brightness and volume sliders (`NotchPanel.java`) (+12 more)

### Community 3 - "GlassCard"
Cohesion: 0.10
Nodes (18): acceleratedecelerateinterpolator, android.animation.ValueAnimator, android.graphics.drawable.Drawable, android.graphics.Matrix, android.graphics.Paint, android.view.View, lineargradient, linearinterpolator (+10 more)

### Community 4 - "NotchPanel"
Cohesion: 0.09
Nodes (7): android.graphics.RectF, android.view.animation.OvershootInterpolator, LayoutParams, Override, NotchPanel, Panel, Override

### Community 6 - "Checks.md"
Cohesion: 0.10
Nodes (19): Before every commit, Checks, Release, Session start, Taseen's own checks (every task, unasked), Testing on the phone, Keeping this current, Notes (+11 more)

### Community 7 - "Wallpapers.java"
Cohesion: 0.11
Nodes (18): arrays, comparator, consumer, fileoutputstream, httpurlconnection, imagedecoder, inputstream, jsonarray (+10 more)

### Community 8 - "Wallpapers"
Cohesion: 0.12
Nodes (7): android.content.res.Resources, android.graphics.Bitmap, Wallpapers, Collection, Item, Rect, Wallpapers

### Community 9 - "TapWallpaper.java"
Cohesion: 0.22
Nodes (8): bitmapfactory, canvas, color, systemclock, vibrationeffect, vibratormanager, viewconfiguration, wallpapermanager

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
Nodes (14): android.accessibilityservice.AccessibilityService, android.view.WindowManager, arraylist, componentname, contentresolver, dashpatheffect, displaycutout, files (+6 more)

### Community 14 - "build.sh"
Cohesion: 0.50
Nodes (3): JAVA_HOME, PATH, build.sh script

### Community 15 - "ShizukuSetup.java"
Cohesion: 0.10
Nodes (20): android.content.ContentProvider, android.content.ContentValues, android.database.Cursor, android.net.Uri, android.os.Handler, android.os.IBinder, android.os.Parcel, android.os.Parcelable (+12 more)

### Community 19 - "NotchPanel.java"
Cohesion: 0.22
Nodes (9): display, displaymanager, file, intent, ioexception, pixelformat, settings, toast (+1 more)

### Community 20 - "SlidersActivity"
Cohesion: 0.40
Nodes (4): android.app.Activity, Decisions, Context, SlidersActivity

## Knowledge Gaps
- **33 isolated node(s):** `build.sh script`, `JAVA_HOME`, `PATH`, `PRIMARY`, `SECONDARY` (+28 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 100 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **4 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `MainActivity` connect `android.content.Context` to `MainActivity.java`, `TapWallpaper`, `GlassCard`, `NotchPanel`, `Wallpapers`, `Override`, `ShizukuSetup.java`, `SlidersActivity`?**
  _High betweenness centrality (0.154) - this node is a cross-community bridge._
- **Why does `Architecture` connect `TapWallpaper` to `android.content.Context`, `Wallpapers`, `Checks.md`, `ShizukuSetup.java`?**
  _High betweenness centrality (0.090) - this node is a cross-community bridge._
- **Why does `NotchPanel` connect `NotchPanel` to `android.content.Context`, `TapWallpaper`, `log`, `LockService.java`, `NotchPanel.java`?**
  _High betweenness centrality (0.061) - this node is a cross-community bridge._
- **What connects `build.sh script`, `JAVA_HOME`, `PATH` to the rest of the system?**
  _33 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `android.content.Context` be split into smaller, more focused modules?**
  _Cohesion score 0.10343061955965181 - nodes in this community are weakly interconnected._
- **Should `MainActivity.java` be split into smaller, more focused modules?**
  _Cohesion score 0.06862745098039216 - nodes in this community are weakly interconnected._
- **Should `TapWallpaper` be split into smaller, more focused modules?**
  _Cohesion score 0.08771929824561403 - nodes in this community are weakly interconnected._