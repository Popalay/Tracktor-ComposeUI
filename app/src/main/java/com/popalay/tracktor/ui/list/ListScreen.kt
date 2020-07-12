package com.popalay.tracktor.ui.list

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.clickable
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.layout.Column
import androidx.ui.layout.ExperimentalLayout
import androidx.ui.layout.Spacer
import androidx.ui.layout.height
import androidx.ui.layout.padding
import androidx.ui.material.Scaffold
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.popalay.tracktor.AppTheme
import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.ui.dialog.ChooseUnitDialog
import com.popalay.tracktor.ui.dialog.DeleteTrackerDialog
import com.popalay.tracktor.ui.dialog.UpdateTrackedValueDialog
import com.popalay.tracktor.ui.list.ListWorkflow.Action
import com.squareup.workflow.ui.compose.composedViewFactory
import com.squareup.workflow.ui.compose.tooling.preview
import java.time.LocalDateTime

@OptIn(ExperimentalLayout::class)
val ListBinding = composedViewFactory<ListWorkflow.Rendering> { rendering, _ ->
    Scaffold(topBar = { CreateTrackerAppBar(onSubmit = { rendering.onAction(Action.NewTrackerTitleSubmitted(it)) }) }) {
        Column {
            when {
                rendering.state.itemInEditing != null -> {
                    UpdateTrackedValueDialog(
                        onCloseRequest = { rendering.onAction(Action.TrackDialogDismissed) },
                        onSave = { rendering.onAction(Action.NewRecordSubmitted(rendering.state.itemInEditing.input, it)) }
                    )
                }
                rendering.state.itemInDeleting != null -> {
                    DeleteTrackerDialog(
                        onCloseRequest = { rendering.onAction(Action.DeleteDialogDismissed) },
                        onSubmit = { rendering.onAction(Action.DeleteSubmitted(rendering.state.itemInDeleting.input)) }
                    )
                }
                rendering.state.itemInCreating != null -> {
                    ChooseUnitDialog(
                        onCloseRequest = { rendering.onAction(Action.ChooseUnitDialogDismissed) },
                        onSubmit = { rendering.onAction(Action.UnitSubmitted(it)) }
                    )
                }
            }
            TrackerList(rendering.state.items, rendering.onAction)
        }
    }
}

@Composable
private fun TrackerList(
    items: List<TrackerListItem>,
    onAction: (Action) -> Unit
) {
    LazyColumnItems(items = items, itemContent = {
        Spacer(modifier = Modifier.height(8.dp))
        TrackerListItem(
            it,
            Modifier.padding(horizontal = 16.dp).clickable(onClick = { onAction(Action.TrackerClicked(it.data)) }),
            onAddClicked = { onAction(Action.AddRecordClicked(it.data)) },
            onRemoveClicked = { onAction(Action.DeleteTrackerClicked(it.data)) }
        )
        if (items.lastOrNull() == it) {
            Spacer(modifier = Modifier.height(8.dp))
        }
    })
}

@Preview
@Composable
fun ListScreenPreview() {
    val items = listOf(
        TrackerWithRecords(Tracker("id", "title", TrackableUnit.Kilograms, LocalDateTime.now()), emptyList()),
        TrackerWithRecords(Tracker("id", "title", TrackableUnit.Kilograms, LocalDateTime.now()), emptyList()),
        TrackerWithRecords(Tracker("id", "title", TrackableUnit.Kilograms, LocalDateTime.now()), emptyList())
    ).map { it.toListItem() }
    AppTheme(isDarkTheme = true) {
        ListBinding.preview(rendering = ListWorkflow.Rendering(ListWorkflow.State(items, null, null, null, null)) {})
    }
}