package com.popalay.tracktor.feature.trackerdetail

import androidx.compose.runtime.Composable
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import androidx.ui.tooling.preview.PreviewParameterProvider
import com.popalay.tracktor.feature.trackerdetail.TrackerDetailWorkflow.Action
import com.popalay.tracktor.utils.Faker
import com.popalay.tracktor.utils.onBackPressed
import com.squareup.workflow.ui.compose.composedViewFactory

val TrackerDetailBinding = composedViewFactory<TrackerDetailWorkflow.Rendering> { rendering, _ ->
    onBackPressed { rendering.onAction(Action.CloseScreen) }
    TrackerDetailScreen(rendering.state, rendering.onAction)
}

class TrackerDetailStatePreviewProvider : PreviewParameterProvider<TrackerDetailWorkflow.State> {
    override val values: Sequence<TrackerDetailWorkflow.State>
        get() = sequenceOf(
            TrackerDetailWorkflow.State(isAddRecordDialogShowing = false),
            TrackerDetailWorkflow.State(isAddRecordDialogShowing = true, trackerWithRecords = Faker.fakeTrackerWithRecords())
        )
}

@Preview
@Composable
fun TrackerDetailScreen(
    @PreviewParameter(TrackerDetailStatePreviewProvider::class) state: TrackerDetailWorkflow.State,
    onAction: (Action) -> Unit = {}
) {
    if (state.trackerWithRecords == null) {
        TrackerDetailLoadingView(onArrowClicked = { onAction(Action.CloseScreen) })
    } else {
        TrackerDetailContentView(state, onAction)
    }
}