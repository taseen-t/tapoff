---
tags: [tapoff, checks]
---
# Checks

## Session start
1. Read the top of [[Progress log]] and [[Tasks]].
2. `./build.sh` must build cleanly.

## Testing on the phone
1. `./build.sh --install`, then **wait ~12 s** before touching the app. Never `am start -S` or force-stop right after
   an install: Android resets the live wallpaper (see [[Decisions]] #5 and #6).
2. Confirm TapOff is still the wallpaper: `adb shell dumpsys wallpaper | grep mWallpaperComponent`.
3. Edge strips present: `adb shell dumpsys window windows | grep "TapOff edge"`.
4. Volume: `adb shell input swipe 20 1600 20 1000 800` and compare `streamVolume` in `adb shell dumpsys audio`.
5. Brightness: slide at x=1068 and read `adb shell settings get system screen_brightness`; put it back afterwards.
6. Screenshot before every tap. Taseen may be using the phone at the same time.

## Before every commit
1. Builds, installed, and the changed feature checked on the phone.
2. [[Tasks]] updated; any decision added to [[Decisions]]; [[Architecture]] updated if structure changed.
3. Graphify refreshed: `graphify update . && graphify export obsidian --dir "docs/Code graph"`.

## Taseen's own checks (every task, unasked)
1. **Security pass.** No secrets in the repo or docs (keystore and `.vercel` stay ignored). New permissions or
   exported components need a reason in [[Decisions]]. Say plainly what wasn't checked.
2. **Sweep dormant and duplicate code.** Delete what a change orphaned; one helper per job.
3. **Libraries only where the problem is hard.** Icons come from Material Symbols and Lucide, not hand-drawn.
4. **Never spend.** No store fee, domain or paid plan without a yes.
5. **Document every step**, not at the end.

## Release
1. Bump `--version-code` / `--version-name` in `build.sh`.
2. Commit, push, `gh release create vX.Y TapOff.apk`.
3. Site: `cd site && npx vercel@latest deploy --prod --yes`, then check the live page.
