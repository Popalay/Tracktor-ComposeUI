package com.popalay.tracktor.feature.settings

import com.popalay.tracktor.feature.featureflagslist.FeatureFlagsListWorkflow
import com.squareup.workflow.RenderContext
import com.squareup.workflow.Snapshot
import com.squareup.workflow.StatefulWorkflow
import com.squareup.workflow.WorkflowAction
import com.squareup.workflow.renderChild
import java.nio.charset.Charset

class SettingsWorkflow(
    private val featureFlagsListWorkflow: FeatureFlagsListWorkflow
) : StatefulWorkflow<Unit, SettingsWorkflow.State, SettingsWorkflow.Output, Any>() {

    enum class State {
        Root, FeatureFlagsList
    }

    sealed class Output {
        object Back : Output()
    }

    sealed class Action : WorkflowAction<State, Output> {
        data class FeatureFlagListOutput(val output: FeatureFlagsListWorkflow.Output) : Action()
        object BackClicked : Action()
        object FeatureTogglesClicked : Action()

        override fun WorkflowAction.Updater<State, Output>.apply() {
            nextState = when (val action = this@Action) {
                is FeatureFlagListOutput -> when (action.output) {
                    FeatureFlagsListWorkflow.Output.Back -> State.Root
                }
                BackClicked -> nextState.also { setOutput(Output.Back) }
                FeatureTogglesClicked -> State.FeatureFlagsList
            }
        }
    }

    data class Rendering(
        val onAction: (Action) -> Unit
    )

    override fun initialState(props: Unit, snapshot: Snapshot?) =
        snapshot?.let { State.valueOf(it.bytes.string(Charset.defaultCharset())) } ?: State.Root

    override fun render(
        props: Unit,
        state: State,
        context: RenderContext<State, Output>
    ): Any = when (state) {
        State.Root -> Rendering(
            onAction = { context.actionSink.send(it) }
        )
        State.FeatureFlagsList -> context.renderChild(featureFlagsListWorkflow) { Action.FeatureFlagListOutput(it) }
    }

    override fun snapshotState(state: State): Snapshot = Snapshot.of(state.name)
}