package com.popalay.tracktor.list

import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.model.TrackedValue
import com.squareup.workflow.RenderContext
import com.squareup.workflow.Snapshot
import com.squareup.workflow.StatefulWorkflow
import com.squareup.workflow.action

object ListWorkflow : StatefulWorkflow<Unit, ListWorkflow.State, Nothing, ListWorkflow.Rendering>() {

    data class State(
        val items: List<Any>
    )

    data class Rendering(
        val state: State,
        val onSubmitClicked: (TrackedValue) -> Unit,
        val onNewRecordSubmitted: (TrackedValue, Double) -> Unit
    )

    private fun submitNewItem(newItem: TrackedValue) = action {
        nextState = with(nextState) {
            copy(
                items = items.plus(newItem)
            )
        }
    }

    private fun submitNewRecord(item: TrackedValue, newRecord: Double) = action {
        nextState = with(nextState) {
            copy(
                items = items.toMutableList().apply {
                    this[indexOf(item)] = item.copy(value = newRecord)
                }
            )
        }
    }

    override fun initialState(props: Unit, snapshot: Snapshot?): State =
        State(listOf(Any(), TrackedValue("Sample", 0.0, TrackableUnit.Kilograms)))

    override fun render(
        props: Unit,
        state: State,
        context: RenderContext<State, Nothing>
    ): Rendering =
        Rendering(
            state = state,
            onSubmitClicked = { context.actionSink.send(submitNewItem(it)) },
            onNewRecordSubmitted = { item, newRecord -> context.actionSink.send(submitNewRecord(item, newRecord)) }
        )

    override fun snapshotState(state: State): Snapshot = Snapshot.EMPTY
}