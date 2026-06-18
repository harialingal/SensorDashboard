# 📡 SensorDashboard

> 🤖 **This project was built entirely with AI using [Antigravity](https://antigravity.dev) — from code generation to deployment — without writing a single line of code manually.**

**Current Version: `v1.1.0`**

A beautiful Android app that displays all device sensor data in a real-time dashboard, built entirely with **Jetpack Compose** and **Kotlin**.

---

## ✨ Features

- **Dashboard Screen** — Live grid of all hardware sensors on your device
  - Search and filter by sensor category (Motion, Environment, Position, Other)
  - Real-time mini sparkline charts per sensor card
  - Live X/Y/Z axis readings updating continuously

- **Detail Screen** — Tap any sensor for a full deep-dive
  - Live spinning gauge indicator
  - Full-width history sparkline chart
  - Per-axis readings with correct units (m/s², µT, lux, hPa, etc.)
  - Hardware metadata: Vendor, Resolution, Max Range, Power Draw, Min Delay

- **Premium Design**
  - Dark theme with glassmorphism-style cards
  - Smooth animations and micro-interactions
  - Custom Canvas-drawn sparkline charts

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + StateFlow |
| Navigation | Jetpack Navigation 3 |
| Sensors | Android SensorManager |
| Build | Gradle with Version Catalog |

---

## 📁 Project Structure

```
app/src/main/java/com/example/sensordashboard/
├── MainActivity.kt
├── Navigation.kt
├── NavigationKeys.kt
├── data/
│   ├── SensorModels.kt       # Data models
│   ├── SensorRepository.kt   # SensorEventListener registration
│   └── DataRepository.kt     # Bridges sensors to ViewModels
├── theme/
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
└── ui/
    ├── components/
    │   ├── SensorCard.kt      # Dashboard sensor card
    │   └── SparklineChart.kt  # Custom Canvas sparkline
    ├── main/
    │   ├── DashboardScreen.kt
    │   └── DashboardViewModel.kt
    └── detail/
        ├── DetailScreen.kt
        └── DetailViewModel.kt
```

---

## 🚀 Getting Started

1. Clone the repository
   ```bash
   git clone https://github.com/harialingal/SensorDashboard.git
   ```
2. Open in **Android Studio** (Hedgehog or newer)
3. Run on a physical Android device (API 24+) — physical device recommended for real sensor data

---

## 📋 Changelog

### `v1.1.0` — 2026-06-18
**Bug Fix: Detail Screen Navigation**
- Fixed a bug where tapping any sensor after the first would reopen the first sensor's detail screen
- **Root cause:** Jetpack Navigation 3 does not automatically scope `ViewModel` instances per backstack entry by default. The `DetailViewModel` was resolving against the Activity's `ViewModelStore`, caching the first instance for all subsequent navigations
- **Fix:** Added `rememberSaveableStateHolderNavEntryDecorator()` and `rememberViewModelStoreNavEntryDecorator()` to `NavDisplay` in `Navigation.kt`, ensuring each sensor detail screen gets its own isolated `ViewModel` and lifecycle

**Repository**
- Initial push to GitHub: [github.com/harialingal/SensorDashboard](https://github.com/harialingal/SensorDashboard)
- Cleaned up misplaced file paths in repo root
- Added AI attribution to README

---

### `v1.0.0` — 2026-06-15
**Initial Release**
- Created full Android project with Jetpack Compose
- **Dashboard Screen:**
  - Displays all hardware sensors available on the device
  - Dynamic top bar with total sensor count
  - Search bar to filter by sensor name/vendor
  - Category filter chips: All · Motion · Environment · Position · Other
  - Colorful stats tiles per sensor type
  - 2-column grid of live sensor cards with sparklines
- **Detail Screen:**
  - Live spinning accuracy gauge
  - Full-width sensor history sparkline chart
  - Per-axis readings with units (m/s², µT, lux, hPa, °, etc.)
  - Full hardware metadata panel (Vendor, Version, Resolution, Max Range, Power, Min Delay)
- **Theming:**
  - Dark theme with glassmorphism-style cards
  - Custom Canvas-based sparkline chart component
  - Material 3 with extended icons
- **Architecture:**
  - MVVM with `StateFlow` and `collectAsStateWithLifecycle`
  - `SensorRepository` using `SensorEventListener` with Coroutines
  - Jetpack Navigation 3 with typed `NavKey` destinations
- **Build:**
  - JDK 17 (JetBrains Runtime `jbr-17.0.11`)
  - Gradle version catalog (`libs.versions.toml`)
  - Fixed missing `material-icons-extended` dependency
- **Deployment:**
  - Built and deployed via ADB to physical Android device (`e4735390`)
  - USB debugging authorized and app launched on device

---

## 🤖 Built with AI

This entire project — including architecture design, Kotlin/Compose code, bug fixes, and GitHub deployment — was created using **[Antigravity](https://antigravity.dev)**, an AI-powered coding agent by Google DeepMind. No code was written manually.

---

## 📄 License

MIT License — feel free to use and modify.