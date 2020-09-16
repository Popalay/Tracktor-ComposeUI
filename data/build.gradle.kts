plugins {
    id("com.android.library")
    id("com.squareup.sqldelight") version Version.sqlDelight
    kotlin("multiplatform")
    kotlin("plugin.serialization") version Version.kotlin
}

kotlin {
    android()
    ios {
        binaries {
            framework {
                baseName = "data"
            }
        }
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