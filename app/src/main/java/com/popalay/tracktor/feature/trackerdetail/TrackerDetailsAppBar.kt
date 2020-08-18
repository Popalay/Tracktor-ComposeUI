package com.popalay.tracktor.feature.trackerdetail

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Undo
import androidx.compose.runtime.Composable
import com.popalay.tracktor.data.model.TrackerWithRecords
import com.popalay.tracktor.ui.widget.TopAppBar

@Composable
fun TrackerDetailsAppBar(
    tracker: TrackerWithRecords,
    onArrowClicked: () -> Unit = {},
    onUndoClicked: () -> Unit = {},
    onDeleteClicked: () -> Unit = {}
) {
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
        }
    )
}