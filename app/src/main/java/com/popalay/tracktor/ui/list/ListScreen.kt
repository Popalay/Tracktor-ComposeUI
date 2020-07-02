package com.popalay.tracktor.ui.list

import androidx.compose.Composable
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
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.popalay.tracktor.AppTheme
import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.ui.create.CreateTrackedValue
import com.squareup.workflow.ui.compose.composedViewFactory
import com.squareup.workflow.ui.compose.tooling.preview

val ListBinding = composedViewFactory<ListWorkflow.Rendering> { rendering, _ ->
    Scaffold(
        topAppBar = {
            Column {
                TopAppBar(title = { Text("Tracktor") })
                CreateTrackedValue(onSubmit = { rendering.onEvent(ListWorkflow.Event.NewTrackerTitleSubmitted(it)) })
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
        ) {
            when {
                rendering.state.itemInEditing != null -> {
                    UpdateTrackedValueDialog(
                        onCloseRequest = { rendering.onEvent(ListWorkflow.Event.TrackDialogDismissed) },
                        onSave = { rendering.onEvent(ListWorkflow.Event.NewRecordSubmitted(rendering.state.itemInEditing.tracker, it)) }
                    )
                }
                rendering.state.itemInDeleting != null -> {
                    DeleteTrackerDialog(
                        onCloseRequest = { rendering.onEvent(ListWorkflow.Event.DeleteDialogDismissed) },
                        onSubmit = { rendering.onEvent(ListWorkflow.Event.DeleteSubmitted(rendering.state.itemInDeleting)) }
                    )
                }
                rendering.state.itemInCreating != null -> {
                    ChooseUnitDialog(
                        onCloseRequest = { rendering.onEvent(ListWorkflow.Event.ChooseUnitDialogDismissed) },
                        onSubmit = { rendering.onEvent(ListWorkflow.Event.UnitSubmitted(it)) }
                    )
                }
            }
            AdapterList(data = rendering.state.items) {
                Spacer(modifier = Modifier.height(8.dp))
                TrackedValueValueListItem(
                    it,
                    onClick = { rendering.onEvent(ListWorkflow.Event.ItemClicked(it.data)) },
                    onLongClick = { rendering.onEvent(ListWorkflow.Event.ItemLongClicked(it.data)) }
                )
                if (rendering.state.items.lastOrNull() == it) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun ListScreenPreview() {
    val items = listOf(
        TrackerWithRecords(Tracker("id", "title", TrackableUnit.Kilograms), emptyList()),
        TrackerWithRecords(Tracker("id", "title", TrackableUnit.Kilograms), emptyList()),
        TrackerWithRecords(Tracker("id", "title", TrackableUnit.Kilograms), emptyList())
    ).map { it.toListItem() }
    AppTheme(isDarkTheme = true) {
        ListBinding.preview(rendering = ListWorkflow.Rendering(ListWorkflow.State(items, null, null, null)) {})
    }
}