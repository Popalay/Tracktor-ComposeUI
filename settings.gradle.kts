import java.net.URI

include(":app")
include(":data")
include(":core")
include(":domain")

rootProject.name = "Tracktor"

enableFeaturePreview("GRADLE_METADATA")

if (System.getenv("CI") != "true") {
    includeBuild("../workflow") {
        dependencySubstitution {
            substitute(module("com.squareup.workflow:core-compose")).with(project(":core-compose"))
            substitute(module("com.squareup.workflow:compose-tooling")).with(project(":compose-tooling"))
            substitute(module("com.squareup.workflow:workflow-core")).with(project(":workflow-core"))
            substitute(module("com.squareup.workflow:workflow-runtime")).with(project(":workflow-runtime"))
        }
    }
} else {
    sourceControl {
        gitRepository(URI("https://github.com/Popalay/workflow.git")) {
            producesModule("com.squareup.workflow:core-compose")
            producesModule("com.squareup.workflow:compose-tooling")
            producesModule("com.squareup.workflow:workflow-core")
            producesModule("com.squareup.workflow:workflow-runtime")
        }
    }
}

pluginManagement {
    repositories {
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap/")
        gradlePluginPortal()
        mavenCentral()
        jcenter()
        google()
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.android.application") {
                useModule("com.android.tools.build:gradle:${requested.version}")
            }
        }
    }
}