package com.popalay.tracktor.ui.trackerdetail

import androidx.compose.Composable
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import androidx.ui.tooling.preview.PreviewParameterProvider
import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.model.ValueRecord
import com.popalay.tracktor.ui.trackerdetail.TrackerDetailWorkflow.Action
import com.popalay.tracktor.utils.onBackPressed
import com.squareup.workflow.ui.compose.composedViewFactory
import java.time.LocalDateTime

val TrackerDetailBinding = composedViewFactory<TrackerDetailWorkflow.Rendering> { rendering, _ ->
    onBackPressed { rendering.onAction(Action.BackClicked) }
    TrackerDetailScreen(rendering.state, rendering.onAction)
}

class TrackerDetailStatePreviewProvider : PreviewParameterProvider<TrackerDetailWorkflow.State> {
    override val values: Sequence<TrackerDetailWorkflow.State>
        get() {
            val records = listOf(
                ValueRecord("valueId", "trackerId", 42.3, LocalDateTime.now()),
                ValueRecord("valueId", "trackerId", 12.3, LocalDateTime.now()),
                ValueRecord("valueId", "trackerId", 62.3, LocalDateTime.now()),
                ValueRecord("valueId", "trackerId", 2.3, LocalDateTime.now())
            )
            val tracker = TrackerWithRecords(Tracker("id", "title", TrackableUnit.Kilograms, LocalDateTime.now()), records)

            return sequenceOf(
                TrackerDetailWorkflow.State(null, null),
                TrackerDetailWorkflow.State(tracker, true to 0.0)
            )
        }
}

@Preview
@Composable
fun TrackerDetailScreen(
    @PreviewParameter(TrackerDetailStatePreviewProvider::class) state: TrackerDetailWorkflow.State,
    onAction: (Action) -> Unit = {}
) {
    if (state.trackerWithRecords == null) {
        TrackerDetailLoadingView(onArrowClicked = { onAction(Action.BackClicked) })
    } else {
        TrackerDetailContentView(
            state.trackerWithRecords,
            isAddRecordDialogShowing = state.isAddRecordDialogShowing != null,
            onAction = onAction
        )
    }
}