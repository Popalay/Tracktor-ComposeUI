package com.popalay.tracktor.ui.createtracker

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.core.ViewAmbient
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.contentColor
import androidx.ui.layout.Column
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.material.Divider
import androidx.ui.material.EmphasisAmbient
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
        Column(modifier = Modifier.padding(top = 16.dp)) {
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
