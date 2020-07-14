package com.popalay.tracktor.model

sealed class MenuItem(val displayName: String) {
    object FeatureFlagsMenuItem : MenuItem("Feature flags")
}