# Changelog

## 1.3-fork — changes since 1.2.2

Builds from this fork are versioned `1.3-fork.<build>` (e.g. `1.3-fork.6`). Signed fork builds
install over each other as updates, keeping all settings.

### New features

- **Battery indicator on the lock screen** (optional, Appearance → Battery)
  - Battery icon whose fill shows the remaining charge, so it is readable even with the
    percentage turned off.
  - Lightning bolt while charging; only the fill switches to the charging color, the outline
    and percentage keep the normal color.
  - Show/hide percentage, six position presets (corners and top/bottom center) plus free
    X/Y position, adjustable size, and separate colors (with opacity) for normal and charging.
  - Updates live while the lock screen is shown; no extra permission required.
- **Configurable unlock buttons** (from upstream 1.3, Options → Buttons…)
  - Buttons pressed on the lock screen are listed; remove any you do not want to count as an
    unlock press, or add one by pressing it.
  - D-pad now counts as a button on handhelds that report it only as an analog HAT axis.
  - New toggles: count screen taps, count analog L2/R2 triggers.
  - Anti-lockout fallback keeps a way to unlock even if every method is turned off.
- **Apps never stopped** (Options → Apps never stopped…)
  - Choose apps that must only be paused, never stopped, while locked (for apps that crash
    after being stopped). Defaults to every RetroArch variant (`com.retroarch`,
    `com.retroarch.aarch64`, `com.retroarch.ra32`), replacing the previous hard-coded
    64-bit RetroArch only.

### Improvements and fixes

- Emulators that keep running behind the lock screen are now detected more reliably: the app
  can see other installed apps on Android 11+, which the audio-playback check depends on.
- Anti-lockout fix: enabled analog triggers only count as an unlock method once an analog
  trigger has actually been pressed, so devices with digital L2/R2 cannot be locked out.
- The lock-screen background is decoded only as large as the screen needs (typically a few MB
  instead of up to ~64 MB) and prepared in the background when the service starts, so the
  first lock after a restart is ready sooner.

### Build and distribution

- APKs are built automatically by GitHub Actions on every push and signed with this fork's
  own release key, so new builds install as updates.
- Note: the signing key differs from the original author's releases; switching between the
  official app and this fork requires uninstalling first.
