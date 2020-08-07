package com.popalay.tracktor.ui.createtracker

import androidx.compose.foundation.Icon
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.contentColor
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import androidx.ui.tooling.preview.PreviewParameterProvider
import com.popalay.tracktor.WindowInsetsAmbient
import com.popalay.tracktor.model.ProgressDirection
import com.popalay.tracktor.model.UnitValueType
import com.popalay.tracktor.success
import com.popalay.tracktor.ui.createtracker.CreateTrackerWorkflow.Action
import com.popalay.tracktor.ui.widget.Chip
import com.popalay.tracktor.ui.widget.ChipGroup
import com.popalay.tracktor.ui.widget.DefaultTopAppBarHeight
import com.popalay.tracktor.ui.widget.TopAppBar
import com.popalay.tracktor.utils.onBackPressed
import com.squareup.workflow.ui.compose.composedViewFactory

val CreateTrackerBinding = composedViewFactory<CreateTrackerWorkflow.Rendering> { rendering, _ ->
    onBackPressed { rendering.onAction(Action.BackClicked) }
    CreateTrackerScreen(rendering.state, rendering.onAction)
}

class CreateTrackerStatePreviewProvider : PreviewParameterProvider<CreateTrackerWorkflow.State> {
    override val values: Sequence<CreateTrackerWorkflow.State>
        get() = sequenceOf(CreateTrackerWorkflow.State())
}

@Preview
@Composable
fun CreateTrackerScreen(
    @PreviewParameter(CreateTrackerStatePreviewProvider::class) state: CreateTrackerWorkflow.State,
    onAction: (Action) -> Unit = {}
) {
    Scaffold(topBar = { CreateTrackerAppBar(onAction, state) }) {
        ScrollableColumn(modifier = Modifier.padding(top = 16.dp)) {
            TitleInput(state, onAction)
            if (state.isUnitsVisible) {
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                DirectionSelector(state, onAction)
                Divider(modifier = Modifier.padding(vertical = 16.dp))
                UnitSelector(state, onAction)
                Divider(modifier = Modifier.padding(vertical = 16.dp))
            }
            if (state.isCustomUnitCreating) {
                CustomUnitCreator(state, onAction)
                Divider(modifier = Modifier.padding(vertical = 16.dp))
            }
            if (state.isInitialValueVisible) {
                ValueInput(state, onAction)
            }
        }
    }
}

@Composable
private fun CustomUnitCreator(
    state: CreateTrackerWorkflow.State,
    onAction: (Action) -> Unit
) {
    TextField(
        value = state.customUnit.name,
        label = { Text("Unit name") },
        onValueChange = { onAction(Action.CustomUnitNameChanged(it)) },
        activeColor = MaterialTheme.colors.onSurface,
        backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    )
    Spacer(modifier = Modifier.height(16.dp))
    Row {
        TextField(
            value = state.customUnit.symbol,
            label = { Text("Unit symbol") },
            onValueChange = { onAction(Action.CustomUnitSymbolChanged(it)) },
            activeColor = MaterialTheme.colors.onSurface,
            backgroundColor = MaterialTheme.colors.surface,
            modifier = Modifier.padding(start = 16.dp).weight(2F)
        )
        Spacer(modifier = Modifier.width(16.dp))
        CustomUnitValueTypeDropDown(onAction, state)
    }
}

@Composable
private fun RowScope.CustomUnitValueTypeDropDown(
    onAction: (Action) -> Unit,
    state: CreateTrackerWorkflow.State
) {
    DropdownMenu(
        toggle = {
            TextButton(
                onClick = { onAction(Action.CustomUnitValueTypeClicked) },
                contentColor = contentColor(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(state.customUnit.valueType.displayName.ifBlank { "Click to select" }, textAlign = TextAlign.Center)
            }
        },
        expanded = state.isCustomUnitValueTypeDropdownShown,
        toggleModifier = Modifier.padding(end = 16.dp).gravity(Alignment.CenterVertically).weight(1F),
        onDismissRequest = { onAction(Action.CustomUnitValueTypeDropdownDismissed) }
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
    state: CreateTrackerWorkflow.State
) {
    val insets = WindowInsetsAmbient.current
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { onAction(Action.BackClicked) }) {
                Icon(Icons.Default.ArrowBack)
            }
        },
        title = { Text(text = "Let's track") },
        actions = {
            TextButton(
                enabled = state.isValidToSave,
                contentColor = contentColor(),
                disabledContentColor = EmphasisAmbient.current.disabled.applyEmphasis(contentColor()),
                onClick = { onAction(Action.SaveClicked) }
            ) {
                Text(text = "SAVE")
            }
        },
        modifier = Modifier.preferredHeight(insets.top + DefaultTopAppBarHeight),
        contentModifier = Modifier.padding(top = insets.top)
    )
}

@Composable
private fun TitleInput(
    state: CreateTrackerWorkflow.State,
    onAction: (Action) -> Unit
) {
    TextField(
        value = state.title,
        label = { Text("What would you like to track?") },
        onValueChange = { onAction(Action.TitleChanged(it)) },
        activeColor = MaterialTheme.colors.onSurface,
        backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    )
}

@Composable
private fun UnitSelector(
    state: CreateTrackerWorkflow.State,
    onAction: (Action) -> Unit
) {
    val rootView = ViewAmbient.current
    Text(
        "Choose preferred unit or create your own:",
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

@Composable
private fun DirectionSelector(
    state: CreateTrackerWorkflow.State,
    onAction: (Action) -> Unit
) {
    Text(
        "Progress direction, define value growth direction:",
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

@Composable
private fun ValueInput(
    state: CreateTrackerWorkflow.State,
    onAction: (Action) -> Unit
) {
    TextField(
        value = state.initialValue,
        label = { Text("What's your initial value?") },
        onValueChange = { onAction(Action.ValueChanged(it)) },
        keyboardType = state.initialValueKeyboardType,
        activeColor = MaterialTheme.colors.onSurface,
        backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    )
}
