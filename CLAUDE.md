# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Kotlin Multiplatform (KMP) "DVD bouncing logo" screensaver app targeting Android, iOS, Desktop (JVM), and Web (WASM). It serves as an educational KMP demonstration.

- **Namespace**: `com.rwmobi.dvdmultiplatform`
- **Version**: 1.2.0
- **Min SDK**: Android 26, **Target/Compile SDK**: 36

## Common Commands

```bash
# Build
./gradlew build                          # Full build all targets
./gradlew composeApp:build               # Build only the shared KMP library module
./gradlew androidApp:assembleDebug       # Build only the Android app module

# Test
./gradlew :androidApp:pixel2Api35DebugAndroidTest   # Android instrumented tests (Pixel 2 API 35 managed device)

# Code quality (run before committing)
./gradlew formatKotlin                   # Format Kotlin code (Kotlinter)
./gradlew detekt                         # Static analysis (Detekt)
```

`formatKotlin` and `detekt` are wired into `preBuild` and `check` tasks respectively, so they run automatically during builds.

## Architecture

### Multiplatform Structure

All shared UI and business logic lives in `commonMain`. Platform-specific code is minimal — only entry points and `expect`/`actual` implementations.

```
composeApp/src/                  # KMP library module (com.android.kotlin.multiplatform.library
                                  # for the Android target — no application entry point here)
├── commonMain/kotlin/          # Shared UI (App.kt) + Platform.kt expect declarations
├── commonMain/composeResources/ # Shared SVG assets and strings
├── androidMain/                 # Platform.android.kt (actual only)
├── desktopMain/                 # main() entry point, Platform.jvm.kt
├── wasmJsMain/                  # WASM entry point, Platform.wasmJs.kt
└── iosMain/                     # MainViewController, Platform.ios.kt

androidApp/src/                  # Android application module (com.android.application)
├── main/                        # MainActivity, AndroidManifest.xml, launcher icons/strings
└── androidTest/                 # UI instrumented tests
```

### Platform Entry Points

| Platform | File | Pattern |
|---|---|---|
| Android | `androidApp/src/main/.../MainActivity.kt` | `ComponentActivity` + `setContent { App() }`, depends on `composeApp` |
| Desktop | `composeApp/src/desktopMain/.../Main.kt` | `application { Window { App() } }` |
| iOS | `composeApp/src/iosMain/.../MainViewController.kt` | Native controller wrapper |
| Web | `composeApp/src/wasmJsMain/.../Main.kt` | WASM browser entry |

### Key Files

- **`commonMain/kotlin/App.kt`** — All UI and physics: bouncing logo with velocity vectors, random color changes on wall collisions via `LaunchedEffect`
- **`commonMain/kotlin/Platform.kt`** — `expect` interface; each target provides `actual` implementation for platform name
- **`iosApp/iosApp.xcodeproj`** — Native iOS Xcode project wrapping the KMP framework

### Build Configuration

- Version catalog: `gradle/libs.versions.toml`
- Detekt config: `config/detekt/detekt.yml` (line length: 180, Compose-specific rules excluded)
- Release signing reads from CI environment variables
- APK output naming includes version and timestamp

## Tech Stack

- Kotlin 2.3.10 + Compose Multiplatform 1.10.2
- Compose BOM 2026.03.00
- Gradle 9.4.0 with Kotlin DSL, JDK 17
- Detekt 1.23.8, Kotlinter 5.3.0
