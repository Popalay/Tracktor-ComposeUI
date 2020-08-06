package com.popalay.tracktor.ui.list

import androidx.compose.animation.animate
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.InnerPadding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onActive
import androidx.compose.runtime.state
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import androidx.ui.tooling.preview.PreviewParameterProvider
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
    LazyColumnForIndexed(
        items = state.items, modifier,
        contentPadding = InnerPadding(16.dp).copy(bottom = insets.bottom + 16.dp)
    ) { index, item ->
        TrackerListItem(
            item.copy(animate = state.animate),
            modifier = Modifier.clickable(onClick = { onAction(Action.TrackerClicked(item.data)) }),
            onAddClicked = { onAction(Action.AddRecordClicked(item.data)) },
            onRemoveClicked = { onAction(Action.DeleteTrackerClicked(item.data)) }
        )
        if (index != state.items.lastIndex) {
            Spacer(modifier = Modifier.height(8.dp))
        }
        onActive {
            onAction(Action.AnimationProceeded)
        }
    }
}