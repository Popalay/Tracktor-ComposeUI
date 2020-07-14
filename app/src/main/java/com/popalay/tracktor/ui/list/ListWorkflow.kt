package com.popalay.tracktor.ui.list

import com.popalay.tracktor.data.TrackingRepository
import com.popalay.tracktor.model.MenuItem
import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.model.TrackerListItem
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.model.toListItem
import com.popalay.tracktor.ui.featureflagslist.FeatureFlagsListWorkflow
import com.popalay.tracktor.ui.trackerdetail.TrackerDetailWorkflow
import com.popalay.tracktor.worker.GetAllTrackersWorker
import com.squareup.workflow.RenderContext
import com.squareup.workflow.Snapshot
import com.squareup.workflow.StatefulWorkflow
import com.squareup.workflow.Worker
import com.squareup.workflow.WorkflowAction
import org.koin.core.KoinComponent
import org.koin.core.get
import java.time.LocalDateTime
import java.util.UUID

data class DialogState<T, P>(
    val input: T,
    val output: P,
    val isConsumed: Boolean = false
)

class ListWorkflow(
    private val trackingRepository: TrackingRepository,
    private val getAllTrackersWorker: GetAllTrackersWorker
) : StatefulWorkflow<Unit, ListWorkflow.State, Nothing, Any>(), KoinComponent {

    data class State(
        val items: List<TrackerListItem> = emptyList(),
        val menuItems: List<MenuItem> = listOf(MenuItem.FeatureFlagsMenuItem),
        val itemInCreating: DialogState<Tracker, TrackableUnit>? = null,
        val itemInEditing: DialogState<Tracker, Double>? = null,
        val itemInDeleting: DialogState<Tracker, Tracker>? = null,
        val trackerDetails: DialogState<Tracker, Unit>? = null,
        val featureFlagsList: Boolean = false
    )

    sealed class Action : WorkflowAction<State, Nothing> {
        data class UnitSubmitted(val unit: TrackableUnit) : Action()
        data class NewRecordSubmitted(val tracker: Tracker, val value: Double) : Action()
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
        object Back : Action()

        override fun WorkflowAction.Updater<State, Nothing>.apply() {
            nextState = when (val action = this@Action) {
                is UnitSubmitted -> nextState.copy(itemInCreating = nextState.itemInCreating?.copy(output = action.unit, isConsumed = true))
                is NewRecordSubmitted -> nextState.copy(itemInEditing = nextState.itemInEditing?.copy(output = action.value, isConsumed = true))
                is DeleteSubmitted -> nextState.copy(itemInDeleting = nextState.itemInDeleting?.copy(isConsumed = true))
                is ListUpdated -> nextState.copy(items = action.list.map { it.toListItem() })
                is AddRecordClicked -> nextState.copy(itemInEditing = DialogState(action.item.tracker, 0.0))
                is DeleteTrackerClicked -> nextState.copy(itemInDeleting = DialogState(action.item.tracker, action.item.tracker))
                is TrackerClicked -> nextState.copy(trackerDetails = DialogState(action.item.tracker, Unit))
                is NewTrackerTitleSubmitted -> nextState.copy(
                    itemInCreating = DialogState(
                        Tracker(
                            id = UUID.randomUUID().toString(),
                            title = action.title,
                            unit = TrackableUnit.None,
                            date = LocalDateTime.now()
                        ),
                        TrackableUnit.None
                    )
                )
                is MenuItemClicked -> {
                    when (action.menuItem) {
                        MenuItem.FeatureFlagsMenuItem -> nextState.copy(featureFlagsList = true)
                    }
                }
                TrackDialogDismissed -> nextState.copy(itemInEditing = null)
                DeleteDialogDismissed -> nextState.copy(itemInDeleting = null)
                ChooseUnitDialogDismissed -> nextState.copy(itemInCreating = null)
                Back -> nextState.copy(trackerDetails = null, featureFlagsList = false)
            }
        }
    }

    data class Rendering(
        val state: State,
        val onAction: (Action) -> Unit
    )

    override fun initialState(props: Unit, snapshot: Snapshot?): State = State(
        items = listOf(),
        itemInEditing = null,
        itemInDeleting = null,
        itemInCreating = null,
        trackerDetails = null
    )

    override fun render(
        props: Unit,
        state: State,
        context: RenderContext<State, Nothing>
    ): Any {
        context.runningWorker(getAllTrackersWorker) { Action.ListUpdated(it) }
        runSideEffects(state, context)

        return when {
            state.trackerDetails != null -> context.renderChild(
                get<TrackerDetailWorkflow>(),
                TrackerDetailWorkflow.Props(state.trackerDetails.input.id),
                state.trackerDetails.input.id,
                handler = { Action.Back }
            )
            state.featureFlagsList -> context.renderChild(
                get<FeatureFlagsListWorkflow>(),
                Unit,
                handler = { Action.Back }
            )
            else -> Rendering(
                state = state,
                onAction = { context.actionSink.send(it) }
            )
        }
    }

    override fun snapshotState(state: State): Snapshot = Snapshot.EMPTY

    private fun runSideEffects(state: State, context: RenderContext<State, Nothing>) {
        if (state.itemInCreating?.isConsumed == true) {
            val worker = Worker.from { trackingRepository.saveTracker(state.itemInCreating.input.copy(unit = state.itemInCreating.output)) }
            context.runningWorker(worker) { Action.ChooseUnitDialogDismissed }
        }
        if (state.itemInEditing?.isConsumed == true) {
            val worker = Worker.from { trackingRepository.saveRecord(state.itemInEditing.input, state.itemInEditing.output) }
            context.runningWorker(worker) { Action.TrackDialogDismissed }
        }
        if (state.itemInDeleting?.isConsumed == true) {
            val worker = Worker.from { trackingRepository.deleteTracker(state.itemInDeleting.output) }
            context.runningWorker(worker) { Action.DeleteDialogDismissed }
        }
    }
}