## DVD Multiplatform ![Gradle Build](https://github.com/ryanw-mobile/dvd-multiplatform/actions/workflows/main_build.yml/badge.svg)

### This is a Kotlin Multiplatform project targeting Android, iOS, Web, Desktop.

<br />
<p align="center">
  <img src="https://github.com/ryanw-mobile/dvd-multiplatform/blob/main/screenshot/240611_android_animated.gif" width="200" />&nbsp;&nbsp;&nbsp;&nbsp;
  <img src="https://github.com/ryanw-mobile/dvd-multiplatform/blob/main/screenshot/240611_all_animated.gif" width="600" />
<br />

## Complementary article

This is a sample app accompanying my Medium.com article [Build Your First Kotlin Multiplatform App: With Minimum KMP Knowledge - Achieve Maximum Efficiency with Minimal Effort](https://medium.com/@callmeryan/build-your-first-kotlin-multiplatform-app-with-minimum-kmp-knowledge-acc894dc270f). There you can learn how to set up a Kotlin Multiplatform (Compose Multiplatform) project from scratch, and build this app on Android, desktop, web and iOS.

<br />
<br />

## Some technical details

* `/composeApp` is for Kotlin code shared across the Compose Multiplatform application.
  It contains several subfolders:
    - `commonMain` is for code that’s common for all targets.
    - `androidMain` is the traditional Android project root.
    - `desktopMain` is for the desktop (JVM) app.
    - `wasmJsMain` is for the web app.
    - `iosMain` is for the Kotlin code to be exposed to the iOS app.

* `/iosApp` contains the iOS application. Open `iosApp.xcodeproj` to build the App.

### Dependencies

* [AndroidX Activity Compose](https://developer.android.com/jetpack/androidx/releases/activity) - Apache 2.0 - Jetpack Compose integration with Activity
* [AndroidX Test Ext JUnit](https://developer.android.com/jetpack/androidx/releases/test) - Apache 2.0 - Extensions for Android testing
* [AndroidX Espresso](https://developer.android.com/training/testing/espresso) - Apache 2.0 - UI testing framework

### Plugins

* [Android Application Plugin](https://developer.android.com/studio/build/gradle-plugin-3-0-0-migration) - Google - Plugin for building Android applications
* [Android Library Plugin](https://developer.android.com/studio/build/gradle-plugin-3-0-0-migration) - Google - Plugin for building Android libraries
* [Jetbrains Compose Plugin](https://github.com/JetBrains/compose-jb) - JetBrains - Plugin for Jetpack Compose
* [Compose Compiler Plugin](https://developer.android.com/jetpack/compose) - JetBrains - Plugin for Jetpack Compose
* [Kotlin Multiplatform Plugin](https://kotlinlang.org/docs/multiplatform.html) - JetBrains - Plugin for Kotlin Multiplatform projects
* [Ktlint Plugin](https://github.com/JLLeitschuh/ktlint-gradle) - JLLeitschuh - Plugin for Kotlin linter

This format provides a brief description and link for each dependency and plugin. Adjust the descriptions and links as needed for your specific project.

<br />
<br />

## License

```
   Copyright 2024 Ryan Wong @ RW MobiMedia UK Limited

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```

### Icon licenses

Every tiny piece matters. This App contains the icons contributed by:

* [Icons8 Flat Color Icons](https://github.com/icons8/flat-Color-icons) - MIT
