package com.popalay.tracktor.ui.list

import com.popalay.tracktor.data.TrackingRepository
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.model.ValueRecord
import com.popalay.tracktor.worker.GetAllTrackersWorker
import com.squareup.workflow.RenderContext
import com.squareup.workflow.Snapshot
import com.squareup.workflow.StatefulWorkflow
import com.squareup.workflow.action
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

object ListWorkflow : StatefulWorkflow<Unit, ListWorkflow.State, Nothing, ListWorkflow.Rendering>() {

    data class State(
        val items: List<Any>
    )

    sealed class Event {
        data class NewTrackerSubmitted(val tracker: Tracker) : Event()
        data class NewRecordSubmitted(val tracker: Tracker, val value: Double) : Event()
    }

    data class Rendering(
        val state: State,
        val onEvent: (Event) -> Unit
    )

    private fun updateList(items: List<TrackerWithRecords>) = action {
        nextState = nextState.copy(items = listOf(Any()) + items)
    }

    override fun initialState(props: Unit, snapshot: Snapshot?): State = State(listOf())

    override fun render(
        props: Unit,
        state: State,
        context: RenderContext<State, Nothing>
    ): Rendering {
        context.runningWorker(GetAllTrackersWorker(), handler = ::updateList)
        return Rendering(
            state = state,
            onEvent = {
                when (it) {
                    is Event.NewTrackerSubmitted -> context.actionSink.send(action {
                        GlobalScope.launch {
                            TrackingRepository.saveTracker(it.tracker)
                        }
                    })
                    is Event.NewRecordSubmitted -> context.actionSink.send(action {
                        GlobalScope.launch {
                            val record = ValueRecord(
                                id = UUID.randomUUID().toString(),
                                trackerId = it.tracker.id,
                                value = it.value,
                                date = LocalDateTime.now()
                            )
                            TrackingRepository.saveRecord(record)
                        }
                    })
                }
            }
        )
    }

    override fun snapshotState(state: State): Snapshot = Snapshot.EMPTY
}