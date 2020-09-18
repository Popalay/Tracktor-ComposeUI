plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.serialization") version Version.kotlin
}

android {
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerVersion = Version.kotlin
        kotlinCompilerExtensionVersion = Version.compose
    }
}

dependencies {
    implementation(project(":data"))

    implementation(Libs.kotlinStd)
    implementation(Libs.materialDesign)
    implementation(Libs.androidXCore)
    implementation(Libs.Kotlinx.datetime)
    implementation(Libs.Kotlinx.serialization)
    implementation(Libs.Compose.animation)
    implementation(Libs.Compose.foundation)
    implementation(Libs.Compose.foundationLayout)
    implementation(Libs.Compose.material)
    implementation(Libs.Compose.runtime)
    implementation(Libs.Compose.ui)
    implementation(Libs.Koin.core)
}