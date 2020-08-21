package com.popalay.tracktor

import com.popalay.tracktor.feature.list.ListWorkflow
import com.squareup.workflow.RenderContext
import com.squareup.workflow.Snapshot
import com.squareup.workflow.StatefulWorkflow
import com.squareup.workflow.WorkflowAction

class AppWorkflow(
    private val listWorkflow: ListWorkflow
) : StatefulWorkflow<Unit, AppWorkflow.State, Nothing, Any>() {

    sealed class State {
        object TrackerList : State()
    }

    sealed class Action : WorkflowAction<State, Nothing>

    override fun initialState(props: Unit, snapshot: Snapshot?): State = State.TrackerList

    override fun render(props: Unit, state: State, context: RenderContext<State, Nothing>): Any = when (state) {
        is State.TrackerList -> context.renderChild(listWorkflow, ListWorkflow.Props) { WorkflowAction.noAction() }
    }

    override fun snapshotState(state: State): Snapshot = Snapshot.EMPTY
}