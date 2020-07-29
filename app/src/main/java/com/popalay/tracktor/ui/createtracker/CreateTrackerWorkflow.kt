package com.popalay.tracktor.ui.createtracker

import androidx.ui.input.KeyboardType
import com.popalay.tracktor.data.TrackingRepository
import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.utils.toData
import com.popalay.tracktor.utils.toSnapshot
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.workflow.RenderContext
import com.squareup.workflow.Snapshot
import com.squareup.workflow.StatefulWorkflow
import com.squareup.workflow.Worker
import com.squareup.workflow.WorkflowAction
import com.squareup.workflow.applyTo
import java.time.LocalDateTime
import java.util.UUID

class CreateTrackerWorkflow(
    private val trackingRepository: TrackingRepository,
    private val moshi: Moshi
) : StatefulWorkflow<Unit, CreateTrackerWorkflow.State, CreateTrackerWorkflow.Output, CreateTrackerWorkflow.Rendering>() {

    @JsonClass(generateAdapter = true)
    data class State(
        val title: String = "",
        val units: List<TrackableUnit> = emptyList(),
        val selectedUnit: TrackableUnit = TrackableUnit.None,
        val initialValue: String = "",
        val initialValueKeyboardType: KeyboardType = KeyboardType.Number,
        val isUnitsVisible: Boolean = false,
        val isInitialValueVisible: Boolean = false,
        @Transient val currentAction: Action? = null
    ) {
        val isValidToSave: Boolean
            get() = title.isNotBlank() && selectedUnit != TrackableUnit.None &&
                    (initialValue.toDoubleOrNull() != null && selectedUnit != TrackableUnit.Word ||
                            initialValue.isNotBlank() && selectedUnit == TrackableUnit.Word)
    }

    sealed class Output {
        object Back : Output()
    }

    sealed class Action : WorkflowAction<State, Output> {
        data class SideEffectAction(val action: Action) : Action()
        data class TitleChanged(val title: String) : Action()
        data class UnitSelected(val unit: TrackableUnit) : Action()
        data class ValueChanged(val value: String) : Action()
        object SaveClicked : Action()
        object TrackerSaved : Action()
        object BackClicked : Action()

        override fun WorkflowAction.Updater<State, Output>.apply() {
            nextState = when (val action = this@Action) {
                is SideEffectAction -> action.action.applyTo(nextState.copy(currentAction = null)).let { result ->
                    result.second?.also { setOutput(it) }
                    result.first
                }
                is TitleChanged -> nextState.copy(
                    title = action.title,
                    isUnitsVisible = action.title.isNotBlank(),
                    isInitialValueVisible = action.title.isNotBlank() && nextState.selectedUnit != TrackableUnit.None,
                    initialValue = if (action.title.isBlank()) "" else nextState.initialValue,
                    selectedUnit = if (action.title.isBlank()) TrackableUnit.None else nextState.selectedUnit
                )
                is UnitSelected -> nextState.copy(
                    selectedUnit = action.unit,
                    initialValueKeyboardType = if (action.unit == TrackableUnit.Word) KeyboardType.Text else KeyboardType.Number,
                    isInitialValueVisible = nextState.title.isNotBlank() && action.unit != TrackableUnit.None
                )
                is ValueChanged -> nextState.copy(
                    initialValue = action.value
                )
                TrackerSaved -> nextState.also { setOutput(Output.Back) }
                BackClicked -> nextState.also { setOutput(Output.Back) }
                else -> nextState.copy(currentAction = this@Action)
            }
        }
    }

    data class Rendering(
        val state: State,
        val onAction: (Action) -> Unit
    )

    override fun initialState(props: Unit, snapshot: Snapshot?): State = snapshot?.toData(moshi)
        ?: State(units = TrackableUnit.values().drop(1))

    override fun render(
        props: Unit,
        state: State,
        context: RenderContext<State, Output>
    ): Rendering {
        runSideEffects(state, context)

        return Rendering(
            state = state,
            onAction = { context.actionSink.send(it) }
        )
    }

    override fun snapshotState(state: State): Snapshot = state.toSnapshot(moshi)

    private fun runSideEffects(state: State, context: RenderContext<State, Output>) {
        when (val action = state.currentAction) {
            is Action.SaveClicked -> {
                val worker = Worker.from {
                    val tracker = Tracker(
                        id = UUID.randomUUID().toString(),
                        title = state.title.trim(),
                        unit = state.selectedUnit,
                        date = LocalDateTime.now()
                    )
                    trackingRepository.saveTracker(tracker)
                    if (tracker.unit == TrackableUnit.Word) {
                        trackingRepository.saveRecord(tracker, state.initialValue)
                    } else {
                        trackingRepository.saveRecord(tracker, state.initialValue.toDoubleOrNull() ?: 0.0)
                    }
                }
                context.runningWorker(worker) { Action.SideEffectAction(Action.TrackerSaved) }
            }
        }
    }
}