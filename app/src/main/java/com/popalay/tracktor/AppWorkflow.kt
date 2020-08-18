package com.popalay.tracktor

import com.popalay.tracktor.data.model.TrackerWithRecords
import com.popalay.tracktor.ui.createtracker.CreateTrackerWorkflow
import com.popalay.tracktor.ui.featureflagslist.FeatureFlagsListWorkflow
import com.popalay.tracktor.ui.list.ListWorkflow
import com.popalay.tracktor.ui.settings.SettingsWorkflow
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
    private val featureFlagsListWorkflow: FeatureFlagsListWorkflow,
    private val createTrackerWorkflow: CreateTrackerWorkflow,
    private val settingsWorkflow: SettingsWorkflow
) : StatefulWorkflow<Unit, AppWorkflow.State, Nothing, Any>() {

    sealed class State {
        data class TrackerList(val animate: Boolean, val itemInDeleting: TrackerWithRecords? = null) : State()
        data class TrackerDetail(val trackerId: String) : State()
        object FeatureTogglesList : State()
        object CreateTracker : State()
        object Settings : State()
    }

    sealed class Action : WorkflowAction<State, Nothing> {
        data class TrackerListOutput(val output: ListWorkflow.Output) : Action()
        data class TrackerDetailOutput(val output: TrackerDetailWorkflow.Output) : Action()
        data class FeatureTogglesListOutput(val output: FeatureFlagsListWorkflow.Output) : Action()
        data class CreateTrackerOutput(val output: CreateTrackerWorkflow.Output) : Action()
        data class SettingsOutput(val output: SettingsWorkflow.Output) : Action()

        override fun WorkflowAction.Updater<State, Nothing>.apply() {
            nextState = when (val action = this@Action) {
                is TrackerListOutput -> when (val output = action.output) {
                    is ListWorkflow.Output.TrackerDetail -> State.TrackerDetail(output.trackerId)
                    ListWorkflow.Output.CreateTracker -> State.CreateTracker
                    ListWorkflow.Output.Settings -> State.Settings
                }
                is TrackerDetailOutput -> when (val output = action.output) {
                    TrackerDetailWorkflow.Output.Back -> State.TrackerList(animate = false)
                    is TrackerDetailWorkflow.Output.TrackerDeleted -> State.TrackerList(animate = false, itemInDeleting = output.item)
                }
                is FeatureTogglesListOutput -> when (action.output) {
                    FeatureFlagsListWorkflow.Output.Back -> State.Settings
                }
                is CreateTrackerOutput -> when (action.output) {
                    CreateTrackerWorkflow.Output.Back -> State.TrackerList(animate = false)
                }
                is SettingsOutput -> when (action.output) {
                    SettingsWorkflow.Output.Back -> State.TrackerList(animate = false)
                    SettingsWorkflow.Output.FeatureTogglesList -> State.FeatureTogglesList
                }
            }
        }
    }

    override fun initialState(props: Unit, snapshot: Snapshot?): State = snapshot?.toState() ?: State.TrackerList(animate = true)

    override fun render(props: Unit, state: State, context: RenderContext<State, Nothing>): Any = when (state) {
        is State.TrackerList -> context.renderChild(
            listWorkflow,
            ListWorkflow.Props(state.animate, state.itemInDeleting)
        ) { Action.TrackerListOutput(it) }
        is State.TrackerDetail -> context.renderChild(
            trackerDetailWorkflow,
            TrackerDetailWorkflow.Props(state.trackerId)
        ) { Action.TrackerDetailOutput(it) }
        State.FeatureTogglesList -> context.renderChild(featureFlagsListWorkflow) { Action.FeatureTogglesListOutput(it) }
        State.CreateTracker -> context.renderChild(createTrackerWorkflow) { Action.CreateTrackerOutput(it) }
        State.Settings -> context.renderChild(settingsWorkflow) { Action.SettingsOutput(it) }
    }

    override fun snapshotState(state: State): Snapshot = state.toSnapshot()

    private fun State.toSnapshot() = when (this) {
        is State.TrackerList -> Snapshot.of("TrackerList,$animate")
        is State.TrackerDetail -> Snapshot.of("TrackerDetail,$trackerId")
        State.FeatureTogglesList -> Snapshot.of("FeatureFlagList")
        State.CreateTracker -> Snapshot.of("CreateTracker")
        State.Settings -> Snapshot.of("Settings")
    }

    private fun Snapshot.toState(): State? {
        val stringSnapshot = bytes.string(Charset.defaultCharset())
        return when {
            stringSnapshot.startsWith("TrackerList") -> State.TrackerList(stringSnapshot.split(",")[1].toBoolean())
            stringSnapshot.startsWith("TrackerDetail") -> State.TrackerDetail(stringSnapshot.split(",")[1])
            stringSnapshot.startsWith("FeatureFlagList") -> State.FeatureTogglesList
            stringSnapshot.startsWith("CreateTracker") -> State.CreateTracker
            stringSnapshot.startsWith("Settings") -> State.Settings
            else -> null
        }
    }
}