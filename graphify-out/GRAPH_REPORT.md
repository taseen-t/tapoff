# Graph Report - tapoff  (2026-10-03)

## Corpus Check
- 26 files · ~40,393 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 41 file(s) not represented in the graph (top: .xml 34, (none) 4, .aidl 3)

## Summary
- 484 nodes · 1219 edges · 26 communities (21 shown, 5 thin omitted)
- Extraction: 97% EXTRACTED · 3% INFERRED · 0% AMBIGUOUS · INFERRED: 34 edges (avg confidence: 0.92)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `6c26d724`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- MainActivity
- MainActivity.java
- TapWallpaper
- GlassCard
- NotchPanel
- android.graphics.Canvas
- Progress log
- Wallpapers.java
- android.content.Context
- TapWallpaper.java
- log
- TapOff
- Override
- build.sh
- ShizukuSetup.java
- uri
- imagebutton
- overshootinterpolator
- Reddit
- NotchPanel.java
- LockService.java
- Tasks
- Setup guide (portrait, ~99 s)
- Checks
- TapOff

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
- `Wallpapers` --references--> `TapWallpaper`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/TapWallpaper.java
- `Processes` --references--> `LockService`  [INFERRED]
  docs/Architecture.md → src/com/taseen/tapoff/LockService.java

## Import Cycles
- None detected.

## Communities (26 total, 5 thin omitted)

### Community 0 - "MainActivity"
Cohesion: 0.10
Nodes (15): android.graphics.Bitmap, android.graphics.drawable.GradientDrawable, android.widget.LinearLayout, android.widget.ScrollView, android.widget.Switch, android.widget.TextView, ArgbEvaluator, UI (+7 more)

### Community 1 - "MainActivity.java"
Cohesion: 0.07
Nodes (31): android.app.Activity, android.content.Intent, android.graphics.Typeface, android.view.MotionEvent, android.view.VelocityTracker, android.view.View, android.widget.Button, android.widget.FrameLayout (+23 more)

### Community 2 - "TapWallpaper"
Cohesion: 0.07
Nodes (24): android.accessibilityservice.AccessibilityService, android.app.WallpaperColors, android.service.wallpaper.WallpaperService, android.view.accessibility.AccessibilityEvent, android.view.SurfaceHolder, BroadcastReceiver, ContentObserver, Architecture (+16 more)

### Community 3 - "GlassCard"
Cohesion: 0.08
Nodes (17): acceleratedecelerateinterpolator, android.animation.ValueAnimator, android.graphics.drawable.Drawable, android.graphics.Matrix, android.graphics.Paint, lineargradient, linearinterpolator, path (+9 more)

### Community 4 - "NotchPanel"
Cohesion: 0.08
Nodes (8): android.graphics.RectF, android.view.animation.OvershootInterpolator, Setup without a computer (`ShizukuSetup.java`), LayoutParams, Override, NotchPanel, Panel, Override

### Community 6 - "Progress log"
Cohesion: 0.15
Nodes (13): 2026-09-30 · 1.7: slide the edges, 2026-09-30 (evening) · 1.9: setup without a computer, 2026-09-30 (late) · Play Protect block: a no-computer way in, 2026-09-30 (later) · 1.8: back-tap sliders, favourites, NASA, 2026-09-30 (night) · Shizuku PR, brag video, 2026-10-01 · Setup guide video, 2026-10-02 (evening) · Video controls, video shadow, telling people, 2026-10-02 · Setup video on the website (+5 more)

### Community 7 - "Wallpapers.java"
Cohesion: 0.11
Nodes (18): arrays, comparator, consumer, fileoutputstream, httpurlconnection, imagedecoder, inputstream, jsonarray (+10 more)

### Community 8 - "android.content.Context"
Cohesion: 0.18
Nodes (7): android.content.Context, android.content.res.Resources, Wallpapers, Collection, Item, Rect, Wallpapers

### Community 10 - "TapWallpaper.java"
Cohesion: 0.22
Nodes (8): bitmapfactory, canvas, color, systemclock, vibrationeffect, vibratormanager, viewconfiguration, wallpapermanager

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
Cohesion: 0.11
Nodes (20): android.content.ContentProvider, android.content.ContentValues, android.database.Cursor, android.net.Uri, android.os.Bundle, android.os.Handler, android.os.IBinder, android.os.Parcel (+12 more)

### Community 19 - "Reddit"
Cohesion: 0.22
Nodes (9): Facts each post must get right, Instagram (Reels), Method, Reddit, Research - reach, The demand is real, and the competition is known, What gets upvotes, What to post where (+1 more)

### Community 20 - "NotchPanel.java"
Cohesion: 0.22
Nodes (9): android.view.WindowManager, display, displaymanager, file, intent, ioexception, pixelformat, toast (+1 more)

### Community 21 - "LockService.java"
Cohesion: 0.15
Nodes (13): arraylist, componentname, contentresolver, dashpatheffect, displaycutout, files, list, packagemanager (+5 more)

### Community 24 - "Tasks"
Cohesion: 0.29
Nodes (7): 2026-09-30, 2026-09-30 (later) · 1.8, 2026-10-02 · Telling people, 2026-10-03, Done before this vault existed (v1.0–1.6), Open, Tasks

### Community 25 - "Setup guide (portrait, ~99 s)"
Cohesion: 0.29
Nodes (7): Brag video (portrait + landscape, 23 s), On the site, Privacy pipeline (from the UplinkeSIM videos' method), Rebuild, Setup guide (portrait, ~99 s), Shared style, Videos

### Community 26 - "Checks"
Cohesion: 0.33
Nodes (6): Before every commit, Checks, Release, Session start, Taseen's own checks (every task, unasked), Testing on the phone

### Community 27 - "TapOff"
Cohesion: 0.67
Nodes (3): Keeping this current, Notes, TapOff

## Knowledge Gaps
- **55 isolated node(s):** `build.sh script`, `JAVA_HOME`, `PATH`, `PRIMARY`, `SECONDARY` (+50 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 122 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **5 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `MainActivity` connect `MainActivity` to `MainActivity.java`, `TapWallpaper`, `GlassCard`, `NotchPanel`, `android.content.Context`, `Override`, `ShizukuSetup.java`?**
  _High betweenness centrality (0.147) - this node is a cross-community bridge._
- **Why does `Decisions` connect `TapWallpaper` to `ShizukuSetup.java`, `Decisions.md`?**
  _High betweenness centrality (0.110) - this node is a cross-community bridge._
- **Why does `Architecture` connect `TapWallpaper` to `MainActivity`, `android.content.Context`, `NotchPanel`, `Decisions.md`?**
  _High betweenness centrality (0.089) - this node is a cross-community bridge._
- **What connects `build.sh script`, `JAVA_HOME`, `PATH` to the rest of the system?**
  _55 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `MainActivity` be split into smaller, more focused modules?**
  _Cohesion score 0.09779825908858167 - nodes in this community are weakly interconnected._
- **Should `MainActivity.java` be split into smaller, more focused modules?**
  _Cohesion score 0.06636500754147813 - nodes in this community are weakly interconnected._
- **Should `TapWallpaper` be split into smaller, more focused modules?**
  _Cohesion score 0.07215541165587419 - nodes in this community are weakly interconnected._