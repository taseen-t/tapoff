---
tags: [tapoff, research, launch]
---
# Research - reach

Where telling people about TapOff can land, what gets reach there, and the video styles worth copying. Researched
2026-10-03. Posts are written from the problem side, by the developer, never as a pitch or as a "user who found it".
Drafts live outside the repo (they're unposted); this note keeps the findings. See [[Decisions]] #48–49.

## Reddit

### What to post where
Ranked by reach × whether a developer's post survives the rules. Rules are from a January 2025 archive snapshot:
**check each sidebar before posting.** Members are live counts (2026-10-03).

| Community | Members | Own-app posts | Angle |
|---|---|---|---|
| r/androidapps | 578K | Allowed; dev posts are common and do well (304, 158, 89 upvotes for "I built…" posts this year) | The broken-button story, features, install |
| r/SideProject | 859K | Made for it | The builder's story; Android utilities reach 300–900 |
| r/pixel_phones | 183K | No rule found in the archive; check | Pixel-specific: Pixel's own double-tap works on the lock screen only |
| r/Android | 3.2M | Allowed only as a detailed self-post, from an account at least 3 months old **with posting history on r/Android**, and the dev must reply in the thread | Same story, `[DEV]` in the title. Small utilities do 200–800 here ("Volume Scroll" 230, "Media Bar" 201, "Dsk Mode" 773) |
| r/opensource | 386K | Allowed with the `Promotional` flair, keep self-promotion under ~10% of posts | MIT, readable source; why Play Protect blocks an open-source accessibility app |
| r/fossdroid | 106K | Must be FOSS, licence named, **source link first** | Source first, MIT, not on F-Droid, Obtainium can track the GitHub releases |
| r/androiddev | 295K | Only with source code shared; no posts about getting around Android security | Technical: no-Gradle build, an accessibility service that lives one second, Shizuku without its library, wallpaper taps |
| r/Pixel6 | 38K | Allowed with the `Self-Promotion` flair | Short version; a Pixel 6 owner's power button fell off in a thread asking for exactly this |
| r/androidthemes | 190K | `[Promotion]` posts on Fridays only, with a real description | Cutout wallpapers. Low reach: promo posts score 4–35 |
| r/AndroidHomescreen | 27K | One promo post a week, must be real content | Cutout wallpapers |

**Don't post:**
- r/GooglePixel (1.1M) bans all self-promotion, apps included.
- r/android_beta doesn't allow ads.
- Samsung, OnePlus and other non-Pixel phone communities: those launchers already double-tap to sleep.

### What gets upvotes
- **Story first.** Every top developer post opens with a concrete annoyance, then "so I built…":
  - "I got tired of every app having a subscription… so I built my own" (304)
  - "I got so irritated by banking/UPI apps breaking when developer settings are on! So built something" (187 in r/androiddev, top comment "Developers identifying problems and then carving out a solution. Respect.")
  - "I made an app that puts the AYN Thor back to sleep if it wakes while the lid is closed" (728 in a device community)

  TapOff's own line is this shape already: the new case broke the power button, so I built the fix.
- **Device-specific fixes do best in device communities.** Examples: AYN Thor (728) and the Galaxy Z Flip cover-screen launcher (820). For TapOff that means Pixel communities.
- **What commenters punish:** AI-looking store images ("feel really scamy"), surprise paywalls, and posting then leaving.
- **What they ask:** "Will it be on F-Droid?" and "Add to Obtainium".
- **Topical now:** sideloading limits and Play Protect. A Shizuku + InstallerX guide got 1,852; r/fossdroid's top posts this year (841–1,208) are about developer verification and side-loading.

### The demand is real, and the competition is known
- "What's Google waiting for?" (r/pixel_phones, 171 upvotes, 221 comments) asks why Pixels lack double-tap to sleep.
  - Replies include "The power button in my pixel 6 just fell off… Can anyone recommend a third party app".
  - Also: "I feel like the only people saying it's not important is people who have never used it".
  - The objection to expect: "I've literally never wanted this" (209). So pitch the broken or stiff button, not "everyone needs this".
- "Google will finally let you double tap your Pixel's screen to turn it off" (r/GooglePixel, 821) turned out to be the
  **lock screen only**; a top reply: "this news is regarding double tap to sleep from Pixel's lock screen, and not the
  home screen." TapOff's home-screen double-tap is still the gap.
- People already recommend Nova Launcher (home screen only, needs Nova) and Play Store screen-off apps. Posts should
  describe TapOff's own design (accessibility off except for about a second while locking, so bank apps keep
  working; fingerprint still unlocks) and never describe how a competitor works.

### Facts each post must get right
- Pixel's own double-tap to sleep is lock screen only; TapOff adds the home screen (taps come through TapOff's live
  wallpaper, so it must be the home wallpaper; checked on Pixel Launcher).
- It goes online only for wallpapers and to check GitHub for updates. Never write "no internet".
- The accessibility service declares no event types and `canRetrieveWindowContent="false"`; it's switched on for about
  a second while locking, then turns itself off. Device-admin `lockNow()` was ruled out because it forces the PIN.
- Tested on a Pixel 7 running Android 17 only; Android 13+ required.
- Play Protect blocks browser installs of any app with an accessibility service; the post should give both ways past it
  (pause scanning, or Shizuku + InstallerX), plus the computer route.
- Not on F-Droid. Obtainium can track it (GitHub releases with a `TapOff.apk` asset).

## Instagram (Reels)
Logged-out `/popular/<tag>` pages, 12 Reels per tag; the counts there are labelled views. Small sample, biased to hits,
no likes, so no engagement rate.

| Tag | Median views | Top |
|---|---|---|
| #techtips | 3.95M | 23.5M ("wait for the end… use 2 devices in 1 phone") |
| #phonehacks | 399K | 3.3M ("green lines on your screen? try this") |
| #androidhacks | 31K | 1.7M ("phone speaks when you plug in the charger") |
| #androidtips | 4.4K | 593K (sponsored) |
| #pixeltips | 564 | 206K ("the best Pixel update") |
| #powerbutton | 238 | 136K (charging setting); the power-button videos are repairs, 135–17.8K |

**What wins:**
- A phone problem in the first line, a "hidden / secret / genius" trick or app that fixes it, and a quick on-screen demo. Examples:
  - water-ejector apps (141K, 245K)
  - "this hidden feature limits your volume to 85 dB" (185K)
  - "Power Button Problem Solve" (3.9K, small account)
- **Broad tags carry reach; niche tags barely move.** Post under #techtips / #phonehacks / #androidhacks with one or two Pixel tags.
- **The people with broken power buttons on Instagram are watching repair videos.** "No repair needed" speaks to them.

## YouTube: Infinite Desk
@InfiniteDesk, 395K subscribers, "We explore better ways of thinking."

**Shorts carry the channel:**
- 49 Shorts, median 613K views, 159M in total.
- Top Shorts:
  - "The Genius Way to Weld Steel With Electricity" (26M, 79 s, 528K likes)
  - "The Amazing Evolution of the Faucet" (16M)
  - "The Bolt and Nut That Never Loosen, No Matter the Vibration" (15M, 70 s)
  - "Why does the IV needle get pulled out?" (8.8M, 42 s)
- The 7 long videos have a median of 7.6K views.
- Its phone Shorts are its weakest ("Phone Battery Draining When You're Doing NOTHING?" 49K) except "Same Charging Speed, 20× the Price?" (2.1M), which is a surprising comparison.

**The format:**
- 40–80 s Shorts with a calm voiceover and clean 3D animation.
- Titles are "The Genius / Clever / Ingenious Way to…", "Why does X look like this?", or "The Evolution of…".
- The script goes: the problem everyone has → why it happens → attempt 1 fails → attempt 2 fails → attempt 3 fails → the clever fix and exactly how it works.
- Likes run about 2% of views.

**TapOff fits it well.** The phone needs a button to turn off. The button wears out. A launcher gesture only works in that launcher. An always-on accessibility service upsets bank apps. A device-admin lock forces the PIN. The fix: the taps come through the live wallpaper, and accessibility is on for one second only. Shorts only; the outlines are with the drafts.

## Method
- **Reddit:** Agent Reach's OpenCLI bridge, read-only search and read through Taseen's Chrome session, spaced out.
  - The public Arctic Shift archive gave subreddit lists and rule snapshots. Its scores are captured seconds after posting, so it can't rank posts.
  - The in-app browser can't open Reddit.
- **Instagram:** the in-app browser, logged out, one tag page every 35–40 s.
- **YouTube:** yt-dlp (channel lists, metadata, auto-captions).
