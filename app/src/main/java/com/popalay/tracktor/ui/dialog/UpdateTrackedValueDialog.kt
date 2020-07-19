package com.popalay.tracktor.ui.dialog

import androidx.compose.Composable
import androidx.compose.remember
import androidx.compose.state
import androidx.ui.foundation.Text
import androidx.ui.input.KeyboardType
import androidx.ui.material.AlertDialog
import androidx.ui.material.Button
import androidx.ui.material.FilledTextField
import com.popalay.tracktor.model.TrackableUnit

@Composable
fun UpdateTrackedValueDialog(
    unit: TrackableUnit,
    onCloseRequest: () -> Unit,
    onSave: (String) -> Unit
) {
    val newValue = state { "" }
    val keyboardType = if (unit == TrackableUnit.Word) KeyboardType.Text else KeyboardType.Number
    val validator: (String) -> Boolean = remember(unit) {
        when (unit) {
            TrackableUnit.Word -> {
                { it.isNotBlank() }
            }
            else -> {
                { it.toDoubleOrNull() != null }
            }
        }
    }

    AlertDialog(
        onCloseRequest = onCloseRequest,
        title = { Text(text = "Track") },
        text = {
            FilledTextField(
                value = newValue.value,
                label = { Text(text = "Value") },
                keyboardType = keyboardType,
                onValueChange = { newValue.value = it }
            )
        },
        confirmButton = {
            Button(
                text = { Text(text = "Save") },
                enabled = validator(newValue.value),
                onClick = { onSave(newValue.value.trim()) }
            )
        }
    )
}