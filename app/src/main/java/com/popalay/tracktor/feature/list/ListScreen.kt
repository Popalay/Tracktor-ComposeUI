package com.popalay.tracktor.feature.list

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.InnerPadding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onActive
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import androidx.ui.tooling.preview.PreviewParameterProvider
import com.popalay.tracktor.core.R
import com.popalay.tracktor.data.model.toListItem
import com.popalay.tracktor.feature.list.ListWorkflow.Action
import com.popalay.tracktor.feature.list.ListWorkflow.Rendering
import com.popalay.tracktor.feature.list.ListWorkflow.State
import com.popalay.tracktor.ui.dialog.AddNewRecordDialog
import com.popalay.tracktor.ui.widget.AllCategoryList
import com.popalay.tracktor.ui.widget.AnimatedSnackbar
import com.popalay.tracktor.utils.Faker
import com.popalay.tracktor.utils.navigationBarHeight
import com.popalay.tracktor.utils.navigationBarPadding
import com.squareup.workflow.ui.compose.composedViewFactory

@OptIn(ExperimentalLayout::class)
val ListBinding = composedViewFactory<Rendering> { rendering, _ ->
    ListScreen(rendering.state, rendering.onAction)
}

class ListStatePreviewProvider : PreviewParameterProvider<State> {
    override val values: Sequence<State>
        get() = sequenceOf(State(List(5) { Faker.fakeTrackerWithRecords() }.map { it.toListItem() }))
}

@Preview
@Composable
fun ListScreen(
    @PreviewParameter(ListStatePreviewProvider::class) state: State,
    onAction: (Action) -> Unit = {}
) {
    Scaffold(
        topBar = { LogoAppBar(onActionClick = { onAction(Action.SettingsClicked) }) },
        floatingActionButtonPosition = Scaffold.FabPosition.Center,
        floatingActionButton = {
            Column(horizontalGravity = Alignment.CenterHorizontally) {
                FloatingActionButton(
                    onClick = { onAction(Action.CreateTrackerClicked) },
                    modifier = Modifier.navigationBarPadding()
                ) {
                    Icon(Icons.Default.Add)
                }
                AnimatedSnackbar(
                    message = state.itemInDeleting?.tracker?.title?.let { stringResource(R.string.tracker_item_removed_message, it) } ?: "",
                    actionText = stringResource(R.string.button_undo),
                    shouldDisplay = state.itemInDeleting != null,
                    onActionClick = { onAction(Action.UndoDeletingClicked) }
                )
            }
        }
    ) {
        Stack(modifier = Modifier.fillMaxSize()) {
            when {
                state.itemInEditing != null -> {
                    AddNewRecordDialog(
                        tracker = state.itemInEditing,
                        onCloseRequest = { onAction(Action.TrackDialogDismissed) },
                        onSave = { onAction(Action.NewRecordSubmitted(state.itemInEditing, it)) }
                    )
                }
            }
            if (state.showEmptyState) {
                Text(
                    text = stringResource(R.string.tracker_list_empty_message),
                    style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.gravity(Alignment.Center)
                )
            } else {
                TrackerList(state, onAction)
            }
        }
    }
}

@Composable
private fun TrackerList(
    state: State,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumnForIndexed(
        items = state.filteredItems,
        contentPadding = InnerPadding(top = 16.dp, bottom = 16.dp),
        modifier = modifier
    ) { index, item ->
        if (index == 0 && state.statistic != null) {
            StatisticWidget(
                state.statistic,
                state.animate,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            if (state.allCategories.isNotEmpty()) {
                AllCategoryList(
                    categories = state.allCategories,
                    selectedCategory = state.selectedCategory,
                    onCategoryClick = { onAction(Action.CategoryClick(it)) },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
        }
        TrackerListItem(
            item.copy(animate = state.animate),
            modifier = Modifier.padding(horizontal = 16.dp),
            contentModifier = Modifier.clickable(onClick = { onAction(Action.TrackerClicked(item.data)) }),
            onAddClicked = { onAction(Action.AddRecordClicked(item.data)) },
            onRemoveClicked = { onAction(Action.DeleteTrackerClicked(item.data)) }
        )
        if (index != state.items.lastIndex) {
            Spacer(Modifier.height(8.dp))
        } else {
            Spacer(Modifier.navigationBarHeight())
        }
        onActive {
            onAction(Action.AnimationProceeded)
        }
    }
}