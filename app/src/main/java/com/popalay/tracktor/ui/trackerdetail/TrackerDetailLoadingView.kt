package com.popalay.tracktor.ui.trackerdetail

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.popalay.tracktor.WindowInsetsAmbient
import com.popalay.tracktor.ui.widget.DefaultTopAppBarHeight
import com.popalay.tracktor.ui.widget.TopAppBar

@Composable
fun TrackerDetailLoadingView(onArrowClicked: () -> Unit = {}) {
    val insets = WindowInsetsAmbient.current
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onArrowClicked) {
                        Icon(Icons.Default.ArrowBack)
                    }
                },
                title = { Text(text = "Loading...") },
                modifier = Modifier.preferredHeight(insets.top + DefaultTopAppBarHeight),
                contentModifier = Modifier.padding(top = insets.top)
            )
        }
    ) {}
}