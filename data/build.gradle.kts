import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("com.android.library")
    id("com.squareup.sqldelight") version Version.sqlDelight
    id("org.jetbrains.kotlin.native.cocoapods")
    kotlin("multiplatform")
    kotlin("plugin.serialization") version Version.kotlin
}

version = AndroidConfig.versionName

kotlin {
    android()

    val iOSTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true) ::iosArm64 else ::iosX64

    iOSTarget("ios"){}

    cocoapods {
        summary = "Shared Data code for Android/iOS"
        homepage = "Link to a Kotlin/Native module homepage"
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Libs.Kotlinx.datetime)
                implementation(Libs.Kotlinx.serialization)
                implementation(Libs.Kotlinx.coroutinesCore)
                implementation(Libs.uuid)
                implementation(Libs.Koin.core)
                implementation(Libs.SqlDelight.runtime)
                implementation(Libs.SqlDelight.coroutinesExtensions)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Libs.Kotlinx.coroutinesAndroid)
                implementation(Libs.SqlDelight.android)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(Libs.SqlDelight.native)
            }
        }
        val commonTest by getting
        val androidTest by getting
        val iosTest by getting
    }
}

sqldelight {
    database("TracktorDatabase") {
        packageName = "com.popalay.tracktor.db"
        sourceFolders = listOf("sqldelight")
    }
}

android {
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        consumerProguardFile("proguard-rules.pro")
    }
}