---
tags: [tapoff, home]
---
# TapOff

Double-tap to turn an Android screen off without leaving accessibility on, slide the screen edges for volume and
brightness, and wallpapers drawn around the camera hole. Open source (MIT), sideloaded APK, site on Vercel.

- **APK:** https://github.com/taseen-t/tapoff/releases/latest/download/TapOff.apk
- **Site:** https://tapoff.vercel.app (source in `site/index.html`)
- **Repo:** https://github.com/taseen-t/tapoff

## Notes
- [[Architecture]]: how the pieces fit, file by file.
- [[Decisions]]: every non-obvious choice and why.
- [[Tasks]]: every request, with subtasks ticked as they land.
- [[Progress log]]: one entry per session, newest first.
- [[Checks]]: what to run before and after work.
- `Code graph/`: the Graphify export of the whole codebase (regenerate, don't edit).

## Keeping this current
Update these notes in the same commit as the work. After code changes:

```
graphify update . && graphify export obsidian --dir "docs/Code graph"
```
