package com.popalay.tracktor.model

data class FeatureFlagListItem(
    val id: String,
    val displayName: String,
    val isEnabled: Boolean
) : ListItem