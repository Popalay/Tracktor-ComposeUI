package com.popalay.tracktor.ui.createtracker

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.core.ViewAmbient
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.height
import androidx.ui.layout.padding
import androidx.ui.material.Divider
import androidx.ui.material.FilledTextField
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Scaffold
import androidx.ui.material.TextButton
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowBack
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import androidx.ui.tooling.preview.PreviewParameterProvider
import androidx.ui.unit.dp
import com.popalay.tracktor.WindowInsetsAmbient
import com.popalay.tracktor.ui.createtracker.CreateTrackerWorkflow.Action
import com.popalay.tracktor.ui.widget.Chip
import com.popalay.tracktor.ui.widget.ChipGroup
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
    Scaffold {
        Column {
            Spacer(modifier = Modifier.height(WindowInsetsAmbient.current.top))
            TopAppBar(onAction, state)

            Title()
            Spacer(modifier = Modifier.height(16.dp))
            TitleInput(state, onAction)
            if (state.isUnitsVisible) {
                UnitSelector(state, onAction)
            }
            if (state.isInitialValueVisible) {
                ValueInput(state, onAction)
            }
        }
    }
}

@Composable
private fun TopAppBar(
    onAction: (Action) -> Unit,
    state: CreateTrackerWorkflow.State
) {
    Row(verticalGravity = Alignment.CenterVertically) {
        IconButton(onClick = { onAction(Action.BackClicked) }) {
            Icon(Icons.Default.ArrowBack)
        }
        Spacer(modifier = Modifier.weight(1F))
        TextButton(
            enabled = state.isValidToSave,
            contentColor = MaterialTheme.colors.secondary,
            onClick = { onAction(Action.SaveClicked) }
        ) {
            Text(text = "SAVE")
        }
    }
}

@Composable
private fun Title() {
    Text(
        text = "Lets track!",
        style = MaterialTheme.typography.h3,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    )
}

@Composable
private fun TitleInput(
    state: CreateTrackerWorkflow.State,
    onAction: (Action) -> Unit
) {
    FilledTextField(
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
    Divider(modifier = Modifier.padding(vertical = 16.dp))
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
    }
    Divider(modifier = Modifier.padding(vertical = 16.dp))
}

@Composable
private fun ValueInput(
    state: CreateTrackerWorkflow.State,
    onAction: (Action) -> Unit
) {
    FilledTextField(
        value = state.initialValue,
        label = { Text("What's your initial value?") },
        onValueChange = { onAction(Action.ValueChanged(it)) },
        keyboardType = state.initialValueKeyboardType,
        activeColor = MaterialTheme.colors.onSurface,
        backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    )
}
