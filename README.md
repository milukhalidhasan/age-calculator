# Age Calculator — Android App

A clean, professional **Date-to-Date Age Calculator** built with **Kotlin + Jetpack Compose (Material 3)**.

## Features
- Calculate exact age between any two dates: **years, months, days**.
- Input dates two ways:
  - **Type** them directly (accepts `05/03/1990`, `5-3-1990`, `1990-03-05`, `05 Mar 1990`).
  - **Built-in calendar** picker (tap the calendar icon in any field).
- Extra details:
  - Total **months, weeks, days, hours, minutes, seconds**
  - **Day of the week** you were born
  - **Zodiac sign**
  - **Next birthday** — date, weekday, countdown in days, and the age you'll turn
  - A friendly "Happy Birthday" state when the birthday is today
- Light & dark theme (follows the system), edge-to-edge modern look.

## Requirements
- Android Studio (Koala / Ladybug or newer recommended)
- Android device or emulator running **Android 8.0 (API 26)** or higher

## How to build & run
1. Unzip the project.
2. In Android Studio: **File ▸ Open** and select the `AgeCalculator` folder.
3. Let Gradle sync (it downloads the Android Gradle Plugin and dependencies, and
   generates the Gradle wrapper automatically).
4. Press **Run ▶** with a device/emulator selected.

### Build an installable APK
- **Build ▸ Build Bundle(s) / APK(s) ▸ Build APK(s)**
- The debug APK lands in `app/build/outputs/apk/debug/app-debug.apk`.

## Project layout
```
AgeCalculator/
├─ app/
│  ├─ build.gradle.kts
│  └─ src/main/
│     ├─ AndroidManifest.xml
│     ├─ java/com/ageapp/agecalculator/
│     │  ├─ MainActivity.kt        # All Compose UI
│     │  ├─ AgeCalculator.kt       # Pure calculation logic
│     │  └─ ui/theme/              # Colors, typography, Material 3 theme
│     └─ res/                      # Icons, strings, theme resources
├─ build.gradle.kts
├─ settings.gradle.kts
└─ preview.html                    # Open in a browser for a quick design preview
```

## Notes
- The calculation logic in `AgeCalculator.kt` is pure Kotlin (no Android
  dependencies), so it's easy to unit-test or reuse.
- Package name / applicationId is `com.ageapp.agecalculator` — change it in
  `app/build.gradle.kts` and the manifest if you plan to publish.
