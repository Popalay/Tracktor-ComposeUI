plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("plugin.serialization") version Version.kotlin
}

kotlin {
    android()
    ios {
        binaries {
            framework {
                baseName = "domain"
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":data"))

                implementation(Libs.Workflow.core)
                implementation(Libs.Kotlinx.serialization)
                implementation(Libs.Kotlinx.datetime)
                implementation(Libs.Koin.core)
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