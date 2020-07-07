package com.popalay.tracktor.ui.list

import com.popalay.tracktor.data.TrackingRepository
import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.model.ValueRecord
import com.popalay.tracktor.worker.GetAllTrackersWorker
import com.squareup.workflow.RenderContext
import com.squareup.workflow.Snapshot
import com.squareup.workflow.StatefulWorkflow
import com.squareup.workflow.WorkflowAction
import com.squareup.workflow.action
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

class ListWorkflow(
    private val trackingRepository: TrackingRepository,
    private val getAllTrackersWorker: GetAllTrackersWorker
) : StatefulWorkflow<Unit, ListWorkflow.State, Nothing, ListWorkflow.Rendering>() {

    private val job: Job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    data class State(
        val items: List<ListItem>,
        val itemInCreating: Tracker?,
        val itemInEditing: TrackerWithRecords?,
        val itemInDeleting: TrackerWithRecords?
    )

    sealed class Event {
        data class UnitSubmitted(val unit: TrackableUnit) : Event()
        data class NewRecordSubmitted(val tracker: Tracker, val value: Double) : Event()
        data class ListUpdated(val list: List<ListItem>) : Event()
        data class AddRecordClicked(val item: TrackerWithRecords) : Event()
        data class RemoveTrackerClicked(val item: TrackerWithRecords) : Event()
        data class DeleteSubmitted(val item: TrackerWithRecords) : Event()
        data class NewTrackerTitleSubmitted(val title: String) : Event()
        object TrackDialogDismissed : Event()
        object DeleteDialogDismissed : Event()
        object ChooseUnitDialogDismissed : Event()
    }

    data class Rendering(
        val state: State,
        val onEvent: (Event) -> Unit
    )

    override fun initialState(props: Unit, snapshot: Snapshot?): State = State(
        items = listOf(),
        itemInEditing = null,
        itemInDeleting = null,
        itemInCreating = null
    )

    private val eventDispatcher: (Event) -> WorkflowAction<State, Nothing> = { event ->
        when (event) {
            is Event.UnitSubmitted -> action {
                coroutineScope.launch {
                    nextState.itemInCreating?.let {
                        trackingRepository.saveTracker(it.copy(unit = event.unit))
                    }
                    nextState = nextState.copy(itemInCreating = null)
                }
            }
            is Event.NewRecordSubmitted -> action {
                coroutineScope.launch {
                    val record = ValueRecord(
                        id = UUID.randomUUID().toString(),
                        trackerId = event.tracker.id,
                        value = event.value,
                        date = LocalDateTime.now()
                    )
                    trackingRepository.saveRecord(record)
                }
            }
            is Event.ListUpdated -> action {
                nextState = nextState.copy(items = event.list)
            }
            is Event.AddRecordClicked -> action {
                nextState = nextState.copy(itemInEditing = event.item)
            }
            is Event.RemoveTrackerClicked -> action {
                nextState = nextState.copy(itemInDeleting = event.item)
            }
            is Event.DeleteSubmitted -> action {
                coroutineScope.launch {
                    trackingRepository.deleteTracker(event.item.tracker)
                }
            }
            is Event.NewTrackerTitleSubmitted -> action {
                nextState = nextState.copy(
                    itemInCreating = Tracker(
                        id = UUID.randomUUID().toString(),
                        title = event.title,
                        unit = TrackableUnit.None,
                        date = LocalDateTime.now()
                    )
                )
            }
            Event.TrackDialogDismissed -> action {
                nextState = nextState.copy(itemInEditing = null)
            }
            Event.DeleteDialogDismissed -> action {
                nextState = nextState.copy(itemInDeleting = null)
            }
            Event.ChooseUnitDialogDismissed -> action {
                nextState = nextState.copy(itemInCreating = null)
            }
        }
    }

    override fun render(
        props: Unit,
        state: State,
        context: RenderContext<State, Nothing>
    ): Rendering {
        context.runningWorker(getAllTrackersWorker) { list -> eventDispatcher(Event.ListUpdated(list.map { it.toListItem() })) }
        return Rendering(
            state = state,
            onEvent = { context.actionSink.send(eventDispatcher(it)) }
        )
    }

    override fun snapshotState(state: State): Snapshot = Snapshot.EMPTY
}