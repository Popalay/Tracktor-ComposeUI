package com.popalay.tracktor.ui.trackerdetail

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.material.IconButton
import androidx.ui.material.Scaffold
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowBack
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