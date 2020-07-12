package com.popalay.tracktor.ui.trackerdetail

import androidx.compose.Composable
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.material.IconButton
import androidx.ui.material.Scaffold
import androidx.ui.material.TopAppBar
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowBack

@Composable
fun TrackerDetailLoadingView(onArrowClicked: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onArrowClicked) {
                        Icon(Icons.Default.ArrowBack)
                    }
                },
                title = { Text(text = "Loading...") })
        }
    ) {}
}