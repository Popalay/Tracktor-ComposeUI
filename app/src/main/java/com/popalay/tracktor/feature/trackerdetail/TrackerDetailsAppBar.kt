package com.popalay.tracktor.feature.trackerdetail

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.vectorResource
import com.popalay.tracktor.core.R
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
                    Icon(vectorResource(R.drawable.undo_24px))
                }
            }
            IconButton(onClick = onDeleteClicked) {
                Icon(vectorResource(R.drawable.delete_forever_24px))
            }
        }
    )
}