package com.popalay.tracktor.ui.trackerdetail

import com.popalay.tracktor.data.TrackingRepository
import com.popalay.tracktor.domain.worker.GetTrackerByIdWorker
import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.model.TrackerWithRecords
import com.squareup.workflow.RenderContext
import com.squareup.workflow.Snapshot
import com.squareup.workflow.StatefulWorkflow
import com.squareup.workflow.Worker
import com.squareup.workflow.WorkflowAction
import com.squareup.workflow.applyTo

class TrackerDetailWorkflow(
    private val trackingRepository: TrackingRepository
) : StatefulWorkflow<TrackerDetailWorkflow.Props, TrackerDetailWorkflow.State, Unit, TrackerDetailWorkflow.Rendering>() {

    data class Props(val trackerId: String)

    data class State(
        val trackerWithRecords: TrackerWithRecords? = null,
        val isAddRecordDialogShowing: Boolean = false,
        val currentAction: Action? = null
    )

    sealed class Action : WorkflowAction<State, Unit> {
        data class SideEffectAction(val action: Action) : Action()
        data class TrackerUpdated(val tracker: TrackerWithRecords) : Action()
        data class NewRecordSubmitted(val value: String) : Action()
        object BackClicked : Action()
        object AddRecordClicked : Action()
        object TrackDialogDismissed : Action()

        override fun WorkflowAction.Updater<State, Unit>.apply() {
            nextState = when (val action = this@Action) {
                is SideEffectAction -> action.action.applyTo(nextState.copy(currentAction = null)).first
                is TrackerUpdated -> nextState.copy(trackerWithRecords = action.tracker)
                BackClicked -> nextState.also { setOutput(Unit) }
                AddRecordClicked -> nextState.copy(isAddRecordDialogShowing = true)
                TrackDialogDismissed -> nextState.copy(isAddRecordDialogShowing = false)
                else -> nextState.copy(currentAction = this@Action)
            }
        }
    }

    data class Rendering(
        val state: State,
        val onAction: (Action) -> Unit
    )

    override fun initialState(props: Props, snapshot: Snapshot?): State = State()

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
        when (val action = state.currentAction) {
            is Action.NewRecordSubmitted -> {
                val tracker = requireNotNull(state.trackerWithRecords).tracker
                val worker = if (tracker.unit == TrackableUnit.Word) {
                    Worker.from { trackingRepository.saveRecord(tracker, action.value) }
                } else {
                    Worker.from { trackingRepository.saveRecord(tracker, action.value.toDoubleOrNull() ?: 0.0) }
                }
                context.runningWorker(worker) { Action.SideEffectAction(Action.TrackDialogDismissed) }
            }
        }
    }
}