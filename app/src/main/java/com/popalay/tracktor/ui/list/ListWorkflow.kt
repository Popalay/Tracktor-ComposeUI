package com.popalay.tracktor.ui.list

import com.popalay.tracktor.data.TrackingRepository
import com.popalay.tracktor.domain.worker.GetAllTrackersWorker
import com.popalay.tracktor.model.MenuItem
import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.model.TrackerListItem
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.model.toListItem
import com.squareup.workflow.RenderContext
import com.squareup.workflow.Snapshot
import com.squareup.workflow.StatefulWorkflow
import com.squareup.workflow.Worker
import com.squareup.workflow.WorkflowAction
import com.squareup.workflow.applyTo
import org.koin.core.KoinComponent
import java.time.LocalDateTime
import java.util.UUID

class ListWorkflow(
    private val trackingRepository: TrackingRepository,
    private val getAllTrackersWorker: GetAllTrackersWorker
) : StatefulWorkflow<Unit, ListWorkflow.State, ListWorkflow.Output, Any>(), KoinComponent {

    data class State(
        val items: List<TrackerListItem> = emptyList(),
        val menuItems: List<MenuItem> = listOf(MenuItem.FeatureFlagsMenuItem),
        val itemInCreating: Tracker? = null,
        val itemInEditing: Tracker? = null,
        val itemInDeleting: Tracker? = null,
        val currentAction: Action? = null
    )

    data class Rendering(
        val state: State,
        val onAction: (Action) -> Unit
    )

    sealed class Output {
        data class TrackerDetail(val trackerId: String) : Output()
        object FeatureFlagList : Output()
    }

    sealed class Action : WorkflowAction<State, Output> {
        data class SideEffectAction(val action: Action) : Action()
        data class UnitSubmitted(val unit: TrackableUnit) : Action()
        data class NewRecordSubmitted(val tracker: Tracker, val value: String) : Action()
        data class ListUpdated(val list: List<TrackerWithRecords>) : Action()
        data class AddRecordClicked(val item: TrackerWithRecords) : Action()
        data class DeleteTrackerClicked(val item: TrackerWithRecords) : Action()
        data class DeleteSubmitted(val item: Tracker) : Action()
        data class NewTrackerTitleSubmitted(val title: String) : Action()
        data class TrackerClicked(val item: TrackerWithRecords) : Action()
        data class MenuItemClicked(val menuItem: MenuItem) : Action()
        object TrackDialogDismissed : Action()
        object DeleteDialogDismissed : Action()
        object ChooseUnitDialogDismissed : Action()

        override fun WorkflowAction.Updater<State, Output>.apply() {
            nextState = when (val action = this@Action) {
                is SideEffectAction -> action.action.applyTo(nextState.copy(currentAction = null)).first
                is ListUpdated -> nextState.copy(items = action.list.map { it.toListItem() })
                is AddRecordClicked -> nextState.copy(itemInEditing = action.item.tracker)
                is DeleteTrackerClicked -> nextState.copy(itemInDeleting = action.item.tracker)
                is TrackerClicked -> nextState.also { setOutput(Output.TrackerDetail(action.item.tracker.id)) }
                is NewTrackerTitleSubmitted -> nextState.copy(
                    itemInCreating = Tracker(
                        id = UUID.randomUUID().toString(),
                        title = action.title,
                        unit = TrackableUnit.None,
                        date = LocalDateTime.now()
                    )
                )
                is MenuItemClicked -> handleMenuItem(action.menuItem)
                TrackDialogDismissed -> nextState.copy(itemInEditing = null)
                DeleteDialogDismissed -> nextState.copy(itemInDeleting = null)
                ChooseUnitDialogDismissed -> nextState.copy(itemInCreating = null)
                else -> nextState.copy(currentAction = this@Action)
            }
        }

        private fun WorkflowAction.Updater<State, Output>.handleMenuItem(menuItem: MenuItem): State =
            when (menuItem) {
                MenuItem.FeatureFlagsMenuItem -> nextState.also { setOutput(Output.FeatureFlagList) }
            }
    }

    override fun initialState(props: Unit, snapshot: Snapshot?): State = State()

    override fun render(
        props: Unit,
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

    override fun snapshotState(state: State): Snapshot = Snapshot.EMPTY

    private fun runSideEffects(state: State, context: RenderContext<State, Output>) {
        when (val action = state.currentAction) {
            is Action.UnitSubmitted -> {
                val worker = Worker.from { trackingRepository.saveTracker(requireNotNull(state.itemInCreating).copy(unit = action.unit)) }
                context.runningWorker(worker) { Action.SideEffectAction(Action.ChooseUnitDialogDismissed) }
            }
            is Action.NewRecordSubmitted -> {
                val worker = if (action.tracker.unit == TrackableUnit.Word) {
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