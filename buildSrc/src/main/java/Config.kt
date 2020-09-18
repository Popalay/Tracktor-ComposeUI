object AndroidConfig {
    const val compileSdk = 30
    const val minSdk = 26
    const val targetSdk = 30

    const val versionName = "1.8"
}

object Version {
    const val androidGradlePlugin = "4.2.0-alpha10"
    const val kotlin = "1.4.10"
    const val koin = "3.0.0-alpha-4"
    const val compose = "1.0.0-alpha02"
    const val workflow = "1.0.0-alpha.1"
    const val workflowCompose = "0.30.0"
    const val androidX = "1.5.0-alpha02"
    const val sqlDelight = "1.4.3"
    const val coroutines = "1.3.9"
}

object Libs {
    const val kotlinStd = "org.jetbrains.kotlin:kotlin-stdlib:${Version.kotlin}"
    const val materialDesign = "com.google.android.material:material:1.2.0"
    const val androidXCore = "androidx.core:core-ktx:${Version.androidX}"
    const val uuid = "com.benasher44:uuid:0.2.2"

    object Compose {
        const val animation = "androidx.compose.animation:animation:${Version.compose}"
        const val foundation = "androidx.compose.foundation:foundation:${Version.compose}"
        const val foundationLayout = "androidx.compose.foundation:foundation-layout:${Version.compose}"
        const val material = "androidx.compose.material:material:${Version.compose}"
        const val runtime = "androidx.compose.runtime:runtime:${Version.compose}"
        const val ui = "androidx.compose.ui:ui:${Version.compose}"
    }

    object Workflow {
        const val core = "com.squareup.workflow:workflow-core"
        const val runtime = "com.squareup.workflow:workflow-runtime"
        const val compose = "com.squareup.workflow:core-compose"
        const val composeTooling = "com.squareup.workflow:compose-tooling"
    }

    object Koin {
        const val core = "org.koin:koin-core:${Version.koin}"
        const val android = "org.koin:koin-android:${Version.koin}"
    }

    object SqlDelight {
        const val runtime = "com.squareup.sqldelight:runtime:${Version.sqlDelight}"
        const val android = "com.squareup.sqldelight:android-driver:${Version.sqlDelight}"
        const val native = "com.squareup.sqldelight:native-driver:${Version.sqlDelight}"
        const val coroutinesExtensions = "com.squareup.sqldelight:coroutines-extensions:${Version.sqlDelight}"
    }

    object Kotlinx {
        const val datetime = "org.jetbrains.kotlinx:kotlinx-datetime:0.1.0"
        const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0-RC"
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.coroutines}"
        const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.coroutines}"
    }
}