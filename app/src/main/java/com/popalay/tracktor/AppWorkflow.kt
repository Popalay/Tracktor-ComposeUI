package com.popalay.tracktor

import com.popalay.tracktor.ui.featureflagslist.FeatureFlagsListWorkflow
import com.popalay.tracktor.ui.list.ListWorkflow
import com.popalay.tracktor.ui.trackerdetail.TrackerDetailWorkflow
import com.squareup.workflow.RenderContext
import com.squareup.workflow.Snapshot
import com.squareup.workflow.StatefulWorkflow
import com.squareup.workflow.WorkflowAction
import com.squareup.workflow.renderChild
import java.nio.charset.Charset

class AppWorkflow(
    private val listWorkflow: ListWorkflow,
    private val trackerDetailWorkflow: TrackerDetailWorkflow,
    private val featureFlagsListWorkflow: FeatureFlagsListWorkflow
) : StatefulWorkflow<Unit, AppWorkflow.State, Nothing, Any>() {

    sealed class State {
        object TrackerList : State()
        data class TrackerDetail(val trackerId: String) : State()
        object FeatureFlagList : State()
    }

    sealed class Action : WorkflowAction<State, Nothing> {
        data class TrackerListOutput(val output: ListWorkflow.Output) : Action()
        data class TrackerDetailOutput(val output: TrackerDetailWorkflow.Output) : Action()
        data class FeatureFlagsListOutput(val output: FeatureFlagsListWorkflow.Output) : Action()

        override fun WorkflowAction.Updater<State, Nothing>.apply() {
            nextState = when (val action = this@Action) {
                is TrackerListOutput -> when (val output = action.output) {
                    is ListWorkflow.Output.TrackerDetail -> State.TrackerDetail(output.trackerId)
                    ListWorkflow.Output.FeatureFlagList -> State.FeatureFlagList
                }
                is TrackerDetailOutput -> when (action.output) {
                    TrackerDetailWorkflow.Output.Back -> State.TrackerList
                }
                is FeatureFlagsListOutput -> when (action.output) {
                    FeatureFlagsListWorkflow.Output.Back -> State.TrackerList
                }
            }
        }
    }

    override fun initialState(props: Unit, snapshot: Snapshot?): State = snapshot?.toState() ?: State.TrackerList

    override fun render(props: Unit, state: State, context: RenderContext<State, Nothing>): Any = when (state) {
        State.TrackerList -> context.renderChild(listWorkflow) { Action.TrackerListOutput(it) }
        is State.TrackerDetail -> context.renderChild(
            trackerDetailWorkflow,
            TrackerDetailWorkflow.Props(state.trackerId)
        ) { Action.TrackerDetailOutput(it) }
        State.FeatureFlagList -> context.renderChild(featureFlagsListWorkflow) { Action.FeatureFlagsListOutput(it) }
    }

    override fun snapshotState(state: State): Snapshot = state.toSnapshot()

    private fun State.toSnapshot() = when (this) {
        State.TrackerList -> Snapshot.of("TrackerList")
        is State.TrackerDetail -> Snapshot.of("TrackerDetail,$trackerId")
        State.FeatureFlagList -> Snapshot.Companion.of("FeatureFlagList")
    }

    private fun Snapshot.toState(): State? {
        val stringSnapshot = bytes.string(Charset.defaultCharset())
        return when {
            stringSnapshot.startsWith("TrackerList") -> State.TrackerList
            stringSnapshot.startsWith("TrackerDetail") -> State.TrackerDetail(stringSnapshot.split(",")[1])
            stringSnapshot.startsWith("FeatureFlagList") -> State.FeatureFlagList
            else -> null
        }
    }
}