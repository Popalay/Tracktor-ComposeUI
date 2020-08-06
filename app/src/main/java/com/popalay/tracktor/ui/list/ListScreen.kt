package com.popalay.tracktor.ui.list

import androidx.compose.Composable
import androidx.compose.onActive
import androidx.compose.state
import androidx.ui.animation.animate
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.layout.Column
import androidx.ui.layout.ExperimentalLayout
import androidx.ui.layout.InnerPadding
import androidx.ui.layout.Spacer
import androidx.ui.layout.Stack
import androidx.ui.layout.height
import androidx.ui.layout.offset
import androidx.ui.material.FloatingActionButton
import androidx.ui.material.Scaffold
import androidx.ui.material.Snackbar
import androidx.ui.material.TextButton
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Add
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import androidx.ui.tooling.preview.PreviewParameterProvider
import androidx.ui.unit.dp
import com.popalay.tracktor.WindowInsetsAmbient
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.model.toListItem
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
    Scaffold(
        topBar = {
            CreateTrackerAppBar(
                menuItems = state.menuItems,
                onMenuItemClicked = { onAction(Action.MenuItemClicked(it)) }
            )
        },
        floatingActionButtonPosition = Scaffold.FabPosition.Center,
        floatingActionButton = {
            Column(horizontalGravity = Alignment.CenterHorizontally) {
                FloatingActionButton(
                    onClick = { onAction(Action.CreateTrackerClicked) },
                    modifier = Modifier.offset(y = -WindowInsetsAmbient.current.bottom)
                ) {
                    Icon(Icons.Default.Add)
                }
                UndoDeletingSnackbar(state.itemInDeleting, onAction)
            }
        }
    ) {
        Stack {
            when {
                state.itemInEditing != null -> {
                    UpdateTrackedValueDialog(
                        unit = state.itemInEditing.compatibleUnit,
                        onCloseRequest = { onAction(Action.TrackDialogDismissed) },
                        onSave = { onAction(Action.NewRecordSubmitted(state.itemInEditing, it)) }
                    )
                }
            }
            TrackerList(state, onAction)
        }
    }
}

@Composable
private fun UndoDeletingSnackbar(itemInDeleting: TrackerWithRecords?, onAction: (Action) -> Unit) {
    val snackbarVisibility = state { true }
    val snackbarOffset = animate(if (itemInDeleting == null) 72.dp else 0.dp) {
        snackbarVisibility.value = itemInDeleting != null
    }
    if (snackbarVisibility.value) {
        Snackbar(
            text = { Text(text = itemInDeleting?.tracker?.title?.let { "$it was removed" } ?: "") },
            action = {
                TextButton(onClick = { onAction(Action.UndoDeletingClicked) }) {
                    Text(text = "UNDO")
                }
            },
            modifier = Modifier.offset(y = snackbarOffset)
        )
    }
}

@Composable
private fun TrackerList(
    state: ListWorkflow.State,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier
) {
    val insets = WindowInsetsAmbient.current
    LazyColumnItems(
        items = state.items, modifier,
        contentPadding = InnerPadding(16.dp).copy(bottom = insets.bottom + 16.dp)
    ) {
        TrackerListItem(
            it.copy(animate = state.animate),
            modifier = Modifier.clickable(onClick = { onAction(Action.TrackerClicked(it.data)) }),
            onAddClicked = { onAction(Action.AddRecordClicked(it.data)) },
            onRemoveClicked = { onAction(Action.DeleteTrackerClicked(it.data)) }
        )
        if (it != state.items.last()) {
            Spacer(modifier = Modifier.height(8.dp))
        }
        onActive {
            onAction(Action.AnimationProceeded)
        }
    }
}