# Graph Report - tapoff  (2026-10-02)

## Corpus Check
- 25 files · ~35,682 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 41 file(s) not represented in the graph (top: .xml 34, (none) 4, .aidl 3)

## Summary
- 468 nodes · 1199 edges · 17 communities (12 shown, 5 thin omitted)
- Extraction: 97% EXTRACTED · 3% INFERRED · 0% AMBIGUOUS · INFERRED: 34 edges (avg confidence: 0.92)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `f1cd8912`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- MainActivity
- MainActivity.java
- TapWallpaper.java
- NotchPanel.java
- android.content.Context
- android.graphics.Canvas
- Progress log
- Wallpapers.java
- PreviewActivity
- GlassCard
- log
- TapOff
- build.sh
- ShizukuSetup.java
- uri
- imagebutton
- overshootinterpolator

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
- `Decisions` --references--> `LockService`  [INFERRED]
  docs/Decisions.md → src/com/taseen/tapoff/LockService.java
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

## Communities (17 total, 5 thin omitted)

### Community 0 - "MainActivity"
Cohesion: 0.10
Nodes (16): android.graphics.drawable.GradientDrawable, android.widget.FrameLayout, android.widget.ImageView, android.widget.LinearLayout, android.widget.ScrollView, android.widget.Switch, android.widget.TextView, ArgbEvaluator (+8 more)

### Community 1 - "MainActivity.java"
Cohesion: 0.06
Nodes (31): android.content.Intent, android.graphics.Typeface, android.os.Bundle, android.view.MotionEvent, android.view.VelocityTracker, android.widget.Button, android.widget.ImageButton, android.widget.ProgressBar (+23 more)

### Community 2 - "TapWallpaper.java"
Cohesion: 0.06
Nodes (29): android.accessibilityservice.AccessibilityService, android.app.WallpaperColors, android.service.wallpaper.WallpaperService, android.view.accessibility.AccessibilityEvent, android.view.SurfaceHolder, bitmapfactory, BroadcastReceiver, canvas (+21 more)

### Community 3 - "NotchPanel.java"
Cohesion: 0.07
Nodes (37): acceleratedecelerateinterpolator, android.animation.ValueAnimator, android.graphics.drawable.Drawable, android.graphics.Matrix, android.graphics.Paint, android.view.animation.OvershootInterpolator, android.view.View, android.view.WindowManager (+29 more)

### Community 4 - "android.content.Context"
Cohesion: 0.14
Nodes (5): android.content.Context, android.graphics.RectF, LayoutParams, NotchPanel, Override

### Community 6 - "Progress log"
Cohesion: 0.07
Nodes (30): Before every commit, Checks, Release, Session start, Taseen's own checks (every task, unasked), Testing on the phone, Keeping this current, Notes (+22 more)

### Community 7 - "Wallpapers.java"
Cohesion: 0.07
Nodes (26): android.app.Activity, arrays, comparator, consumer, Decisions, file, fileoutputstream, httpurlconnection (+18 more)

### Community 8 - "PreviewActivity"
Cohesion: 0.10
Nodes (9): android.content.res.Resources, android.graphics.Bitmap, Wallpapers, Override, PreviewActivity, Collection, Item, Rect (+1 more)

### Community 11 - "log"
Cohesion: 0.22
Nodes (7): android.media.AudioManager, android.service.quicksettings.TileService, log, Override, ScreenOffTile, Override, VolumeTile

### Community 12 - "TapOff"
Cohesion: 0.15
Nodes (12): Bugs, Build from source, Credits, How it works, Install, License, Privacy, TapOff (+4 more)

### Community 14 - "build.sh"
Cohesion: 0.50
Nodes (3): JAVA_HOME, PATH, build.sh script

### Community 15 - "ShizukuSetup.java"
Cohesion: 0.10
Nodes (20): android.content.ContentProvider, android.content.ContentValues, android.database.Cursor, android.net.Uri, android.os.Handler, android.os.IBinder, android.os.Parcel, android.os.Parcelable (+12 more)

## Knowledge Gaps
- **42 isolated node(s):** `build.sh script`, `JAVA_HOME`, `PATH`, `PRIMARY`, `SECONDARY` (+37 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 109 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **5 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `MainActivity` connect `MainActivity` to `MainActivity.java`, `TapWallpaper.java`, `NotchPanel.java`, `android.content.Context`, `Wallpapers.java`, `PreviewActivity`, `GlassCard`, `ShizukuSetup.java`?**
  _High betweenness centrality (0.151) - this node is a cross-community bridge._
- **Why does `Architecture` connect `TapWallpaper.java` to `MainActivity`, `PreviewActivity`, `Progress log`, `ShizukuSetup.java`?**
  _High betweenness centrality (0.082) - this node is a cross-community bridge._
- **Why does `Decisions` connect `Wallpapers.java` to `TapWallpaper.java`, `Progress log`, `ShizukuSetup.java`?**
  _High betweenness centrality (0.071) - this node is a cross-community bridge._
- **What connects `build.sh script`, `JAVA_HOME`, `PATH` to the rest of the system?**
  _42 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `MainActivity` be split into smaller, more focused modules?**
  _Cohesion score 0.09994711792702274 - nodes in this community are weakly interconnected._
- **Should `MainActivity.java` be split into smaller, more focused modules?**
  _Cohesion score 0.05803921568627451 - nodes in this community are weakly interconnected._
- **Should `TapWallpaper.java` be split into smaller, more focused modules?**
  _Cohesion score 0.06485671191553545 - nodes in this community are weakly interconnected._