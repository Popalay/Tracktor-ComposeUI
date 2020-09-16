package com.popalay.tracktor.domain.workflow

import com.popalay.tracktor.data.TrackingRepository
import com.popalay.tracktor.data.model.Category
import com.popalay.tracktor.data.model.Statistic
import com.popalay.tracktor.data.model.TrackableUnit
import com.popalay.tracktor.data.model.Tracker
import com.popalay.tracktor.data.model.TrackerListItem
import com.popalay.tracktor.data.model.TrackerWithRecords
import com.popalay.tracktor.data.model.toListItem
import com.popalay.tracktor.domain.utils.toData
import com.popalay.tracktor.domain.utils.toSnapshot
import com.popalay.tracktor.domain.worker.GetAllTrackersWorker
import com.squareup.workflow.RenderContext
import com.squareup.workflow.Snapshot
import com.squareup.workflow.StatefulWorkflow
import com.squareup.workflow.Worker
import com.squareup.workflow.WorkflowAction
import com.squareup.workflow.applyTo
import com.squareup.workflow.renderChild
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koin.core.KoinComponent

class ListWorkflow(
    private val trackingRepository: TrackingRepository,
    private val getAllTrackersWorker: GetAllTrackersWorker,
    private val trackerDetailWorkflow: TrackerDetailWorkflow,
    private val createTrackerWorkflow: CreateTrackerWorkflow,
    private val settingsWorkflow: SettingsWorkflow
) : StatefulWorkflow<ListWorkflow.Props, ListWorkflow.State, ListWorkflow.Output, Any>(), KoinComponent {
    companion object {
        private const val DELETING_UNDO_TIMEOUT_MILLIS = 4000L
    }

    object Props

    @Serializable
    data class State(
        @Transient val items: List<TrackerListItem> = emptyList(),
        @Transient val filteredItems: List<TrackerListItem> = emptyList(),
        @Transient val allCategories: List<Category> = emptyList(),
        @Transient val itemInEditing: Tracker? = null,
        @Transient val itemInDeleting: Tracker? = null,
        @Transient val currentAction: Action? = null,
        @Transient val statistic: Statistic? = null,
        val selectedCategory: Category = Category.All,
        val childState: ChildState? = null,
        val showEmptyState: Boolean = false,
        val animate: Boolean = true
    )

    @Serializable
    sealed class ChildState {
        @Serializable
        data class TrackerDetail(val trackerId: String) : ChildState()

        @Serializable
        @Suppress("CanSealedSubClassBeObject")
        class Settings : ChildState()

        @Serializable
        @Suppress("CanSealedSubClassBeObject")
        class TrackerCreation : ChildState()
    }

    data class Rendering(
        val items: List<TrackerListItem>,
        val filteredItems: List<TrackerListItem>,
        val allCategories: List<Category>,
        val itemInEditing: Tracker?,
        val itemInDeleting: Tracker?,
        val statistic: Statistic?,
        val selectedCategory: Category,
        val showEmptyState: Boolean,
        val animate: Boolean,
        val onAction: (Action) -> Unit
    )

    object Output

    sealed class Action : WorkflowAction<State, Output> {
        data class SideEffectAction(val action: Action) : Action()
        data class NewRecordSubmitted(val tracker: Tracker, val value: String) : Action()
        data class ListUpdated(val list: List<TrackerWithRecords>) : Action()
        data class AddRecordClicked(val item: TrackerWithRecords) : Action()
        data class DeleteTrackerClicked(val item: TrackerWithRecords) : Action()
        data class DeleteSubmitted(val item: TrackerWithRecords) : Action()
        data class TrackerClicked(val item: TrackerWithRecords) : Action()
        data class CategoryClick(val category: Category) : Action()
        data class TrackerDetailOutput(val output: TrackerDetailWorkflow.Output) : Action()
        data class TrackerCreationOutput(val output: CreateTrackerWorkflow.Output) : Action()
        data class SettingsOutput(val output: SettingsWorkflow.Output) : Action()
        object TrackDialogDismissed : Action()
        object CreateTrackerClicked : Action()
        object AnimationProceeded : Action()
        object UndoAvailabilityEnded : Action()
        object UndoDeletingClicked : Action()
        object UndoPerformed : Action()
        object SettingsClicked : Action()

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
                is TrackerClicked -> nextState.copy(childState = ChildState.TrackerDetail(action.item.tracker.id))
                is DeleteSubmitted -> nextState.copy(itemInDeleting = action.item.tracker)
                is CategoryClick -> nextState.copy(
                    selectedCategory = action.category,
                    filteredItems = nextState.items.filterByCategory(action.category),
                )
                is SettingsOutput -> when (action.output) {
                    SettingsWorkflow.Output.Back -> nextState.copy(childState = null)
                }
                is TrackerCreationOutput -> when (action.output) {
                    CreateTrackerWorkflow.Output.Back -> nextState.copy(childState = null)
                }
                is TrackerDetailOutput -> when (val output = action.output) {
                    TrackerDetailWorkflow.Output.Back -> nextState.copy(childState = null)
                    is TrackerDetailWorkflow.Output.TrackerDeleted -> nextState.copy(
                        childState = null,
                        itemInDeleting = output.item.tracker
                    )
                }
                CreateTrackerClicked -> nextState.copy(childState = ChildState.TrackerCreation())
                TrackDialogDismissed -> nextState.copy(itemInEditing = null)
                AnimationProceeded -> nextState.copy(animate = false)
                UndoAvailabilityEnded -> nextState.copy(itemInDeleting = null)
                UndoPerformed -> nextState.copy(itemInDeleting = null)
                SettingsClicked -> nextState.copy(childState = ChildState.Settings())
                else -> nextState.copy(currentAction = this@Action)
            }
        }

        private fun List<TrackerListItem>.filterByCategory(category: Category) =
            if (category == Category.All) this else filter { category in it.data.categories }

        private fun List<TrackerWithRecords>.extractCategories() = flatMap { it.categories }
            .distinct().sortedBy { it.name }
            .let { if (it.isEmpty()) it else listOf(Category.All).plus(it) }
    }

    override fun initialState(props: Props, snapshot: Snapshot?): State = snapshot?.toData() ?: State()

    override fun render(
        props: Props,
        state: State,
        context: RenderContext<State, Output>
    ): Any {
        context.runningWorker(getAllTrackersWorker) { Action.ListUpdated(it) }
        runSideEffects(state, context)

        return when (state.childState) {
            is ChildState.TrackerDetail -> context.renderChild(trackerDetailWorkflow, TrackerDetailWorkflow.Props(state.childState.trackerId)) {
                Action.TrackerDetailOutput(it)
            }
            is ChildState.Settings -> context.renderChild(settingsWorkflow) { Action.SettingsOutput(it) }
            is ChildState.TrackerCreation -> context.renderChild(createTrackerWorkflow) { Action.TrackerCreationOutput(it) }
            else -> Rendering(
                items = state.items,
                filteredItems = state.filteredItems,
                allCategories = state.allCategories,
                itemInEditing = state.itemInEditing,
                itemInDeleting = state.itemInDeleting,
                statistic = state.statistic,
                selectedCategory = state.selectedCategory,
                showEmptyState = state.showEmptyState,
                animate = state.animate,
                onAction = { context.actionSink.send(it) }
            )
        }
    }

    override fun snapshotState(state: State): Snapshot = state.toSnapshot()

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
                val worker = Worker.from { trackingRepository.softDeleteTracker(action.item.tracker.id) }
                context.runningWorker(worker) { Action.SideEffectAction(Action.DeleteSubmitted(action.item)) }
            }
            is Action.UndoDeletingClicked -> {
                state.itemInDeleting?.let {
                    val worker = Worker.from { trackingRepository.restoreTracker(it.id) }
                    context.runningWorker(worker) { Action.SideEffectAction(Action.UndoPerformed) }
                }
            }
        }
        if (state.itemInDeleting != null) {
            val undoTimerWorker = Worker.timer(DELETING_UNDO_TIMEOUT_MILLIS)
            context.runningWorker(undoTimerWorker) { Action.SideEffectAction(Action.UndoAvailabilityEnded) }
        }
    }
}