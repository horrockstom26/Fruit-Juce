<div align="center">

<br/>

<img src="LOGO.png" alt="Kisetsu app icon" width="250" />

# Kisetsu

### A music player that follows the season.

<br/>

[![Latest release](https://img.shields.io/github/v/release/horrockstom26/Fruit-Juce?style=for-the-badge&labelColor=0d1117)](https://github.com/horrockstom26/Fruit-Juce/releases)
[![License](https://img.shields.io/github/license/horrockstom26/Fruit-Juce?style=for-the-badge&labelColor=0d1117)](https://github.com/horrockstom26/Fruit-Juce/blob/main/LICENSE)
[![Downloads](https://img.shields.io/github/downloads/horrockstom26/Fruit-Juce/total?style=for-the-badge&labelColor=0d1117)](https://github.com/horrockstom26/Fruit-Juce/releases)

<br/>
<img src="BANNER.png" alt="Kisetsu banner" width="500" />

[**Features**](#features) · [**Sources & addons**](#sources--addons) · [**Download**](#download) · [**Build from source**](#build-from-source) · [**Support**](#support) · [**License**](#license)

</div>

> [!NOTE]
> Kisetsu is not affiliated with, endorsed by, or connected to YouTube, Google, Deezer, Qobuz, Tidal, or any other service mentioned here.

---

## What Kisetsu is

A free Android music player built around **lossless-first playback**. It plays your local library, YouTube Music, and optional lossless addons, with the library, downloads, lyrics, scrobbling and Discord presence you expect from a modern player.

Android only. No ads, no telemetry, no account required, no "Pro" tier.

## Features

### Interface
- **Apple Music–style layout** — a rounded cover card, credits and two discs beside them, the seek bar with its two times, three large transport buttons, and the action row beneath.
- **Docked navigation** — a white bar with five icon-and-label tabs and a hairline against the page, with the mini player sitting flat above it. A floating glass bar is still there if you prefer it, in Settings → Customisation.
- **Cover-lit colours** — the accent follows the playing artwork by default. Dynamic colours are on out of the box; the full-screen cover wash is off.
- **Your own palette** — pin colours for buttons, seek bar, text and lyrics, or leave them on Automatic.
- **Light and dark themes**, reduced motion, and Home/Library layout toggles.
- **Replay** — your listening year as cards and stories, sign-off included.

### Listening
- **Flow** — an endless personal mix that unlocks at 16 favourites, trains on skips and bans, refreshes hourly, survives restarts, and never autoplays until you press play.
- **Classipod** — a full iPod Classic theme with click-wheel navigation and Cover Flow.
- **Automix V2** — DJ-style transitions with beat locking, entrance rescoring and vocal gating.
- **Sing** — karaoke mode that turns the lead vocal down so you can sing over the track.
- **Honest lossless numbers** — stats and Discord presence report the decoded PCM rate (1411 / 4608 kbps), never a container guess.
- **AI badge with strict mode** — flags AI-generated uploads and can skip them.

### Library & downloads
- **Downloads play at your chosen quality when online**, with the downloaded file kept as the offline fallback.
- **Instant downloads screen** — snapshot first paint, batched verification, single-song cache updates.
- **Save cover art** from any song menu.
- **Replay** appears in Library through a Customisation toggle instead of taking over Home.

## Sources & addons

Kisetsu ranks sources itself: your addons first, then JioSaavn if you opt in, then YouTube Music as the fallback. Each addon has a health check and reports stream refusals on its own row instead of failing silently.

Lossless arrives through addons. They run on community instances and free tiers, so the app never hardcodes a single endpoint: each addon has automatic failover, a refreshed instance list and a row that tells you when it needs attention.

**Anything you add yourself stays yours**: it is listed under its own name or host, and it can always be deleted — including links that happen to speak one of the built-in protocols.

> **Why the addons aren't named after their backends:** they run on volunteers' servers and free tiers. Naming them draws traffic, and traffic gets things rate-limited or shut down. The endpoints, fallbacks and setup steps are all in the source for anyone who wants to look.

## Download

Grab the latest APK from the [Releases](https://github.com/horrockstom26/Fruit-Juce/releases) page. Current build: **7.6** (`versionCode 13`, package `com.horrockstom26.fruitjuce`).

| APK | Use it for |
|---|---|
| `app-prod-arm64-v8a-debug.apk` | Almost every modern phone (recommended). |
| `app-prod-universal-debug.apk` | If you aren't sure which one you need. |

- Sideloading requires enabling "Install unknown apps" for whichever app you download it with.
- Every release is signed with the same key, so updates install over the old build. Your library, downloads and settings carry over.
- Requires Android 8.0 (API 26) or newer.

## Build from source

This repository is developed for Android. JDK 21 and the Android SDK (compileSdk 37) are required.

```bash
./gradlew :app:assembleProdDebug            # debug build
./gradlew :app:assembleProdRelease          # minified, R8-optimised release build
```

The debug APKs land in `app/build/outputs/apk/prod/debug/`. A signed, installable release APK is produced by the **Flow Debug APK** workflow (Actions → Run workflow), which signs with the project's stable key so it updates over previous installs. Last.fm credentials are optional and read from `local.properties`; scrobbling stays unavailable until they are provided.

## Support

Kisetsu is free and open source, and it is staying that way. A star, a bug report or a pull request is appreciated as much as a donation.

- [Issues](https://github.com/horrockstom26/Fruit-Juce/issues)

| Asset | Address |
|---|---|
| **Nano (XNO)** — instant, feeless, no minimum | `nano_1zqdw3qf1z8k3jx8jintaiwpo3yz7zqh1me4ph5j439ts8hsppx8dzy4xcsz` |
| **USDC on Base** (EVM) | `0x3f262ee685ced4a8270cece45ebdfdb2b18f54b5` |
| **Zcash (ZEC)** — shielded unified address | `u1z9k30yyvy63f5w0jypt02kvvw6dcpcgprlhzmsgc57mw6qc8rtuc5tfd9ny4atqhr448udexhkuc8xgl0z5rd9njuxnwl7kh2ahqwcqlydt9dpr4t40eawr5st74as5jed669993epsnwuejnrwv4yrkx065pqvmt0cr8gdwggcv2djp` |

<details>
<summary><b>Copy-paste block</b></summary>

```
XNO (Nano):
nano_1zqdw3qf1z8k3jx8jintaiwpo3yz7zqh1me4ph5j439ts8hsppx8dzy4xcsz

USDC (Base / EVM):
0x3f262ee685ced4a8270cece45ebdfdb2b18f54b5

ZEC (shielded unified address):
u1z9k30yyvy63f5w0jypt02kvvw6dcpcgprlhzmsgc57mw6qc8rtuc5tfd9ny4atqhr448udexhkuc8xgl0z5rd9njuxnwl7kh2ahqwcqlydt9dpr4t40eawr5st74as5jed669993epsnwuejnrwv4yrkx065pqvmt0cr8gdwggcv2djp
```

</details>

## License

Kisetsu is free software under the **GNU General Public License v3.0**. You may use, study, share and modify it, including commercially, but any distribution must keep the licence and ship the corresponding source under the same terms. See [LICENSE](LICENSE).

Third-party components, their copyright holders and their licences are listed in [NOTICE](NOTICE).

## Disclaimer

Kisetsu is an independent player. It is **not** associated with Google LLC, YouTube Music, Deezer, Qobuz, Tidal, or any of their parent companies.

- **No media hosting.** Kisetsu does not host, upload or store copyrighted music. It is an interface to local storage and to user-authenticated or public APIs.
- **Fair use and API usage.** The software is provided for personal research, educational and fair-use purposes. You are responsible for complying with your local copyright law and the terms of any service you connect.
- **No warranty.** Provided as is, without warranty of any kind. Use it at your own discretion.
