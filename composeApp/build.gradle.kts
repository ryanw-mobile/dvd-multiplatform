@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jetbrains.kotlin.gradle.tasks.KotlinCompileTool
import org.jmailen.gradle.kotlinter.tasks.FormatTask
import org.jmailen.gradle.kotlinter.tasks.LintTask

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinter)
}

// Configuration
val productNamespace = "com.rwmobi.dvdmultiplatform"

kotlin {
    wasmJs {
        browser {
            commonWebpackConfig {
                outputModuleName = "composeApp"
                outputFileName = "composeApp.js"
                devServer =
                    (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                        static =
                            (static ?: mutableListOf()).apply {
                                // Serve sources to debug inside browser
                                add(project.projectDir.path)
                            }
                    }
            }
        }
        binaries.executable()
    }

    android {
        // Must differ from androidApp's namespace/applicationId — AGP rejects two modules
        // sharing one namespace once composeApp and androidApp are separate modules.
        namespace = "$productNamespace.shared"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()

        // Required so Compose Multiplatform's composeResources (drawables, strings, etc.) are
        // packaged as real Android resources — otherwise androidApp gets MissingResourceException
        // at runtime, since it only depends on composeApp as a library, not its raw source set.
        androidResources.enable = true

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

compose.resources {
    // Generated Res accessors are internal by default; androidApp (a separate module since the
    // KMP-android-library split) needs to reach them from its instrumented tests.
    publicResClass = true
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = productNamespace
            packageVersion = libs.versions.versionName.get()
        }
    }
}

tasks {
    check { dependsOn("detekt") }
}

// The KMP android library plugin doesn't create a 'preBuild' lifecycle task like
// com.android.application/library did, so hook formatting into every Kotlin compile task instead.
tasks.withType<KotlinCompileTool>().configureEach { dependsOn("formatKotlin") }

tasks.withType<LintTask> {
    exclude {
        it.file.path.contains("generated/")
                || it.file.path.contains("desktopMain")
                || it.file.path.contains("iosMain")
                || it.file.path.contains("wasmJsMain")
    }
}

tasks.withType<FormatTask> { exclude { it.file.path.contains("generated/") } }

detekt { parallel = true }
