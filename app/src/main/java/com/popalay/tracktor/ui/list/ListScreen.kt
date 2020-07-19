package com.popalay.tracktor.ui.list

import androidx.compose.Composable
import androidx.compose.onCommit
import androidx.compose.state
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
import androidx.ui.tooling.preview.PreviewParameter
import androidx.ui.tooling.preview.PreviewParameterProvider
import androidx.ui.unit.dp
import com.popalay.tracktor.model.TrackerListItem
import com.popalay.tracktor.model.toListItem
import com.popalay.tracktor.ui.dialog.ChooseUnitDialog
import com.popalay.tracktor.ui.dialog.DeleteTrackerDialog
import com.popalay.tracktor.ui.dialog.UpdateTrackedValueDialog
import com.popalay.tracktor.ui.list.ListWorkflow.Action
import com.popalay.tracktor.utils.Faker
import com.squareup.workflow.ui.compose.composedViewFactory

@OptIn(ExperimentalLayout::class)
val ListBinding = composedViewFactory<ListWorkflow.Rendering> { rendering, _ ->
    ListScreen(rendering.state, rendering.onAction)
}

class ListStatePreviewProvider : PreviewParameterProvider<ListWorkflow.State> {
    override val values: Sequence<ListWorkflow.State>
        get() = sequenceOf(ListWorkflow.State(List(5) { Faker.fakeTrackerWithRecords() }.map { it.toListItem() }))
}

@Preview
@Composable
fun ListScreen(
    @PreviewParameter(ListStatePreviewProvider::class) state: ListWorkflow.State,
    onAction: (Action) -> Unit = {}
) {
    Scaffold(topBar = {
        CreateTrackerAppBar(
            menuItems = state.menuItems,
            onMenuItemClicked = { onAction(Action.MenuItemClicked(it)) },
            onSubmit = { onAction(Action.NewTrackerTitleSubmitted(it)) }
        )
    }) {
        Column {
            when {
                state.itemInEditing != null -> {
                    UpdateTrackedValueDialog(
                        onCloseRequest = { onAction(Action.TrackDialogDismissed) },
                        onSave = { onAction(Action.NewRecordSubmitted(state.itemInEditing, it)) }
                    )
                }
                state.itemInDeleting != null -> {
                    DeleteTrackerDialog(
                        onCloseRequest = { onAction(Action.DeleteDialogDismissed) },
                        onSubmit = { onAction(Action.DeleteSubmitted(state.itemInDeleting)) }
                    )
                }
                state.itemInCreating != null -> {
                    ChooseUnitDialog(
                        onCloseRequest = { onAction(Action.ChooseUnitDialogDismissed) },
                        onSubmit = { onAction(Action.UnitSubmitted(it)) }
                    )
                }
            }
            TrackerList(state.items, onAction)
        }
    }
}

@Composable
private fun TrackerList(
    items: List<TrackerListItem>,
    onAction: (Action) -> Unit
) {
    val animated = state { false }

    LazyColumnItems(items = items, itemContent = {
        Spacer(modifier = Modifier.height(8.dp))
        TrackerListItem(
            it.copy(animate = !animated.value),
            Modifier.padding(horizontal = 16.dp).clickable(onClick = { onAction(Action.TrackerClicked(it.data)) }),
            onAddClicked = { onAction(Action.AddRecordClicked(it.data)) },
            onRemoveClicked = { onAction(Action.DeleteTrackerClicked(it.data)) }
        )
        if (items.lastOrNull() == it) {
            Spacer(modifier = Modifier.height(8.dp))
        }
        onCommit {
            animated.value = true
        }
    })
}