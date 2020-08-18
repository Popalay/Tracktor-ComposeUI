package com.popalay.tracktor.ui.trackerdetail

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import com.popalay.tracktor.ui.widget.TopAppBar

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
                title = { Text(text = "Loading...") }
            )
        }
    ) {}
}