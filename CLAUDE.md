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
./gradlew composeApp:build               # Build only the app module

# Test
./gradlew androidInstrumentedTest        # Android instrumented tests (Pixel 2 API 35 managed device)

# Code quality (run before committing)
./gradlew formatKotlin                   # Format Kotlin code (Kotlinter)
./gradlew detekt                         # Static analysis (Detekt)
```

`formatKotlin` and `detekt` are wired into `preBuild` and `check` tasks respectively, so they run automatically during builds.

## Architecture

### Multiplatform Structure

All shared UI and business logic lives in `commonMain`. Platform-specific code is minimal — only entry points and `expect`/`actual` implementations.

```
composeApp/src/
├── commonMain/kotlin/          # Shared UI (App.kt) + Platform.kt expect declarations
├── commonMain/composeResources/ # Shared SVG assets and strings
├── androidMain/                 # MainActivity, Platform.android.kt
├── desktopMain/                 # main() entry point, Platform.jvm.kt
├── wasmJsMain/                  # WASM entry point, Platform.wasmJs.kt
├── iosMain/                     # MainViewController, Platform.ios.kt
└── androidInstrumentedTest/     # UI integration tests
```

### Platform Entry Points

| Platform | File | Pattern |
|---|---|---|
| Android | `androidMain/.../MainActivity.kt` | `ComponentActivity` + `setContent { App() }` |
| Desktop | `desktopMain/.../Main.kt` | `application { Window { App() } }` |
| iOS | `iosMain/.../MainViewController.kt` | Native controller wrapper |
| Web | `wasmJsMain/.../Main.kt` | WASM browser entry |

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
