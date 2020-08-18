package com.popalay.tracktor.feature.trackerdetail

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.popalay.tracktor.core.R
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
                title = { Text(stringResource(R.string.common_loading)) }
            )
        }
    ) {}
}