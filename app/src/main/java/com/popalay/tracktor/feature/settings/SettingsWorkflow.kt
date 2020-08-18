package com.popalay.tracktor.feature.settings

import com.squareup.workflow.RenderContext
import com.squareup.workflow.Snapshot
import com.squareup.workflow.StatefulWorkflow
import com.squareup.workflow.WorkflowAction

class SettingsWorkflow : StatefulWorkflow<Unit, Unit, SettingsWorkflow.Output, SettingsWorkflow.Rendering>() {

    sealed class Output {
        object Back : Output()
        object FeatureTogglesList : Output()
    }

    sealed class Action : WorkflowAction<Unit, Output> {
        object BackClicked : Action()
        object FeatureTogglesClicked : Action()

        override fun WorkflowAction.Updater<Unit, Output>.apply() {
            nextState = when (this@Action) {
                BackClicked -> nextState.also { setOutput(Output.Back) }
                FeatureTogglesClicked -> nextState.also { setOutput(Output.FeatureTogglesList) }
            }
        }
    }

    data class Rendering(
        val onAction: (Action) -> Unit
    )

    override fun initialState(props: Unit, snapshot: Snapshot?) = Unit

    override fun render(
        props: Unit,
        state: Unit,
        context: RenderContext<Unit, Output>
    ): Rendering = Rendering(
        onAction = { context.actionSink.send(it) }
    )

    override fun snapshotState(state: Unit): Snapshot = Snapshot.EMPTY
}