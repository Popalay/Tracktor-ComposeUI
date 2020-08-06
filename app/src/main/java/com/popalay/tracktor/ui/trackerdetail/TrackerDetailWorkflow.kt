package com.popalay.tracktor.ui.trackerdetail

import com.popalay.tracktor.data.TrackingRepository
import com.popalay.tracktor.domain.worker.GetTrackerByIdWorker
import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.model.ValueRecord
import com.squareup.workflow.RenderContext
import com.squareup.workflow.Snapshot
import com.squareup.workflow.StatefulWorkflow
import com.squareup.workflow.Worker
import com.squareup.workflow.WorkflowAction
import com.squareup.workflow.applyTo

class TrackerDetailWorkflow(
    private val trackingRepository: TrackingRepository
) : StatefulWorkflow<TrackerDetailWorkflow.Props, TrackerDetailWorkflow.State, TrackerDetailWorkflow.Output, TrackerDetailWorkflow.Rendering>() {
    companion object {
        private const val DELETING_UNDO_TIMEOUT_MILLIS = 2500L
    }

    data class Props(val trackerId: String)

    data class State(
        @Transient val trackerWithRecords: TrackerWithRecords? = null,
        val isAddRecordDialogShowing: Boolean = false,
        @Transient val recordInDeleting: ValueRecord? = null,
        @Transient val currentAction: Action? = null
    )

    sealed class Output {
        object Back : Output()
        data class TrackerDeleted(val item: TrackerWithRecords) : Output()
    }

    sealed class Action : WorkflowAction<State, Output> {
        data class SideEffectAction(val action: Action) : Action()
        data class TrackerUpdated(val trackerWithRecords: TrackerWithRecords) : Action()
        data class DeleteSubmitted(val trackerWithRecords: TrackerWithRecords) : Action()
        data class DeleteRecordSubmitted(val record: ValueRecord) : Action()
        data class NewRecordSubmitted(val value: String) : Action()
        object DeleteLastRecordClicked : Action()
        object DeleteTrackerClicked : Action()
        object CloseScreen : Action()
        object AddRecordClicked : Action()
        object TrackDialogDismissed : Action()
        object UndoAvailabilityEnded : Action()
        object UndoDeletingClicked : Action()
        object UndoPerformed : Action()

        override fun WorkflowAction.Updater<State, Output>.apply() {
            nextState = when (val action = this@Action) {
                is SideEffectAction -> action.action.applyTo(nextState.copy(currentAction = null)).let { result ->
                    result.second?.also { setOutput(it) }
                    result.first
                }
                is TrackerUpdated -> nextState.copy(trackerWithRecords = action.trackerWithRecords)
                is DeleteSubmitted -> nextState.also { setOutput(Output.TrackerDeleted(action.trackerWithRecords)) }
                is DeleteRecordSubmitted -> nextState.copy(recordInDeleting = action.record)
                CloseScreen -> nextState.also { setOutput(Output.Back) }
                AddRecordClicked -> nextState.copy(isAddRecordDialogShowing = true)
                TrackDialogDismissed -> nextState.copy(isAddRecordDialogShowing = false)
                UndoAvailabilityEnded -> nextState.copy(recordInDeleting = null)
                UndoPerformed -> nextState.copy(recordInDeleting = null)
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
        context: RenderContext<State, Output>
    ): Rendering {
        context.runningWorker(GetTrackerByIdWorker(props.trackerId, trackingRepository)) { Action.TrackerUpdated(it) }
        runSideEffects(state, context)

        return Rendering(
            state = state,
            onAction = { context.actionSink.send(it) }
        )
    }

    override fun snapshotState(state: State): Snapshot = Snapshot.EMPTY

    private fun runSideEffects(state: State, context: RenderContext<State, Output>) {
        when (val action = state.currentAction) {
            is Action.NewRecordSubmitted -> {
                val tracker = requireNotNull(state.trackerWithRecords).tracker
                val worker = if (tracker.compatibleUnit == TrackableUnit.Word) {
                    Worker.from { trackingRepository.saveRecord(tracker, action.value) }
                } else {
                    Worker.from { trackingRepository.saveRecord(tracker, action.value.toDoubleOrNull() ?: 0.0) }
                }
                context.runningWorker(worker) { Action.SideEffectAction(Action.TrackDialogDismissed) }
            }
            is Action.DeleteTrackerClicked -> {
                state.trackerWithRecords?.tracker?.let {
                    val worker = Worker.from { trackingRepository.deleteTracker(it) }
                    context.runningWorker(worker) { Action.SideEffectAction(Action.DeleteSubmitted(state.trackerWithRecords)) }
                }
            }
            is Action.DeleteLastRecordClicked -> {
                state.trackerWithRecords?.let {
                    val lastRecord = it.records.last()
                    val worker = Worker.from { trackingRepository.deleteRecord(lastRecord) }
                    context.runningWorker(worker) { Action.SideEffectAction(Action.DeleteRecordSubmitted(lastRecord)) }
                }
            }
            is Action.UndoDeletingClicked -> {
                state.recordInDeleting?.let {
                    val worker = Worker.from { trackingRepository.restoreRecord(it) }
                    context.runningWorker(worker) { Action.SideEffectAction(Action.UndoPerformed) }
                }
            }
        }
        if (state.recordInDeleting != null) {
            val undoTimerWorker = Worker.timer(DELETING_UNDO_TIMEOUT_MILLIS)
            context.runningWorker(undoTimerWorker) { Action.SideEffectAction(Action.UndoAvailabilityEnded) }
        }
    }
}