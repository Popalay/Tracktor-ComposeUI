package com.popalay.tracktor.feature.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FabPosition
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
import com.popalay.tracktor.data.model.Category
import com.popalay.tracktor.data.model.Statistic
import com.popalay.tracktor.data.model.toListItem
import com.popalay.tracktor.domain.workflow.ListWorkflow.Action
import com.popalay.tracktor.domain.workflow.ListWorkflow.Rendering
import com.popalay.tracktor.ui.dialog.AddNewRecordDialog
import com.popalay.tracktor.ui.widget.AllCategoryList
import com.popalay.tracktor.ui.widget.AnimatedSnackbar
import com.popalay.tracktor.ui.widget.SwipeToDismissListItem
import com.popalay.tracktor.utils.Faker
import com.popalay.tracktor.utils.navigationBarsHeight
import com.popalay.tracktor.utils.navigationBarsPadding
import com.squareup.workflow.ui.compose.composedViewFactory

@OptIn(ExperimentalLayout::class)
val ListBinding = composedViewFactory<Rendering> { rendering, _ ->
    ListScreen(rendering)
}

class ListRenderingPreviewProvider : PreviewParameterProvider<Rendering> {
    override val values: Sequence<Rendering>
        get() = sequenceOf(
            Rendering(
                items = List(5) { Faker.fakeTrackerWithRecords() }.map { it.toListItem() },
                filteredItems = List(5) { Faker.fakeTrackerWithRecords() }.map { it.toListItem() },
                statistic = Statistic.generateFor(List(5) { Faker.fakeTrackerWithRecords() }),
                allCategories = Category.defaultList(),
                selectedCategory = Category.All,
                itemInDeleting = null,
                itemInEditing = null,
                showEmptyState = false,
                animate = false,
                onAction = {}
            )
        )
}

@Preview
@Composable
fun ListScreen(
    @PreviewParameter(ListRenderingPreviewProvider::class) rendering: Rendering,
) {
    Scaffold(
        topBar = { LogoAppBar(onActionClick = { rendering.onAction(Action.SettingsClicked) }) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            Column(horizontalGravity = Alignment.CenterHorizontally) {
                AnimatedSnackbar(
                    message = rendering.itemInDeleting?.title?.let {
                        stringResource(R.string.tracker_item_removed_message, it)
                    }.orEmpty(),
                    actionText = stringResource(R.string.button_undo),
                    shouldDisplay = rendering.itemInDeleting != null,
                    onActionClick = { rendering.onAction(Action.UndoDeletingClicked) },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                FloatingActionButton(
                    onClick = { rendering.onAction(Action.CreateTrackerClicked) },
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Icon(Icons.Default.Add)
                }
            }
        }
    ) {
        Stack(modifier = Modifier.fillMaxSize()) {
            when {
                rendering.itemInEditing != null -> {
                    AddNewRecordDialog(
                        tracker = rendering.itemInEditing!!,
                        onDismissRequest = { rendering.onAction(Action.TrackDialogDismissed) },
                        onSave = { rendering.onAction(Action.NewRecordSubmitted(rendering.itemInEditing!!, it)) }
                    )
                }
            }
            if (rendering.showEmptyState) {
                Text(
                    text = stringResource(R.string.tracker_list_empty_message),
                    style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.gravity(Alignment.Center)
                )
            } else {
                TrackerList(rendering)
            }
        }
    }
}

@Composable
private fun TrackerList(
    rendering: Rendering,
    modifier: Modifier = Modifier
) {
    LazyColumnForIndexed(
        items = rendering.filteredItems,
        contentPadding = InnerPadding(top = 16.dp, bottom = 16.dp),
        modifier = modifier
    ) { index, item ->
        if (index == 0) {
            TrackerListHeader(rendering)
        }

        SwipeToDismissListItem(
            state = rendering.filteredItems,
            onDismissedToStart = { rendering.onAction(Action.DeleteTrackerClicked(item.data)) },
            onDismissedToEnd = { rendering.onAction(Action.AddRecordClicked(item.data)) }
        ) {
            TrackerListItem(
                item.copy(animate = rendering.animate),
                contentModifier = Modifier.clickable(onClick = { rendering.onAction(Action.TrackerClicked(item.data)) }),
            )
        }

        if (index != rendering.items.lastIndex) {
            Spacer(Modifier.height(8.dp))
        } else {
            Spacer(Modifier.navigationBarsHeight())
        }

        onActive {
            rendering.onAction(Action.AnimationProceeded)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun TrackerListHeader(
    rendering: Rendering
) {
    AnimatedVisibility(rendering.statistic != null) {
        StatisticWidget(
            requireNotNull(rendering.statistic),
            rendering.animate,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
    AnimatedVisibility(rendering.allCategories.isNotEmpty()) {
        AllCategoryList(
            categories = rendering.allCategories,
            selectedCategory = rendering.selectedCategory,
            onCategoryClick = { rendering.onAction(Action.CategoryClick(it)) },
            modifier = Modifier.padding(top = 8.dp)
        )
    }
    Spacer(Modifier.height(8.dp))
}