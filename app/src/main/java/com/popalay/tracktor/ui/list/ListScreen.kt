package com.popalay.tracktor.ui.list

import androidx.ui.core.Modifier
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.height
import androidx.ui.layout.padding
import androidx.ui.material.Scaffold
import androidx.ui.material.TopAppBar
import androidx.ui.unit.dp
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.ui.create.CreateTrackedValue
import com.squareup.workflow.ui.compose.composedViewFactory

val ListBinding = composedViewFactory<ListWorkflow.Rendering> { rendering, _ ->
    Scaffold(
        topAppBar = {
            TopAppBar(
                title = { Text("Tracktor") }
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            if (rendering.state.itemInEditing != null) {
                UpdateTrackedValueDialog(
                    onCloseRequest = { rendering.onEvent(ListWorkflow.Event.TrackDialogDismissed) },
                    onSave = { rendering.onEvent(ListWorkflow.Event.NewRecordSubmitted(rendering.state.itemInEditing.tracker, it)) }
                )
            } else if (rendering.state.itemInDeleting != null) {
                DeleteTrackerDialog(
                    onCloseRequest = { rendering.onEvent(ListWorkflow.Event.DeleteDialogDismissed) },
                    onSubmit = { rendering.onEvent(ListWorkflow.Event.DeleteSubmitted(rendering.state.itemInDeleting)) }
                )
            }
            AdapterList(data = rendering.state.items) {
                if (it is TrackerWithRecords) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TrackedValueValueListItem(
                        it,
                        onClick = { rendering.onEvent(ListWorkflow.Event.ItemClicked(it)) },
                        onLongClick = { rendering.onEvent(ListWorkflow.Event.ItemLongClicked(it)) }
                    )
                } else {
                    CreateTrackedValue(rendering.onEvent)
                }
            }
        }
    }
}