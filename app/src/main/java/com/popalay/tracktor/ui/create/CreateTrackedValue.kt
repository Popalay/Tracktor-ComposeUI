package com.popalay.tracktor.ui.create

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.layout.ColumnScope.gravity
import androidx.ui.layout.InnerPadding
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.padding
import androidx.ui.layout.width
import androidx.ui.material.Button
import androidx.ui.material.FilledTextField
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp

@Preview
@Composable
fun CreateTrackedValue(onSubmit: (String) -> Unit = {}) {
    val (currentState, updateState) = state { "" }

    Surface(
        color = MaterialTheme.colors.surface,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(InnerPadding(16.dp))
        ) {
            FilledTextField(
                value = currentState,
                onValueChange = { updateState(it) },
                modifier = Modifier.weight(3F),
                label = { Text(text = "Create new tracker") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                text = { Text(text = "Submit") },
                modifier = Modifier.gravity(Alignment.CenterHorizontally),
                backgroundColor = MaterialTheme.colors.secondary,
                enabled = currentState.isNotBlank(),
                onClick = {
                    onSubmit(currentState)
                    updateState("")
                }
            )
        }
    }
}