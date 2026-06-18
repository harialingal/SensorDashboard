# 📡 SensorDashboard

> 🤖 **This project was built entirely with AI using [Antigravity](https://antigravity.dev) — from code generation to deployment — without writing a single line of code manually.**

A beautiful Android app that displays all device sensor data in a real-time dashboard, built entirely with **Jetpack Compose** and **Kotlin**.

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

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + StateFlow |
| Navigation | Jetpack Navigation 3 |
| Sensors | Android SensorManager |
| Build | Gradle with Version Catalog |

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

## 🚀 Getting Started

1. Clone the repository
   ```bash
   git clone https://github.com/harialingal/SensorDashboard.git
   ```
2. Open in **Android Studio** (Hedgehog or newer)
3. Run on a physical Android device (API 24+) — physical device recommended for real sensor data

## 📱 Screenshots

*App running on a physical Android device, showing live accelerometer, gyroscope, magnetometer, and 20+ other sensors.*

## 🤖 Built with AI

This entire project — including architecture design, Kotlin/Compose code, bug fixes, and GitHub deployment — was created using **[Antigravity](https://antigravity.dev)**, an AI-powered coding agent by Google DeepMind. No code was written manually.

## 📄 License

MIT License — feel free to use and modify.