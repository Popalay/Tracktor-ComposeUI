package com.popalay.tracktor.ui.list

import com.popalay.tracktor.data.TrackingRepository
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.model.ValueRecord
import com.popalay.tracktor.worker.GetAllTrackersWorker
import com.squareup.workflow.RenderContext
import com.squareup.workflow.Snapshot
import com.squareup.workflow.StatefulWorkflow
import com.squareup.workflow.WorkflowAction
import com.squareup.workflow.action
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

object ListWorkflow : StatefulWorkflow<Unit, ListWorkflow.State, Nothing, ListWorkflow.Rendering>() {

    data class State(
        val items: List<Any>,
        val itemInEditing: TrackerWithRecords?,
        val itemInDeleting: TrackerWithRecords?
    )

    sealed class Event {
        data class NewTrackerSubmitted(val tracker: Tracker) : Event()
        data class NewRecordSubmitted(val tracker: Tracker, val value: Double) : Event()
        data class ListUpdated(val list: List<TrackerWithRecords>) : Event()
        data class ItemClicked(val item: TrackerWithRecords) : Event()
        data class ItemLongClicked(val item: TrackerWithRecords) : Event()
        data class DeleteSubmitted(val item: TrackerWithRecords) : Event()
        object TrackDialogDismissed : Event()
        object DeleteDialogDismissed : Event()
    }

    data class Rendering(
        val state: State,
        val onEvent: (Event) -> Unit
    )

    override fun initialState(props: Unit, snapshot: Snapshot?): State = State(
        items = listOf(),
        itemInEditing = null,
        itemInDeleting = null
    )

    private val eventDispatcher: (Event) -> WorkflowAction<State, Nothing> = { event ->
        when (event) {
            is Event.NewTrackerSubmitted -> action {
                GlobalScope.launch {
                    TrackingRepository.saveTracker(event.tracker)
                }
            }
            is Event.NewRecordSubmitted -> action {
                GlobalScope.launch {
                    val record = ValueRecord(
                        id = UUID.randomUUID().toString(),
                        trackerId = event.tracker.id,
                        value = event.value,
                        date = LocalDateTime.now()
                    )
                    TrackingRepository.saveRecord(record)
                }
            }
            is Event.ListUpdated -> action {
                nextState = nextState.copy(items = listOf(Any()) + event.list)
            }
            is Event.ItemClicked -> action {
                nextState = nextState.copy(itemInEditing = event.item)
            }
            is Event.ItemLongClicked -> action {
                nextState = nextState.copy(itemInDeleting = event.item)
            }
            is Event.DeleteSubmitted -> action {
                GlobalScope.launch {
                    TrackingRepository.deleteTracker(event.item.tracker)
                }
            }
            Event.TrackDialogDismissed -> action {
                nextState = nextState.copy(itemInEditing = null)
            }
            Event.DeleteDialogDismissed -> action {
                nextState = nextState.copy(itemInDeleting = null)
            }
        }
    }

    override fun render(
        props: Unit,
        state: State,
        context: RenderContext<State, Nothing>
    ): Rendering {
        context.runningWorker(GetAllTrackersWorker()) { eventDispatcher(Event.ListUpdated(it)) }
        return Rendering(
            state = state,
            onEvent = { context.actionSink.send(eventDispatcher(it)) }
        )
    }

    override fun snapshotState(state: State): Snapshot = Snapshot.EMPTY
}