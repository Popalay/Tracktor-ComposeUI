package com.popalay.tracktor.ui.trackerdetail

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Undo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.popalay.tracktor.WindowInsetsAmbient
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.ui.widget.DefaultTopAppBarHeight
import com.popalay.tracktor.ui.widget.TopAppBar

@Composable
fun TrackerDetailsAppBar(
    tracker: TrackerWithRecords,
    onArrowClicked: () -> Unit = {},
    onUndoClicked: () -> Unit = {},
    onDeleteClicked: () -> Unit = {}
) {
    val insets = WindowInsetsAmbient.current
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onArrowClicked) {
                Icon(Icons.Default.ArrowBack)
            }
        },
        title = { Text(tracker.tracker.title) },
        actions = {
            if (tracker.records.size > 1) {
                IconButton(onClick = onUndoClicked) {
                    Icon(Icons.Default.Undo)
                }
            }
            IconButton(onClick = onDeleteClicked) {
                Icon(Icons.Default.DeleteForever)
            }
        },
        modifier = Modifier.preferredHeight(DefaultTopAppBarHeight + insets.top),
        contentModifier = Modifier.padding(top = insets.top)
    )
}