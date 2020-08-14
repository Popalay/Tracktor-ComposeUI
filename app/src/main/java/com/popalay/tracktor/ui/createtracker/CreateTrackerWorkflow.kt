package com.popalay.tracktor.ui.createtracker

import androidx.compose.ui.text.input.KeyboardType
import com.popalay.tracktor.data.TrackingRepository
import com.popalay.tracktor.domain.worker.GetAllUnitsWorker
import com.popalay.tracktor.model.ProgressDirection
import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.model.UnitValueType
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
import java.util.Locale
import java.util.UUID

class CreateTrackerWorkflow(
    private val trackingRepository: TrackingRepository,
    private val getAllUnitsWorker: GetAllUnitsWorker,
    private val moshi: Moshi
) : StatefulWorkflow<Unit, CreateTrackerWorkflow.State, CreateTrackerWorkflow.Output, CreateTrackerWorkflow.Rendering>() {

    @JsonClass(generateAdapter = true)
    data class State(
        val title: String = "",
        val selectedUnit: TrackableUnit = TrackableUnit.None,
        val selectedProgressDirection: ProgressDirection = ProgressDirection.ASCENDING,
        val initialValue: String = "",
        val isUnitsVisible: Boolean = false,
        val isInitialValueVisible: Boolean = false,
        val isCustomUnitCreating: Boolean = false,
        val customUnit: TrackableUnit = TrackableUnit.None,
        val isCustomUnitValueTypeDropdownShown: Boolean = false,
        val isCustomUnitValid: Boolean = false,
        @Transient val units: List<TrackableUnit> = emptyList(),
        @Transient val initialValueKeyboardType: KeyboardType = KeyboardType.Number,
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
        data class ProgressDirectionSelected(val direction: ProgressDirection) : Action()
        data class ValueChanged(val value: String) : Action()
        data class UnitsUpdated(val units: List<TrackableUnit>) : Action()
        data class CustomUnitChanged(val customUnit: TrackableUnit) : Action()
        data class CustomUnitValueTypeSelected(val valueType: UnitValueType) : Action()
        data class CustomUnitNameChanged(val name: String) : Action()
        data class CustomUnitSymbolChanged(val symbol: String) : Action()
        object CustomUnitCreated : Action()
        object SaveClicked : Action()
        object TrackerSaved : Action()
        object BackClicked : Action()
        object AddCustomUnitClicked : Action()
        object CustomUnitValueTypeClicked : Action()
        object CustomUnitValueTypeDropdownDismissed : Action()

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
                    selectedProgressDirection = if (action.unit == TrackableUnit.Word) ProgressDirection.ASCENDING else nextState.selectedProgressDirection,
                    initialValueKeyboardType = if (action.unit == TrackableUnit.Word) KeyboardType.Text else KeyboardType.Number,
                    isInitialValueVisible = nextState.title.isNotBlank() && action.unit != TrackableUnit.None,
                    isCustomUnitCreating = false,
                    customUnit = TrackableUnit.None
                )
                is ProgressDirectionSelected -> nextState.copy(selectedProgressDirection = action.direction)
                is ValueChanged -> nextState.copy(initialValue = action.value)
                is UnitsUpdated -> nextState.copy(units = action.units)
                CustomUnitCreated -> nextState.copy(
                    units = nextState.units.plus(
                        nextState.customUnit.copy(name = nextState.customUnit.name.trim(), symbol = nextState.customUnit.symbol.trim())
                    ),
                    isCustomUnitValid = false,
                    isCustomUnitCreating = false,
                    customUnit = TrackableUnit.None
                )
                is CustomUnitChanged -> nextState.copy(customUnit = action.customUnit)
                is CustomUnitValueTypeSelected -> {
                    val customUnit = nextState.customUnit.copy(valueType = action.valueType)
                    nextState.copy(
                        customUnit = customUnit,
                        isCustomUnitValid = customUnit.isValid(),
                        isCustomUnitValueTypeDropdownShown = false
                    )
                }
                is CustomUnitNameChanged -> {
                    val customUnit = nextState.customUnit.copy(name = action.name)
                    nextState.copy(customUnit = customUnit, isCustomUnitValid = customUnit.isValid())
                }
                is CustomUnitSymbolChanged -> {
                    val customUnit = nextState.customUnit.copy(symbol = action.symbol)
                    nextState.copy(customUnit = customUnit, isCustomUnitValid = customUnit.isValid())
                }
                AddCustomUnitClicked -> nextState.copy(
                    isCustomUnitCreating = true,
                    selectedUnit = TrackableUnit.None,
                    initialValue = "",
                    isInitialValueVisible = false
                )
                CustomUnitValueTypeClicked -> nextState.copy(isCustomUnitValueTypeDropdownShown = !nextState.isCustomUnitValueTypeDropdownShown)
                CustomUnitValueTypeDropdownDismissed -> nextState.copy(isCustomUnitValueTypeDropdownShown = false)
                TrackerSaved -> nextState.also { setOutput(Output.Back) }
                BackClicked -> nextState.also { setOutput(Output.Back) }
                else -> nextState.copy(currentAction = this@Action)
            }
        }

        private fun TrackableUnit.isValid() = name.isNotBlank() && symbol.isNotBlank() && valueType != UnitValueType.NONE
    }

    data class Rendering(
        val state: State,
        val onAction: (Action) -> Unit
    )

    override fun initialState(props: Unit, snapshot: Snapshot?): State = snapshot?.toData(moshi) ?: State()

    override fun render(
        props: Unit,
        state: State,
        context: RenderContext<State, Output>
    ): Rendering {
        context.runningWorker(getAllUnitsWorker) { Action.UnitsUpdated(it) }
        runSideEffects(state, context)

        return Rendering(
            state = state,
            onAction = { context.actionSink.send(it) }
        )
    }

    override fun snapshotState(state: State): Snapshot = state.toSnapshot(moshi)

    private fun runSideEffects(state: State, context: RenderContext<State, Output>) {
        when (state.currentAction) {
            is Action.SaveClicked -> {
                val worker = Worker.from {
                    val tracker = Tracker(
                        id = UUID.randomUUID().toString(),
                        title = state.title.trim().capitalize(Locale.getDefault()),
                        unit = state.selectedUnit,
                        direction = state.selectedProgressDirection,
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