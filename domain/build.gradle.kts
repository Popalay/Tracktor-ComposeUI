import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.native.cocoapods")
    kotlin("multiplatform")
    kotlin("plugin.serialization") version Version.kotlin
}

version = AndroidConfig.versionName

kotlin {
    android()

    val iOSTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true) ::iosArm64 else ::iosX64

    iOSTarget("ios") {}

    cocoapods {
        summary = "Shared Domain code for Android/iOS"
        homepage = "Link to a Kotlin/Native module homepage"
        podfile = project.file("../ios/Tracktor/Podfile")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":data"))

                implementation(Libs.uuid)
                implementation(Libs.Kotlinx.serialization)
                api(Libs.Workflow.core) {
                    version { branch = "popalay/multiplatform" }
                }
                api(Libs.Kotlinx.datetime)
                api(Libs.Koin.core)
            }
        }
        val androidMain by getting
        val iosMain by getting
        val commonTest by getting
        val androidTest by getting
        val iosTest by getting
    }
}

android {
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        consumerProguardFile("proguard-rules.pro")
    }
}