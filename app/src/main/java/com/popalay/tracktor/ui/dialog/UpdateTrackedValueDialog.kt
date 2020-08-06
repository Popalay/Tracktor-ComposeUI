package com.popalay.tracktor.ui.dialog

import androidx.compose.foundation.Text
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.state
import androidx.compose.ui.text.input.KeyboardType
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
            TextField(
                value = newValue.value,
                label = { Text(text = unit.displayName) },
                keyboardType = keyboardType,
                activeColor = MaterialTheme.colors.onSurface,
                onValueChange = { newValue.value = it }
            )
        },
        confirmButton = {
            Button(
                enabled = validator(newValue.value),
                onClick = { onSave(newValue.value.trim()) }
            ) { Text(text = "Save") }
        }
    )
}