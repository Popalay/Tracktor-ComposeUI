package com.popalay.tracktor.data.model

sealed class MenuItem(val displayName: String) {
    object FeatureFlagsMenuItem : MenuItem("Feature flags")
}