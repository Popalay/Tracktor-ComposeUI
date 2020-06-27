package com.popalay.tracktor.create

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Border
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.foundation.shape.corner.CornerSize
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.height
import androidx.ui.layout.padding
import androidx.ui.layout.width
import androidx.ui.material.Button
import androidx.ui.material.Card
import androidx.ui.material.FilledTextField
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.ripple
import androidx.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.model.TrackedValue

data class CreateTrackedValueViewState(
    val title: String = "",
    val unit: TrackableUnit = TrackableUnit.None
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
            CreateTrackedValueBody(
                currentState,
                updateState
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                modifier = Modifier.gravity(Alignment.End),
                enabled = currentState.isSubmitEnabled,
                onClick = {
                    val result = TrackedValue(
                        title = currentState.title,
                        value = 0.0,
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
fun CreateTrackedValueBody(
    currentState: CreateTrackedValueViewState,
    updateState: (CreateTrackedValueViewState) -> Unit
) {
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
        Text(
            text = currentState.unit.displayName.ifBlank { "Choose Unit" },
            modifier = Modifier
                .width(96.dp)
                .fillMaxWidth()
                .gravity(Alignment.CenterVertically)
                .ripple()
                .clickable(onClick = {
                    updateState(
                        currentState.copy(
                            unit = TrackableUnit.values()[(currentState.unit.ordinal + 1) % TrackableUnit.values().size]
                        )
                    )
                }),
            textAlign = TextAlign.Center
        )
    }
}