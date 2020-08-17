plugins {
    id("com.android.library")
    id("kotlin-android")
}

dependencies {
    implementation(project(":data"))
    implementation(Libs.kotlinStd)

    implementation(Libs.Workflow.core)

    implementation(Libs.Koin.core)
}