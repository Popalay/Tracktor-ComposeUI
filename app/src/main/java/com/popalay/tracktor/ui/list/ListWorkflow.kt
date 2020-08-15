package com.popalay.tracktor.ui.list

import com.popalay.tracktor.data.TrackingRepository
import com.popalay.tracktor.domain.worker.GetAllTrackersWorker
import com.popalay.tracktor.model.Category
import com.popalay.tracktor.model.MenuItem
import com.popalay.tracktor.model.Statistic
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
    companion object {
        private const val DELETING_UNDO_TIMEOUT_MILLIS = 2500L
    }

    data class Props(
        val animate: Boolean,
        val itemInDeleting: TrackerWithRecords? = null
    )

    @JsonClass(generateAdapter = true)
    data class State(
        @Transient val items: List<TrackerListItem> = emptyList(),
        @Transient val filteredItems: List<TrackerListItem> = emptyList(),
        @Transient val allCategories: List<Category> = emptyList(),
        @Transient val menuItems: List<MenuItem> = listOf(MenuItem.FeatureFlagsMenuItem),
        @Transient val selectedCategory: Category = Category.All,
        @Transient val itemInEditing: Tracker? = null,
        @Transient val itemInDeleting: TrackerWithRecords? = null,
        @Transient val currentAction: Action? = null,
        @Transient val statistic: Statistic? = null,
        val showEmptyState: Boolean = false,
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
        data class DeleteSubmitted(val item: TrackerWithRecords) : Action()
        data class TrackerClicked(val item: TrackerWithRecords) : Action()
        data class MenuItemClicked(val menuItem: MenuItem) : Action()
        data class CategoryClick(val category: Category) : Action()
        object TrackDialogDismissed : Action()
        object CreateTrackerClicked : Action()
        object AnimationProceeded : Action()
        object UndoAvailabilityEnded : Action()
        object UndoDeletingClicked : Action()
        object UndoPerformed : Action()

        override fun WorkflowAction.Updater<State, Output>.apply() {
            nextState = when (val action = this@Action) {
                is SideEffectAction -> action.action.applyTo(nextState.copy(currentAction = null)).let { result ->
                    result.second?.also { setOutput(it) }
                    result.first
                }
                is ListUpdated -> {
                    val items = action.list.map { it.toListItem() }
                    nextState.copy(
                        items = items,
                        allCategories = action.list.extractCategories(),
                        filteredItems = items.filterByCategory(nextState.selectedCategory),
                        statistic = Statistic.generateFor(action.list),
                        showEmptyState = items.isEmpty()
                    )
                }
                is AddRecordClicked -> nextState.copy(itemInEditing = action.item.tracker)
                is TrackerClicked -> nextState.also { setOutput(Output.TrackerDetail(action.item.tracker.id)) }
                is MenuItemClicked -> handleMenuItem(action.menuItem)
                is DeleteSubmitted -> nextState.copy(itemInDeleting = action.item)
                is CategoryClick -> nextState.copy(
                    selectedCategory = action.category,
                    filteredItems = nextState.items.filterByCategory(action.category),
                )
                CreateTrackerClicked -> nextState.also { setOutput(Output.CreateTracker) }
                TrackDialogDismissed -> nextState.copy(itemInEditing = null)
                AnimationProceeded -> nextState.copy(animate = false)
                UndoAvailabilityEnded -> nextState.copy(itemInDeleting = null)
                UndoPerformed -> nextState.copy(itemInDeleting = null)
                else -> nextState.copy(currentAction = this@Action)
            }
        }

        private fun WorkflowAction.Updater<State, Output>.handleMenuItem(menuItem: MenuItem): State =
            when (menuItem) {
                MenuItem.FeatureFlagsMenuItem -> nextState.also { setOutput(Output.FeatureFlagList) }
            }

        private fun List<TrackerListItem>.filterByCategory(category: Category) =
            if (category == Category.All) this else filter { category in it.data.categories }

        private fun List<TrackerWithRecords>.extractCategories() = flatMap { it.categories }
            .distinct().sortedBy { it.name }
            .let { if (it.isEmpty()) it else listOf(Category.All).plus(it) }
    }

    override fun initialState(props: Props, snapshot: Snapshot?): State =
        snapshot?.toData(moshi) ?: State(animate = props.animate, itemInDeleting = props.itemInDeleting)

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
            is Action.DeleteTrackerClicked -> {
                val worker = Worker.from { trackingRepository.deleteTracker(action.item.tracker) }
                context.runningWorker(worker) { Action.SideEffectAction(Action.DeleteSubmitted(action.item)) }
            }
            is Action.UndoDeletingClicked -> {
                if (state.itemInDeleting == null) return
                val worker = Worker.from { trackingRepository.restoreTracker(state.itemInDeleting) }
                context.runningWorker(worker) { Action.SideEffectAction(Action.UndoPerformed) }
            }
        }
        if (state.itemInDeleting != null) {
            val undoTimerWorker = Worker.timer(DELETING_UNDO_TIMEOUT_MILLIS)
            context.runningWorker(undoTimerWorker) { Action.SideEffectAction(Action.UndoAvailabilityEnded) }
        }
    }
}