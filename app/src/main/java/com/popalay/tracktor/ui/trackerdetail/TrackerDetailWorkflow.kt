package com.popalay.tracktor.ui.trackerdetail

import com.popalay.tracktor.data.TrackingRepository
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.worker.GetTrackerByIdWorker
import com.squareup.workflow.RenderContext
import com.squareup.workflow.Snapshot
import com.squareup.workflow.StatefulWorkflow
import com.squareup.workflow.Worker
import com.squareup.workflow.WorkflowAction

class TrackerDetailWorkflow(
    private val trackingRepository: TrackingRepository
) : StatefulWorkflow<TrackerDetailWorkflow.Props, TrackerDetailWorkflow.State, Unit, TrackerDetailWorkflow.Rendering>() {

    data class Props(val trackerId: String)

    data class State(
        val trackerWithRecords: TrackerWithRecords?,
        val isAddRecordDialogShowing: Pair<Boolean, Double>?
    )

    sealed class Action : WorkflowAction<State, Unit> {
        data class TrackerUpdated(val tracker: TrackerWithRecords) : Action()
        data class NewRecordSubmitted(val value: Double) : Action()
        object BackClicked : Action()
        object AddRecordClicked : Action()
        object TrackDialogDismissed : Action()

        override fun WorkflowAction.Updater<State, Unit>.apply() {
            nextState = when (val action = this@Action) {
                is TrackerUpdated -> nextState.copy(trackerWithRecords = action.tracker)
                is NewRecordSubmitted -> nextState.copy(isAddRecordDialogShowing = true to action.value)
                BackClicked -> nextState.also { setOutput(Unit) }
                AddRecordClicked -> nextState.copy(isAddRecordDialogShowing = false to 0.0)
                TrackDialogDismissed -> nextState.copy(isAddRecordDialogShowing = null)
            }
        }
    }

    data class Rendering(
        val state: State,
        val onAction: (Action) -> Unit
    )

    override fun initialState(props: Props, snapshot: Snapshot?): State =
        State(
            trackerWithRecords = null,
            isAddRecordDialogShowing = null
        )

    override fun render(
        props: Props,
        state: State,
        context: RenderContext<State, Unit>
    ): Rendering {
        context.runningWorker(GetTrackerByIdWorker(props.trackerId, trackingRepository)) { Action.TrackerUpdated(it) }
        runSideEffects(state, context)

        return Rendering(
            state = state,
            onAction = { context.actionSink.send(it) }
        )
    }

    override fun snapshotState(state: State): Snapshot = Snapshot.EMPTY

    private fun runSideEffects(state: State, context: RenderContext<State, Unit>) {
        if (state.isAddRecordDialogShowing?.first == true && state.trackerWithRecords != null) {
            val worker = Worker.from { trackingRepository.saveRecord(state.trackerWithRecords.tracker, state.isAddRecordDialogShowing.second) }
            context.runningWorker(worker) { Action.TrackDialogDismissed }
        }
    }
}