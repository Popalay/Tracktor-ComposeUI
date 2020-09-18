package com.popalay.tracktor.feature.createtracker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Icon
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.contentColor
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.InnerPadding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import androidx.ui.tooling.preview.PreviewParameterProvider
import com.popalay.tracktor.core.R
import com.popalay.tracktor.data.model.ProgressDirection
import com.popalay.tracktor.data.model.UnitValueType
import com.popalay.tracktor.domain.workflow.CreateTrackerWorkflow.Action
import com.popalay.tracktor.domain.workflow.CreateTrackerWorkflow.Rendering
import com.popalay.tracktor.domain.workflow.CreateTrackerWorkflow.State
import com.popalay.tracktor.success
import com.popalay.tracktor.ui.widget.Chip
import com.popalay.tracktor.ui.widget.ChipGroup
import com.popalay.tracktor.ui.widget.TopAppBar
import com.popalay.tracktor.utils.imePadding
import com.popalay.tracktor.utils.onBackPressed
import com.squareup.workflow.ui.compose.composedViewFactory

val CreateTrackerBinding = composedViewFactory<Rendering> { rendering, _ ->
    onBackPressed { rendering.onAction(Action.BackClicked) }
    CreateTrackerScreen(rendering.state, rendering.onAction)
}

class CreateTrackerStatePreviewProvider : PreviewParameterProvider<State> {
    override val values: Sequence<State>
        get() = sequenceOf(State())
}

@OptIn(ExperimentalAnimationApi::class)
@Preview
@Composable
fun CreateTrackerScreen(
    @PreviewParameter(CreateTrackerStatePreviewProvider::class) state: State,
    onAction: (Action) -> Unit = {}
) {
    Scaffold(topBar = { CreateTrackerAppBar(onAction, state) }) {
        ScrollableColumn(contentPadding = InnerPadding(top = 16.dp), modifier = Modifier.imePadding()) {
            TitleInput(state, onAction)
            AnimatedVisibility(state.isUnitsVisible) {
                Column {
                    Divider(Modifier.padding(vertical = 16.dp))
                    DirectionSelector(state, onAction)
                    Divider(Modifier.padding(vertical = 16.dp))
                    UnitSelector(state, onAction)
                    Divider(Modifier.padding(vertical = 16.dp))
                }
            }
            AnimatedVisibility(state.isCustomUnitCreating) {
                Column {
                    CustomUnitCreator(state, onAction)
                    Divider(Modifier.padding(vertical = 16.dp))
                }
            }
            AnimatedVisibility(state.isInitialValueVisible) {
                ValueInput(state, onAction)
            }
        }
    }
}

@Composable
private fun CustomUnitCreator(
    state: State,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        TextField(
            value = state.customUnit.name,
            label = { Text(stringResource(R.string.create_tracker_custom_unit_name_label)) },
            onValueChange = { onAction(Action.CustomUnitNameChanged(it)) },
            activeColor = MaterialTheme.colors.onSurface,
            backgroundColor = MaterialTheme.colors.surface,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(16.dp))
        Row {
            TextField(
                value = state.customUnit.symbol,
                label = { Text(stringResource(R.string.create_tracker_custom_unit_symbol_label)) },
                onValueChange = { onAction(Action.CustomUnitSymbolChanged(it)) },
                activeColor = MaterialTheme.colors.onSurface,
                backgroundColor = MaterialTheme.colors.surface,
                modifier = Modifier.padding(start = 16.dp).weight(2F)
            )
            Spacer(Modifier.width(16.dp))
            CustomUnitValueTypeDropDown(onAction, state)
        }
    }
}

@Composable
private fun RowScope.CustomUnitValueTypeDropDown(
    onAction: (Action) -> Unit,
    state: State,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        toggle = {
            TextButton(
                onClick = { onAction(Action.CustomUnitValueTypeClicked) },
                contentColor = contentColor(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    state.customUnit.valueType.displayName.ifBlank { stringResource(R.string.create_tracker_custom_unit_type_label) },
                    textAlign = TextAlign.Center
                )
            }
        },
        expanded = state.isCustomUnitValueTypeDropdownShown,
        toggleModifier = Modifier.padding(end = 16.dp).gravity(Alignment.CenterVertically).weight(1F),
        onDismissRequest = { onAction(Action.CustomUnitValueTypeDropdownDismissed) },
        dropdownModifier = modifier
    ) {
        UnitValueType.values().drop(1).forEach {
            DropdownMenuItem(onClick = { onAction(Action.CustomUnitValueTypeSelected(it)) }) {
                Text(it.displayName)
            }
        }
    }
}

@Composable
private fun CreateTrackerAppBar(
    onAction: (Action) -> Unit,
    state: State,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { onAction(Action.BackClicked) }) {
                Icon(Icons.Default.ArrowBack)
            }
        },
        title = { Text(stringResource(R.string.create_tracker_title)) },
        actions = {
            TextButton(
                enabled = state.isValidToSave,
                contentColor = contentColor(),
                disabledContentColor = EmphasisAmbient.current.disabled.applyEmphasis(contentColor()),
                onClick = { onAction(Action.SaveClicked) }
            ) {
                Text(stringResource(R.string.button_save))
            }
        },
        modifier = modifier
    )
}

@Composable
private fun TitleInput(
    state: State,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = state.title,
        label = { Text(stringResource(R.string.create_tracker_name_label)) },
        onValueChange = { onAction(Action.TitleChanged(it)) },
        activeColor = MaterialTheme.colors.onSurface,
        backgroundColor = MaterialTheme.colors.surface,
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)
    )
}

@Composable
private fun UnitSelector(
    state: State,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier
) {
    val rootView = ViewAmbient.current
    Column(modifier) {
        Text(
            stringResource(R.string.create_tracker_unit_message),
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        )
        ChipGroup {
            state.units.forEach { unit ->
                Chip(
                    isSelected = unit == state.selectedUnit,
                    onClick = {
                        onAction(Action.UnitSelected(unit))
                        rootView.clearFocus()
                    }
                ) {
                    Text(unit.displayName)
                }
            }
            Chip(
                isSelected = state.isCustomUnitCreating,
                bordered = !state.isCustomUnitValid,
                contentColor = MaterialTheme.colors.onBackground,
                activeColor = if (state.isCustomUnitValid) MaterialTheme.colors.success else MaterialTheme.colors.secondary,
                onClick = { if (state.isCustomUnitValid) onAction(Action.CustomUnitCreated) else onAction(Action.AddCustomUnitClicked) }
            ) {
                Icon(if (state.isCustomUnitValid) Icons.Default.Done else Icons.Default.Add)
            }
        }
    }
}

@Composable
private fun DirectionSelector(
    state: State,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            stringResource(R.string.create_tracker_progress_direction_message),
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        )
        ChipGroup {
            ProgressDirection.values().forEach { unit ->
                Chip(
                    isSelected = unit == state.selectedProgressDirection,
                    onClick = { onAction(Action.ProgressDirectionSelected(unit)) }
                ) {
                    Text(unit.displayName)
                }
            }
        }
    }
}

@Composable
private fun ValueInput(
    state: State,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = state.initialValue,
        label = { Text(stringResource(R.string.create_tracker_value_label)) },
        onValueChange = { onAction(Action.ValueChanged(it)) },
        keyboardType = KeyboardType.valueOf(state.initialValueKeyboardType),
        activeColor = MaterialTheme.colors.onSurface,
        backgroundColor = MaterialTheme.colors.surface,
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)
    )
}
