package com.popalay.tracktor.feature.trackerdetail

import androidx.compose.foundation.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import com.popalay.tracktor.core.R
import com.popalay.tracktor.domain.formatter.ValueRecordFormatter
import com.popalay.tracktor.feature.trackerdetail.TrackerDetailWorkflow.Action
import com.popalay.tracktor.feature.trackerdetail.TrackerDetailWorkflow.State
import com.popalay.tracktor.ui.dialog.AddNewRecordDialog
import com.popalay.tracktor.ui.widget.AnimatedSnackbar
import com.popalay.tracktor.ui.widget.TrackerCategoryList
import com.popalay.tracktor.utils.inject
import com.popalay.tracktor.utils.navigationBarsPadding

@Composable
fun TrackerDetailContentView(
    state: State,
    onAction: (Action) -> Unit
) {
    Scaffold(
        topBar = {
            TrackerDetailsAppBar(
                requireNotNull(state.trackerWithRecords),
                onArrowClicked = { onAction(Action.CloseScreen) },
                onUndoClicked = { onAction(Action.DeleteLastRecordClicked) },
                onDeleteClicked = { onAction(Action.DeleteTrackerClicked) }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            val formatter: ValueRecordFormatter by inject()

            Column(horizontalGravity = Alignment.CenterHorizontally) {
                val message = state.trackerWithRecords?.tracker?.let { formatter.format(it, state.recordInDeleting) }.orEmpty()
                AnimatedSnackbar(
                    message = message,
                    actionText = stringResource(R.string.button_undo),
                    shouldDisplay = state.recordInDeleting != null,
                    onActionClick = { onAction(Action.UndoDeletingClicked) },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                FloatingActionButton(
                    onClick = { onAction(Action.AddRecordClicked) },
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Icon(Icons.Default.Add)
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.background(MaterialTheme.colors.background)
        ) {
            if (state.isAddRecordDialogShowing) {
                AddNewRecordDialog(
                    tracker = requireNotNull(state.trackerWithRecords).tracker,
                    onDismissRequest = { onAction(Action.TrackDialogDismissed) },
                    onSave = { onAction(Action.NewRecordSubmitted(it)) }
                )
            }
            ChartCard(requireNotNull(state.trackerWithRecords), modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            TrackerCategoryList(
                categories = state.trackerWithRecords.categories,
                availableCategories = state.allCategories,
                isAddCategoryDialogShowing = state.isAddCategoryDialogShowing,
                animate = state.animate,
                onSave = { onAction(Action.TrackerCategoriesUpdated(it)) },
                onAddCategoryClicked = { onAction(Action.AddCategoryClicked) },
                onDialogDismissed = { onAction(Action.AddCategoryDialogDismissed) },
                modifier = Modifier.padding(bottom = 8.dp)
            )
            RecordsList(requireNotNull(state.trackerWithRecords), animate = state.animate)

            onActive {
                onAction(Action.AnimationProceeded)
            }
        }
    }
}