# TapOff

Double-tap to turn your Android phone's screen off, for when the power button is worn out, hard to press
through a case, or just too far away. Plus a wallpaper browser with wallpapers drawn around your camera hole.

**Download:** [TapOff.apk](https://github.com/taseen-t/tapoff/releases/latest/download/TapOff.apk) ·
**Website:** https://tapoff.vercel.app

## What it does

- **Double-tap the home screen** on any empty space and the screen turns off, with a tap you feel.
- **Double-tap the lock screen** too. On Pixels this switches on Android's own lock-screen double-tap.
- **Keeps bank apps working.** Many banking apps refuse to open while any accessibility service is on.
  TapOff's is off all the time and switches on for about a second only while it turns the screen off.
- **Fingerprint unlock still works** afterwards. The screen fades to black instead of flashing the lock screen.
- **Wallpapers:** Pixel's built-in ones, today's Bing photo, hand-checked art from Wallhaven, your own photo,
  and **Cutout**: 12 designs drawn live around your phone's camera hole (a black hole, a galaxy, a sunflower,
  a record, a keyhole...). Whatever you pick goes on the home and lock screen.
- **Quick Settings tiles** for Screen off and Volume.
- Light, dark or follow the system.

## Install

TapOff needs Android 13 or newer. It was built and tested on a Pixel 7 running Android 17.

1. Download [TapOff.apk](https://github.com/taseen-t/tapoff/releases/latest/download/TapOff.apk) on your phone
   and open it. Allow your browser to install apps when Android asks.
2. Open TapOff and tap **Set TapOff as home wallpaper**. Choose **Home screen and lock screen**.
   The home-screen double-tap works through the wallpaper, so this step is required.
3. **One-time setup from a computer.** Turning the screen off without leaving accessibility on needs a
   permission only a computer can grant:
   - On the phone: Settings → About phone → tap **Build number** 7 times, then Settings → System →
     Developer options → turn on **USB debugging**.
   - On the computer: install [Android platform-tools](https://developer.android.com/tools/releases/platform-tools),
     plug the phone in, accept the prompt on the phone, and run:
     ```
     adb shell pm grant com.taseen.tapoff android.permission.WRITE_SECURE_SETTINGS
     ```
   - You can turn USB debugging off again afterwards; the permission stays.

## Build from source

No Gradle. You need JDK 17 and the Android SDK (`platforms;android-35`, `build-tools;35.0.0`).

```
./build.sh            # builds TapOff.apk
./build.sh --install  # also installs it on a connected phone and grants the permission
```

Set `JAVA_HOME` and `ANDROID_HOME` if they aren't the Homebrew defaults. The first build creates a signing key
(`debug.keystore`, never committed). Set `TAPOFF_KEYSTORE` / `TAPOFF_KEYSTORE_PASS` to use your own.

## How it works

| Part | File |
|---|---|
| Live wallpaper that hears home-screen taps (`android.wallpaper.tap`) and draws your wallpaper | `TapWallpaper.java` |
| Accessibility service that fades to black, locks, and switches itself off | `LockService.java` |
| Wallpaper sources, on-phone cache, applying to home and lock screen | `Wallpapers.java` |
| The Cutout designs, drawn around the real camera hole | `CutoutArt.java` |
| Main screen, deck of cards, settings | `MainActivity.java`, `GlassCard.java` |
| Full-screen preview with swipe | `PreviewActivity.java`, `SwipeHint.java` |
| Quick Settings tiles | `ScreenOffTile.java`, `VolumeTile.java` |

## Privacy

TapOff has no accounts, no analytics and no ads. It only goes online to fetch wallpapers from Bing and
Wallhaven, and downloaded wallpapers are cached on the phone. The accessibility service reads nothing on
screen; it only turns the screen off.

## Credits

- Photos of the day from Bing; art from [Wallhaven](https://wallhaven.cc). Each belongs to its owner.
- Pixel wallpapers are read from the Pixel wallpaper app already on your phone; none are included here.
- The swipe hint's hand is Material Icons "touch_app" (Apache 2.0).

## Bugs

Message [@taseen_tariq_](https://x.com/taseen_tariq_) on X. If you can't send a DM, reply to any post.

## License

[MIT](LICENSE)
