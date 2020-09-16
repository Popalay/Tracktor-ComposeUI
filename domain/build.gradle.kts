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

val packForXcode by tasks.creating(Sync::class) {
    group = "build"
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val sdkName = System.getenv("SDK_NAME") ?: "iphonesimulator"
    val targetName = "ios" + if (sdkName.startsWith("iphoneos")) "Arm64" else "X64"
    val framework = kotlin.targets.getByName<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>(targetName).binaries.getFramework(mode)
    inputs.property("mode", mode)
    dependsOn(framework.linkTask)
    val targetDir = File(buildDir, "xcode-frameworks")
    from({ framework.outputDirectory })
    into(targetDir)
}
tasks.getByName("build").dependsOn(packForXcode)