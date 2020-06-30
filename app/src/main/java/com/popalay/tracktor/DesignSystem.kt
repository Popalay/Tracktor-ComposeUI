package com.popalay.tracktor

import androidx.compose.Composable
import androidx.ui.tooling.preview.Preview
import com.popalay.tracktor.ui.create.CreateTrackedValue

@Preview("Light CreateTrackedValue")
@Composable
fun previewCreateTrackedValueLight() {
    ThemedPreview {
        CreateTrackedValue()
    }
}

@Preview("Dark CreateTrackedValue")
@Composable
fun previewCreateTrackedValueDark() {
    ThemedPreview(isDarkTheme = true) {
        CreateTrackedValue()
    }
}