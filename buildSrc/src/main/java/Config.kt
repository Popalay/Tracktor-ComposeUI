object AndroidConfig {
    const val compileSdk = 29
    const val minSdk = 26
    const val targetSdk = 29

    const val versionName = "1.7"
}

object Version {
    const val androidGradlePlugin = "4.2.0-alpha07"
    const val kotlin = "1.4.0"
    const val koin = "2.1.6"
    const val moshi = "1.9.3"
    const val compose = "0.1.0-dev17"
    const val workflow = "1.0.0-alpha.1"
    const val workflowCompose = "0.30.0"
    const val room = "2.2.5"
}

object Libs {
    const val kotlinStd = "org.jetbrains.kotlin:kotlin-stdlib:${Version.kotlin}"
    const val materialDesign = "com.google.android.material:material:1.2.0"
    const val preferenceKtx = "androidx.preference:preference-ktx:1.1.1"

    object Room {
        const val runtime = "androidx.room:room-runtime:${Version.room}"
        const val ktx = "androidx.room:room-ktx:${Version.room}"
        const val compiler = "androidx.room:room-compiler:${Version.room}"
    }

    object Compose {
        const val animation = "androidx.compose.animation:animation:${Version.compose}"
        const val foundation = "androidx.compose.foundation:foundation:${Version.compose}"
        const val foundationLayout = "androidx.compose.foundation:foundation-layout:${Version.compose}"
        const val material = "androidx.compose.material:material:${Version.compose}"
        const val materialIconsExtended = "androidx.compose.material:material-icons-extended:${Version.compose}"
        const val runtime = "androidx.compose.runtime:runtime:${Version.compose}"
        const val ui = "androidx.compose.ui:ui:${Version.compose}"
    }

    object Workflow {
        const val core = "com.squareup.workflow:workflow-core-jvm:${Version.workflow}"
        const val runtime = "com.squareup.workflow:workflow-runtime-jvm:${Version.workflow}"
        const val compose = "com.squareup.workflow:workflow-ui-core-compose:${Version.workflowCompose}"
        const val composeTooling = "com.squareup.workflow:workflow-ui-compose-tooling:${Version.workflowCompose}"
    }

    object Koin {
        const val core = "org.koin:koin-core:${Version.koin}"
        const val android = "org.koin:koin-android:${Version.koin}"
    }

    object Moshi {
        const val core = "com.squareup.moshi:moshi:${Version.moshi}"
        const val codegen = "com.squareup.moshi:moshi-kotlin-codegen:${Version.moshi}"
    }
}