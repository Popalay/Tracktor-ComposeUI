package com.popalay.tracktor

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Border
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.foundation.shape.corner.CornerSize
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.material.ripple.ripple
import androidx.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp

data class TrackedValue(
    val title: String,
    val value: String,
    val unit: TrackableUnit
)

enum class TrackableUnit(val displayName: String) {
    None(""),
    Quantity("Quantity"),
    Seconds("Seconds"),
    Kilograms("Kg"),

}

data class CreateTrackedValueViewState(
    val title: String = "",
    val value: String = "",
    val unit: TrackableUnit = TrackableUnit.None,
    val dropdownExpanded: Boolean = false
) {
    val isSubmitEnabled: Boolean
        get() = title.isNotBlank() && unit != TrackableUnit.None
}

@Preview
@Composable
fun CreateTrackedValue(onSubmit: (TrackedValue) -> Unit = {}) {
    val (currentState, updateState) = state { CreateTrackedValueViewState() }

    Card(
        color = MaterialTheme.colors.background,
        border = Border(size = 1.dp, color = MaterialTheme.colors.onBackground),
        shape = MaterialTheme.shapes.medium.copy(CornerSize(8.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Create new tracked item")
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                FilledTextField(
                    value = currentState.title,
                    onValueChange = {
                        updateState(currentState.copy(title = it))
                    },
                    modifier = Modifier.weight(3F),
                    label = { Text(text = "Title") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                DropdownMenu(
                    toggle = {
                        Text(
                            text = currentState.unit.displayName.ifBlank { "Choose Unit" },
                            textAlign = TextAlign.Center
                        )
                    },
                    toggleModifier = Modifier
                        .width(96.dp)
                        .fillMaxWidth()
                        .gravity(Alignment.CenterVertically)
                        .ripple()
                        .clickable(onClick = {
                            updateState(currentState.copy(dropdownExpanded = !currentState.dropdownExpanded))
                        }),
                    expanded = currentState.dropdownExpanded,
                    onDismissRequest = {
                        updateState(currentState.copy(dropdownExpanded = false))
                    }
                ) {
                    TrackableUnit.values().filter { it.displayName.isNotBlank() }.forEach {
                        DropDownItem(it.displayName) {
                            updateState(
                                currentState.copy(
                                    unit = it,
                                    dropdownExpanded = !currentState.dropdownExpanded
                                )
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                modifier = Modifier.gravity(Alignment.End),
                enabled = currentState.isSubmitEnabled,
                onClick = {
                    val result = TrackedValue(
                        title = currentState.title,
                        value = currentState.value,
                        unit = currentState.unit
                    )
                    onSubmit(result)
                    updateState(CreateTrackedValueViewState())
                }
            ) {
                Text(text = "Submit")
            }
        }
    }
}

@Composable
fun DropDownItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.subtitle1,
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .preferredSizeIn(
                minWidth = 112.dp,
                maxWidth = 280.dp,
                minHeight = 48.dp
            )
            .padding(8.dp)
    )
}