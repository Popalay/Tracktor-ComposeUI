package com.popalay.tracktor.ui.featureflagslist

import com.popalay.tracktor.data.featureflags.FeatureFlagsManager
import com.popalay.tracktor.data.model.FeatureFlagListItem
import com.popalay.tracktor.utils.updateItem
import com.squareup.workflow.RenderContext
import com.squareup.workflow.Snapshot
import com.squareup.workflow.StatefulWorkflow
import com.squareup.workflow.WorkflowAction

class FeatureFlagsListWorkflow(
    private val featureFlagsManager: FeatureFlagsManager
) : StatefulWorkflow<Unit, FeatureFlagsListWorkflow.State, FeatureFlagsListWorkflow.Output, FeatureFlagsListWorkflow.Rendering>() {

    data class State(
        val featureFlags: List<FeatureFlagListItem> = emptyList()
    )

    sealed class Output {
        object Back : Output()
    }

    sealed class Action : WorkflowAction<State, Output> {
        data class FeatureFlagChanged(val item: FeatureFlagListItem, val isEnabled: Boolean) : Action()
        object BackClicked : Action()

        override fun WorkflowAction.Updater<State, Output>.apply() {
            nextState = when (val action = this@Action) {
                is FeatureFlagChanged -> nextState.copy(
                    featureFlags = nextState.featureFlags.updateItem(action.item, action.item.copy(isEnabled = action.isEnabled))
                )
                BackClicked -> nextState.also { setOutput(Output.Back) }
            }
        }
    }

    data class Rendering(
        val state: State,
        val onAction: (Action) -> Unit
    )

    override fun initialState(props: Unit, snapshot: Snapshot?): State = State(
        emptyList()
    )

    override fun render(
        props: Unit,
        state: State,
        context: RenderContext<State, Output>
    ): Rendering {
        runSideEffects(state)

        return Rendering(
            state = state,
            onAction = { context.actionSink.send(it) }
        )
    }

    override fun snapshotState(state: State): Snapshot = Snapshot.EMPTY

    private fun runSideEffects(state: State) {
        state.featureFlags.forEach {
/*            if (it.id == KEY_SMALL_TRACKER_LIST_ITEM && it.isEnabled != featureFlagsManager.isSmallTrackerListItemEnabled()) {
                featureFlagsManager.setSmallTrackerListItemEnabled(it.isEnabled)
            }*/
        }
    }
}