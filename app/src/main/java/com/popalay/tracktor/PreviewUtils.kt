package com.popalay.tracktor

import androidx.compose.Composable
import androidx.ui.material.Surface

@Composable
internal fun ThemedPreview(
    isDarkTheme: Boolean = false,
    children: @Composable() () -> Unit
) {
    AppTheme(isDarkTheme = isDarkTheme) {
        Surface {
            children()
        }
    }
}