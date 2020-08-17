plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
}

val isCI = System.getenv("CI") == "true"
println("Is CI environment: $isCI")

android {
    defaultConfig {
        applicationId = "com.popalay.tracktor"
        resConfigs("en")

        signingConfigs {
            getByName("debug") {
                storeFile = file("../release/debug.keystore")
            }
            register("release") {
                storeFile = file("../release/release.keystore")
                keyAlias = "tracktor"
                storePassword = System.getenv("ANDROID_RELEASE_KEYSTORE_PWD") ?: ""
                keyPassword = System.getenv("ANDROID_RELEASE_KEY_PWD") ?: ""
            }
        }

        buildTypes {
            getByName("debug") {
                signingConfig = signingConfigs.getByName("debug")
                versionNameSuffix = "-dev"
                applicationIdSuffix = ".debug"
            }

            getByName("release") {
                signingConfig = if (isCI) signingConfigs.getByName("release") else signingConfigs.getByName("debug")
                isMinifyEnabled = true
                proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            }
        }

        lintOptions {
            isCheckReleaseBuilds = false
            isCheckDependencies = true
            isIgnoreTestSources = true
        }
    }

    buildFeatures {
        compose = true
    }

    dexOptions {
        // Don't pre-dex on CI
        preDexLibraries = !isCI
    }

    composeOptions {
        kotlinCompilerVersion = Version.kotlin
        kotlinCompilerExtensionVersion = Version.compose
    }

    packagingOptions {
        excludes.addAll(
            setOf(
                "META-INF/*.version",
                "META-INF/proguard/*",
                "/*.properties",
                "fabric/*.properties",
                "META-INF/*.properties",
                "META-INF/*.kotlin_module"
            )
        )
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        freeCompilerArgs = listOf("-Xallow-jvm-ir-dependencies", "-Xskip-prerelease-check", "-Xskip-metadata-version-check")
    }
}
dependencies {
    implementation(project(":data"))
    implementation(Libs.kotlinStd)
    implementation(Libs.materialDesign)
    implementation(Libs.preferenceKtx)
    implementation(Libs.insetterKtx)

    implementation(Libs.Compose.animation)
    implementation(Libs.Compose.foundation)
    implementation(Libs.Compose.foundationLayout)
    implementation(Libs.Compose.material)
    implementation(Libs.Compose.materialIconsExtended)
    implementation(Libs.Compose.runtime)
    implementation(Libs.Compose.ui)

    implementation(Libs.Workflow.core)
    implementation(Libs.Workflow.runtime)
    if (!isCI) {
        implementation(Libs.Workflow.compose)
        implementation(Libs.Workflow.composeTooling)
    } else {
        implementation("com.squareup.workflow:core-compose") {
            version {
                branch = "main"
            }
        }
        implementation("com.squareup.workflow:compose-tooling") {
            version {
                branch = "main"
            }
        }
    }

    implementation(Libs.Koin.core)
    implementation(Libs.Koin.android)

    implementation(Libs.Moshi.core)
    kapt(Libs.Moshi.codegen)
}