package com.popalay.tracktor

import androidx.compose.Composable
import androidx.ui.tooling.preview.Preview

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

@Preview("Light DropDownItem")
@Composable
fun previewDropDownItemLight() {
    ThemedPreview {
        DropDownItem("Item") {}
    }
}

@Preview("Dark DropDownItem")
@Composable
fun previewDropDownItemDark() {
    ThemedPreview(isDarkTheme = true) {
        DropDownItem("Item") {}
    }
}