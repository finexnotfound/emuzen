# EmuZen

**EmuZen** is a minimalist, distraction-free retro gaming launcher and emulation frontend for Android. Crafted with an Apple-inspired design philosophy, EmuZen pairs a high-contrast OLED dark aesthetic with translucent glassmorphic surfaces, tactile virtual controls, and a modular emulation architecture.

---

## 📥 Download APK

You can download and install the pre-built APK directly:
- **Download**: [`release/EmuZen-debug.apk`](./release/EmuZen-debug.apk) (Debug APK ready for direct sideloading on Android 12+)
- Alternatively, you can generate fresh debug/release APKs directly inside Google AI Studio via the top-right settings/export menu.

---

## Highlights & Features

### 🎮 Minimalist Retro Launcher
- **OLED Dark Canvas**: True black `#0B0C0E` background engineered for modern AMOLED displays with subtle neon accents and glassmorphic card borders.
- **Spotlight Search & Filter**: Real-time instant search across titles and systems, console filter chips, and sorting by Recently Played, Recently Added, Alphabetical, or Console.
- **Grid & List Views**: Flexible display modes with custom game cards, play time counters, and last-played timestamps.
- **Built-in Homebrew Demo**: Jump right into *Zen Quest* without needing ROMs upfront to test input responsiveness, physics, and save states.

### 🕹️ Custom Virtual On-Screen Controllers
- **Console-Tailored Layouts**:
  - **Nintendo (NES / Game Boy / GBC / GBA / SNES)**: Classic cross D-Pad, angled or horizontal A/B, and diamond X/Y layout with L/R shoulder bumpers.
  - **Nintendo 64**: Dedicated analog thumbstick, yellow 4-way C-button cluster, A/B buttons, and Z trigger.
  - **Nintendo DS / 3DS**: Dual-screen rendering with interactive touch/stylus support on the lower display.
  - **Sony PlayStation 1 & PSP**: Triangle, Circle, Cross, Square action diamond with analog nub.
  - **Sega & Atari**: Classic 3-button and single-action layouts.
- **Adaptive Layout Modes**:
  - **Portrait**: Upper game display with dedicated lower touch control pad.
  - **Landscape**: Edge-to-edge centered game screen flanked by translucent thumb controls.
- **Tactile Customization**: Real-time sliders for button opacity and scale, plus haptic vibration feedback on tap.

### ⚡ Emulation & In-Game Experience
- **Modular Core Architecture**: Decoupled UI layer and emulation engines with clean lifecycle management (`IEmuCore`).
- **Native Game Boy Engine**: Scanline PPU execution (160×144), 4-shade LCD color palette, joypad interrupts, and frame-rate regulation.
- **Glassmorphic In-Game Quick Menu**:
  - **Multi-Slot Save States**: 5 independent save slots with timestamps and state metadata.
  - **Fast Forward**: Toggle emulation speed dynamically (1.5×, 2.0×, 3.0×, 4.0×, 8.0×).
  - **Display Tweaks**: Toggle aspect ratios (Original, 4:3, 16:9, Stretch), integer pixel scaling, and bilinear smoothing filters.
  - **FPS Counter**: Real-time performance monitor.

### ⏱️ Playtime Tracking & Activity
- Automatic session recording saved locally via **Room Database**.
- Aggregated total gaming hours and per-game history.

---

## Supported Systems

| System | Short Name | File Extensions |
| :--- | :---: | :--- |
| **Game Boy** | GB | `.gb` |
| **Game Boy Color** | GBC | `.gbc` |
| **Game Boy Advance** | GBA | `.gba` |
| **Nintendo Entertainment System** | NES | `.nes` |
| **Super Nintendo** | SNES | `.smc`, `.sfc` |
| **Nintendo 64** | N64 | `.n64`, `.z64`, `.v64` |
| **Nintendo DS** | NDS | `.nds` |
| **Nintendo 3DS** | 3DS | `.3ds`, `.cia` |
| **Sony PlayStation 1** | PS1 | `.iso`, `.bin`, `.cue` |
| **Sony PlayStation Portable** | PSP | `.cso`, `.pbp` |
| **Sega Game Gear** | GG | `.gg` |
| **Atari 2600** | A26 | `.a26` |

---

## Architecture & Tech Stack

- **UI Framework**: 100% Jetpack Compose with Material Design 3 (M3).
- **State Management**: Android Architecture Components `ViewModel` + Kotlin `StateFlow`.
- **Database & Storage**: Room Database + Kotlin Symbol Processing (KSP) + SharedPreferences for user settings.
- **Coroutines & Concurrency**: Kotlin Coroutines and StateFlow for game loop execution and database queries.
- **Testing**: Robolectric JVM unit tests and Roborazzi visual regression tests.

---

## Legal & Compliance Notice

EmuZen is an independent open-source emulator frontend. **No copyrighted ROMs, proprietary BIOS images, or commercial game assets are distributed with this software.** Users must provide legally acquired ROM files for personal backup and emulation use.
