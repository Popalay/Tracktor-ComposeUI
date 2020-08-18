plugins {
    id("com.android.library")
    kotlin("android")
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

    implementation(Libs.Compose.animation)
    implementation(Libs.Compose.foundation)
    implementation(Libs.Compose.foundationLayout)
    implementation(Libs.Compose.material)
    implementation(Libs.Compose.materialIconsExtended)
    implementation(Libs.Compose.runtime)
    implementation(Libs.Compose.ui)

    implementation(Libs.Workflow.core)

    implementation(Libs.Koin.core)
    implementation(Libs.Moshi.core)
}