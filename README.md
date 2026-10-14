# **Pocket Lock** fork

> **This is a personal-use fork of [iskrin/pocketlock](https://github.com/iskrin/pocketlock).** It adds a few features and fixes that the maintainer wanted for their own handheld, on top of the original lock screen. See [Disclaimer](#disclaimer) and [Credits](#credits) before using it.

Pocket Lock is a Nintendo Switch–style lock screen for Android handhelds: when the display wakes up, it shows three dots, and you unlock by pressing any button (or tapping the screen) three times.

[Releases](https://github.com/riap0526/pocketlock/releases) | [Add to Obtainium](https://apps.obtainium.imranr.dev/redirect.html?r=obtainium%3A%2F%2Fapp%2F%7B%22id%22%3A%22pl.iskri.pocketlock%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2Friap0526%2Fpocketlock%22%2C%22author%22%3A%22riap0526%22%2C%22name%22%3A%22Pocket%20Lock%22%2C%22apkUrls%22%3Anull%2C%22otherAssetUrls%22%3Anull%2C%22preferredApkIndex%22%3A0%2C%22additionalSettings%22%3A%22%7B%5C%22includePrereleases%5C%22%3Afalse%2C%5C%22fallbackToOlderReleases%5C%22%3Atrue%2C%5C%22verifyLatestTag%5C%22%3Afalse%2C%5C%22sortMethodChoice%5C%22%3A%5C%22date%5C%22%2C%5C%22trackOnly%5C%22%3Afalse%2C%5C%22versionDetection%5C%22%3Atrue%2C%5C%22autoApkFilterByArch%5C%22%3Atrue%7D%22%2C%22overrideSource%22%3A%22GitHub%22%2C%22allowIdChange%22%3Afalse%7D)

---

## What's Different in This Fork

Based on upstream `v1.4`. Full details in [CHANGELOG.md](./CHANGELOG.md). Release APKs are named `PocketLock-<version>.apk` (e.g. `PocketLock-1.4-fork.15.apk`).

**1.4-fork**

- **Ignore buttons after unlock** (Off / 250 / 500 / 1000 ms, default 500 ms): an extra press right after unlocking no longer reaches the app below (e.g. a launcher starting a game on the same A press).
- Merged upstream 1.4 **without its top bar** (clock + battery); this fork keeps its own battery indicator.

**1.3-fork**

- **Battery indicator on the lock screen** (optional): the icon's fill shows the remaining charge, so it reads even with the percentage hidden; lightning bolt and a separate fill color while charging; free positioning, size and colors.
- **Apps never stopped**: an editable list of apps that are only paused, never stopped, while locked (all RetroArch variants by default, instead of one hard-coded package).
- **More reliable detection** of emulators that keep running behind the lock screen on Android 11+.
- **Anti-lockout fix**: analog triggers only count as an unlock method once one has actually been pressed, so devices with digital L2/R2 cannot be locked out.
- **Lighter background image**: decoded only as large as the screen needs and prepared in advance.
- **Signed builds via GitHub Actions**; every build installs over the previous one as an update.

> **Switching from the upstream APK:** this fork is signed with a different key and uses the same package name, so Android requires uninstalling the upstream app first, which **deletes all Pocket Lock settings** (including the background image). If the device administrator permission is enabled, turn it off in Android settings before uninstalling.

---

## Features & Usage

All other features, setup steps and behavior come from the original project. For the full feature list, setup instructions (permissions, system screen lock, device administrator, appearance) and how Pocket Lock works, see the original repository: **[iskrin/pocketlock](https://github.com/iskrin/pocketlock)**.

To build it yourself: JDK 17, Android SDK 35 and `./gradlew assembleRelease`. Release signing in CI uses an encrypted key in `signing/` and the `KEYSTORE_PASSPHRASE` secret; releases are published by the *Release* workflow (Actions → Release → Run workflow with a build number; see [`.github/workflows`](./.github/workflows)). Locally, create a `keystore.properties` as described in the upstream README.

---

## Contributing

Bug reports are welcome via [Issues](https://github.com/riap0526/pocketlock/issues). This fork is maintained mainly for personal use, so feature requests may not be picked up.

---

## Credits

- **[iskrin](https://github.com/iskrin)** — original author of Pocket Lock. This fork is based entirely on the code of [iskrin/pocketlock](https://github.com/iskrin/pocketlock); all original design and features are theirs, and upstream updates are merged into this fork.

---

## Disclaimer

- This fork is maintained **primarily for personal use**: it adds small features and fixes for the maintainer's own device and does not aim to redesign the app.
- Changes in this fork are made **with AI assistance** and tested mainly on that single device.
- It is **not affiliated with or endorsed by** the original author. Please do not report issues from this fork to the upstream project.
- Like the original, it is **not a security-grade lock** (it can be bypassed via ADB or recovery) and is not affiliated with Retroid or Nintendo.
- Provided **as is, without warranty**. Evaluate for yourself whether to use this fork.
