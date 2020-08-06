package com.popalay.tracktor

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable

@Composable
internal fun ThemedPreview(
    isDarkTheme: Boolean = false,
    children: @Composable () -> Unit
) {
    AppTheme(isDarkTheme = isDarkTheme) {
        Surface {
            children()
        }
    }
}