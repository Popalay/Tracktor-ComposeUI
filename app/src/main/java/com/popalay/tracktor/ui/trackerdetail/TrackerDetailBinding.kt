package com.popalay.tracktor.ui.trackerdetail

import com.popalay.tracktor.ui.trackerdetail.TrackerDetailWorkflow.Action
import com.popalay.tracktor.utils.onBackPressed
import com.squareup.workflow.ui.compose.composedViewFactory

val TrackerDetailBinding = composedViewFactory<TrackerDetailWorkflow.Rendering> { rendering, _ ->
    onBackPressed {
        rendering.onAction(Action.BackClicked)
    }

    if (rendering.state.trackerWithRecords == null) {
        TrackerDetailLoadingView(onArrowClicked = { rendering.onAction(Action.BackClicked) })
    } else {
        TrackerDetailContentView(
            rendering.state.trackerWithRecords,
            isAddRecordDialogShowing = rendering.state.isAddRecordDialogShowing != null,
            onAction = rendering.onAction
        )
    }
}