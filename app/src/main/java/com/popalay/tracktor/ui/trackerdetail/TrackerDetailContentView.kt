package com.popalay.tracktor.ui.trackerdetail

import androidx.compose.foundation.Icon
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.InnerPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    Scaffold(
        topBar = {
            ChartAppBar(
                trackerWithRecords,
                onArrowClicked = { onAction(TrackerDetailWorkflow.Action.CloseScreen) },
                onUndoClicked = { onAction(TrackerDetailWorkflow.Action.RemoveLastRecordClicked) },
                onDeleteClicked = { onAction(TrackerDetailWorkflow.Action.DeleteTrackerClicked) }
            )
        },
        floatingActionButtonPosition = Scaffold.FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAction(TrackerDetailWorkflow.Action.AddRecordClicked) },
                modifier = Modifier.offset(y = -insets.bottom)
            ) {
                Icon(Icons.Default.Add)
            }
        }
    ) {
        Column {
            if (isAddRecordDialogShowing) {
                UpdateTrackedValueDialog(
                    unit = trackerWithRecords.tracker.compatibleUnit,
                    onCloseRequest = { onAction(TrackerDetailWorkflow.Action.TrackDialogDismissed) },
                    onSave = { onAction(TrackerDetailWorkflow.Action.NewRecordSubmitted(it)) }
                )
            }
            RecordsList(trackerWithRecords)
        }
    }
}

@Composable
private fun RecordsList(trackerWithRecords: TrackerWithRecords) {
    val items = trackerWithRecords.records.reversed()
    val insets = WindowInsetsAmbient.current
    LazyColumnForIndexed(
        items = items,
        contentPadding = InnerPadding(16.dp).copy(bottom = insets.bottom + 16.dp)
    ) { index, item ->
        RecordListItem(trackerWithRecords, item)
        if (items.lastIndex != index) {
            Divider(modifier = Modifier.padding(vertical = 16.dp))
        }
    }
}