package com.popalay.tracktor.ui.list

import com.popalay.tracktor.data.TrackingRepository
import com.popalay.tracktor.domain.worker.GetAllTrackersWorker
import com.popalay.tracktor.model.MenuItem
import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.model.TrackerListItem
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.model.toListItem
import com.popalay.tracktor.utils.toData
import com.popalay.tracktor.utils.toSnapshot
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.workflow.RenderContext
import com.squareup.workflow.Snapshot
import com.squareup.workflow.StatefulWorkflow
import com.squareup.workflow.Worker
import com.squareup.workflow.WorkflowAction
import com.squareup.workflow.applyTo
import org.koin.core.KoinComponent

class ListWorkflow(
    private val trackingRepository: TrackingRepository,
    private val getAllTrackersWorker: GetAllTrackersWorker,
    private val moshi: Moshi
) : StatefulWorkflow<ListWorkflow.Props, ListWorkflow.State, ListWorkflow.Output, Any>(), KoinComponent {

    data class Props(val animate: Boolean)

    @JsonClass(generateAdapter = true)
    data class State(
        @Transient val items: List<TrackerListItem> = emptyList(),
        @Transient val menuItems: List<MenuItem> = listOf(MenuItem.FeatureFlagsMenuItem),
        @Transient val itemInEditing: Tracker? = null,
        @Transient val itemInDeleting: Tracker? = null,
        @Transient val currentAction: Action? = null,
        val animate: Boolean = true
    )

    data class Rendering(
        val state: State,
        val onAction: (Action) -> Unit
    )

    sealed class Output {
        data class TrackerDetail(val trackerId: String) : Output()
        object FeatureFlagList : Output()
        object CreateTracker : Output()
    }

    sealed class Action : WorkflowAction<State, Output> {
        data class SideEffectAction(val action: Action) : Action()
        data class NewRecordSubmitted(val tracker: Tracker, val value: String) : Action()
        data class ListUpdated(val list: List<TrackerWithRecords>) : Action()
        data class AddRecordClicked(val item: TrackerWithRecords) : Action()
        data class DeleteTrackerClicked(val item: TrackerWithRecords) : Action()
        data class DeleteSubmitted(val item: Tracker) : Action()
        data class TrackerClicked(val item: TrackerWithRecords) : Action()
        data class MenuItemClicked(val menuItem: MenuItem) : Action()
        object TrackDialogDismissed : Action()
        object DeleteDialogDismissed : Action()
        object CreateTrackerClicked : Action()
        object AnimationProceeded : Action()

        override fun WorkflowAction.Updater<State, Output>.apply() {
            nextState = when (val action = this@Action) {
                is SideEffectAction -> action.action.applyTo(nextState.copy(currentAction = null)).let { result ->
                    result.second?.also { setOutput(it) }
                    result.first
                }
                is ListUpdated -> nextState.copy(items = action.list.map { it.toListItem() })
                is AddRecordClicked -> nextState.copy(itemInEditing = action.item.tracker)
                is DeleteTrackerClicked -> nextState.copy(itemInDeleting = action.item.tracker)
                is TrackerClicked -> nextState.also { setOutput(Output.TrackerDetail(action.item.tracker.id)) }
                is MenuItemClicked -> handleMenuItem(action.menuItem)
                CreateTrackerClicked -> nextState.also { setOutput(Output.CreateTracker) }
                TrackDialogDismissed -> nextState.copy(itemInEditing = null)
                DeleteDialogDismissed -> nextState.copy(itemInDeleting = null)
                AnimationProceeded -> nextState.copy(animate = false)
                else -> nextState.copy(currentAction = this@Action)
            }
        }

        private fun WorkflowAction.Updater<State, Output>.handleMenuItem(menuItem: MenuItem): State =
            when (menuItem) {
                MenuItem.FeatureFlagsMenuItem -> nextState.also { setOutput(Output.FeatureFlagList) }
            }
    }

    override fun initialState(props: Props, snapshot: Snapshot?): State = snapshot?.toData(moshi) ?: State(animate = props.animate)

    override fun render(
        props: Props,
        state: State,
        context: RenderContext<State, Output>
    ): Any {
        context.runningWorker(getAllTrackersWorker) { Action.ListUpdated(it) }
        runSideEffects(state, context)

        return Rendering(
            state = state,
            onAction = { context.actionSink.send(it) }
        )
    }

    override fun snapshotState(state: State): Snapshot = state.toSnapshot(moshi)

    private fun runSideEffects(state: State, context: RenderContext<State, Output>) {
        when (val action = state.currentAction) {
            is Action.NewRecordSubmitted -> {
                val worker = if (action.tracker.compatibleUnit == TrackableUnit.Word) {
                    Worker.from { trackingRepository.saveRecord(action.tracker, action.value) }
                } else {
                    Worker.from { trackingRepository.saveRecord(action.tracker, action.value.toDoubleOrNull() ?: 0.0) }
                }
                context.runningWorker(worker) { Action.SideEffectAction(Action.TrackDialogDismissed) }
            }
            is Action.DeleteSubmitted -> {
                val worker = Worker.from { trackingRepository.deleteTracker(action.item) }
                context.runningWorker(worker) { Action.SideEffectAction(Action.DeleteDialogDismissed) }
            }
        }
    }
}