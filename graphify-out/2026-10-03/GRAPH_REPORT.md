# Graph Report - tapoff  (2026-10-02)

## Corpus Check
- 25 files · ~37,328 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 41 file(s) not represented in the graph (top: .xml 34, (none) 4, .aidl 3)

## Summary
- 470 nodes · 1202 edges · 22 communities (17 shown, 5 thin omitted)
- Extraction: 97% EXTRACTED · 3% INFERRED · 0% AMBIGUOUS · INFERRED: 34 edges (avg confidence: 0.92)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `dbd770b4`
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
- TapWallpaper.java
- Panel
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

## Communities (22 total, 5 thin omitted)

### Community 0 - "MainActivity"
Cohesion: 0.10
Nodes (16): android.graphics.Bitmap, android.graphics.drawable.GradientDrawable, android.widget.FrameLayout, android.widget.LinearLayout, android.widget.ScrollView, android.widget.Switch, android.widget.TextView, ArgbEvaluator (+8 more)

### Community 1 - "MainActivity.java"
Cohesion: 0.06
Nodes (29): android.content.Intent, android.graphics.Typeface, android.os.Bundle, android.view.MotionEvent, android.view.VelocityTracker, android.widget.Button, android.widget.ImageButton, android.widget.ImageView (+21 more)

### Community 2 - "TapWallpaper"
Cohesion: 0.07
Nodes (24): android.accessibilityservice.AccessibilityService, android.service.wallpaper.WallpaperService, android.view.accessibility.AccessibilityEvent, BroadcastReceiver, ContentObserver, Architecture, Brightness and volume sliders (`NotchPanel.java`), Icons (+16 more)

### Community 3 - "android.graphics.Paint"
Cohesion: 0.14
Nodes (15): acceleratedecelerateinterpolator, android.animation.ValueAnimator, android.graphics.drawable.Drawable, android.graphics.Matrix, android.graphics.Paint, android.view.View, lineargradient, linearinterpolator (+7 more)

### Community 4 - "android.content.Context"
Cohesion: 0.11
Nodes (8): android.app.Activity, android.content.Context, android.graphics.RectF, LayoutParams, NotchPanel, Context, Override, SlidersActivity

### Community 6 - "Progress log"
Cohesion: 0.06
Nodes (32): Before every commit, Checks, Release, Session start, Taseen's own checks (every task, unasked), Testing on the phone, Keeping this current, Notes (+24 more)

### Community 7 - "Wallpapers.java"
Cohesion: 0.11
Nodes (18): arrays, comparator, consumer, fileoutputstream, httpurlconnection, imagedecoder, inputstream, jsonarray (+10 more)

### Community 8 - "Wallpapers"
Cohesion: 0.21
Nodes (4): android.content.res.Resources, Collection, Item, Wallpapers

### Community 10 - "NotchPanel.java"
Cohesion: 0.15
Nodes (15): android.view.WindowManager, componentname, contentresolver, display, displaymanager, file, files, intent (+7 more)

### Community 11 - "log"
Cohesion: 0.28
Nodes (6): android.media.AudioManager, android.service.quicksettings.TileService, log, ScreenOffTile, Override, VolumeTile

### Community 12 - "TapOff"
Cohesion: 0.15
Nodes (12): Bugs, Build from source, Credits, How it works, Install, License, Privacy, TapOff (+4 more)

### Community 13 - "Override"
Cohesion: 0.22
Nodes (5): ColorFilter, Drawable, Override, Rect, SwitchPart

### Community 14 - "build.sh"
Cohesion: 0.50
Nodes (3): JAVA_HOME, PATH, build.sh script

### Community 15 - "ShizukuSetup.java"
Cohesion: 0.10
Nodes (20): android.content.ContentProvider, android.content.ContentValues, android.database.Cursor, android.net.Uri, android.os.Handler, android.os.IBinder, android.os.Parcel, android.os.Parcelable (+12 more)

### Community 19 - "TapWallpaper.java"
Cohesion: 0.18
Nodes (10): android.app.WallpaperColors, android.view.SurfaceHolder, bitmapfactory, canvas, color, systemclock, vibrationattributes, vibratormanager (+2 more)

### Community 20 - "Panel"
Cohesion: 0.39
Nodes (3): android.view.animation.OvershootInterpolator, Override, Panel

### Community 21 - "CutoutArt.java"
Cohesion: 0.25
Nodes (7): arraylist, dashpatheffect, displaycutout, list, radialgradient, random, sweepgradient

## Knowledge Gaps
- **44 isolated node(s):** `build.sh script`, `JAVA_HOME`, `PATH`, `PRIMARY`, `SECONDARY` (+39 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 111 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **5 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `MainActivity` connect `MainActivity` to `MainActivity.java`, `TapWallpaper`, `android.graphics.Paint`, `android.content.Context`, `GlassCard`, `Override`, `ShizukuSetup.java`?**
  _High betweenness centrality (0.149) - this node is a cross-community bridge._
- **Why does `Decisions` connect `TapWallpaper` to `android.content.Context`, `Progress log`, `ShizukuSetup.java`?**
  _High betweenness centrality (0.082) - this node is a cross-community bridge._
- **Why does `Architecture` connect `TapWallpaper` to `MainActivity`, `Progress log`, `ShizukuSetup.java`?**
  _High betweenness centrality (0.077) - this node is a cross-community bridge._
- **What connects `build.sh script`, `JAVA_HOME`, `PATH` to the rest of the system?**
  _44 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `MainActivity` be split into smaller, more focused modules?**
  _Cohesion score 0.10218579234972677 - nodes in this community are weakly interconnected._
- **Should `MainActivity.java` be split into smaller, more focused modules?**
  _Cohesion score 0.0632996632996633 - nodes in this community are weakly interconnected._
- **Should `TapWallpaper` be split into smaller, more focused modules?**
  _Cohesion score 0.06938775510204082 - nodes in this community are weakly interconnected._