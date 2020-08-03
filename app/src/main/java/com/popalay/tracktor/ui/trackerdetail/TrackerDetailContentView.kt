package com.popalay.tracktor.ui.trackerdetail

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.layout.Column
import androidx.ui.layout.InnerPadding
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
    LazyColumnItems(
        items = items,
        contentPadding = InnerPadding(16.dp).copy(bottom = insets.bottom + 16.dp)
    ) {
        RecordListItem(trackerWithRecords, it)
        if (items.lastOrNull() != it) {
            Divider(modifier = Modifier.padding(vertical = 16.dp))
        }
    }
}