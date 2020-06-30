package com.popalay.tracktor.ui.list

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.foundation.Text
import androidx.ui.input.KeyboardType
import androidx.ui.material.AlertDialog
import androidx.ui.material.Button
import androidx.ui.material.FilledTextField

@Composable
fun UpdateTrackedValueDialog(
    onCloseRequest: () -> Unit,
    onSave: (Double) -> Unit
) {
    val newValue = state { "" }

    AlertDialog(
        onCloseRequest = onCloseRequest,
        title = { Text(text = "Track") },
        text = {
            FilledTextField(
                value = newValue.value,
                label = { Text(text = "Value") },
                keyboardType = KeyboardType.Number,
                onValueChange = {
                    newValue.value = it
                }
            )
        },
        confirmButton = {
            Button(
                enabled = newValue.value.toDoubleOrNull() != null,
                onClick = {
                    onSave(newValue.value.toDoubleOrNull() ?: 0.0)
                    onCloseRequest()
                }
            ) {
                Text(text = "Save")
            }
        }
    )
}