package com.popalay.tracktor.ui.trackerdetail

import androidx.compose.Composable
import androidx.compose.remember
import androidx.compose.state
import androidx.ui.animation.animate
import androidx.ui.core.Modifier
import androidx.ui.core.gesture.DragObserver
import androidx.ui.core.gesture.dragGestureFilter
import androidx.ui.foundation.Icon
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.geometry.Offset
import androidx.ui.layout.Column
import androidx.ui.layout.Spacer
import androidx.ui.layout.height
import androidx.ui.layout.offset
import androidx.ui.layout.padding
import androidx.ui.material.Divider
import androidx.ui.material.FloatingActionButton
import androidx.ui.material.Scaffold
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Add
import androidx.ui.unit.dp
import com.popalay.tracktor.WindowInsetsAmbient
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.ui.dialog.UpdateTrackedValueDialog

@Composable
fun TrackerDetailContentView(
    trackerWithRecords: TrackerWithRecords,
    isAddRecordDialogShowing: Boolean = false,
    onAction: (TrackerDetailWorkflow.Action) -> Unit
) {
    val insets = WindowInsetsAmbient.current
    val isScrolled = state { false }
    val fabOffset = animate(if (isScrolled.value) 72.dp + insets.bottom else -insets.bottom)

    val dragObserver = remember {
        object : DragObserver {
            override fun onStart(downPosition: Offset) {
                isScrolled.value = true
            }

            override fun onStop(velocity: Offset) {
                isScrolled.value = false
            }
        }
    }

    Scaffold(
        topBar = {
            ChartAppBar(
                trackerWithRecords,
                onArrowClicked = { onAction(TrackerDetailWorkflow.Action.BackClicked) }
            )
        },
        floatingActionButtonPosition = Scaffold.FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAction(TrackerDetailWorkflow.Action.AddRecordClicked) },
                modifier = Modifier.offset(y = fabOffset)
            ) {
                Icon(Icons.Default.Add)
            }
        }
    ) {
        Column {
            if (isAddRecordDialogShowing) {
                UpdateTrackedValueDialog(
                    unit = trackerWithRecords.tracker.unit,
                    onCloseRequest = { onAction(TrackerDetailWorkflow.Action.TrackDialogDismissed) },
                    onSave = { onAction(TrackerDetailWorkflow.Action.NewRecordSubmitted(it)) }
                )
            }
            RecordsList(trackerWithRecords, dragObserver)
        }
    }
}

@Composable
private fun RecordsList(trackerWithRecords: TrackerWithRecords, dragObserver: DragObserver) {
    val items = trackerWithRecords.records.reversed()
    LazyColumnItems(items = items, modifier = Modifier.dragGestureFilter(dragObserver), itemContent = {
        RecordListItem(
            trackerWithRecords,
            it,
            Modifier.padding(16.dp)
        )
        if (items.lastOrNull() != it) {
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
        } else {
            Spacer(modifier = Modifier.height(WindowInsetsAmbient.current.bottom))
        }
    })
}