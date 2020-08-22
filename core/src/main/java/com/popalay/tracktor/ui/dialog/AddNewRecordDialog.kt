package com.popalay.tracktor.ui.dialog

import androidx.compose.foundation.Text
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.popalay.tracktor.core.R
import com.popalay.tracktor.data.model.TrackableUnit
import com.popalay.tracktor.data.model.Tracker
import com.popalay.tracktor.utils.rememberMutableState

@Composable
fun AddNewRecordDialog(
    tracker: Tracker,
    onDismissRequest: () -> Unit,
    onSave: (String) -> Unit
) {
    var newValue by rememberMutableState { "" }
    val keyboardType = if (tracker.unit == TrackableUnit.Word) KeyboardType.Text else KeyboardType.Number
    val validator: (String) -> Boolean = remember(tracker.unit) {
        when (tracker.unit) {
            TrackableUnit.Word -> {
                { it.isNotBlank() }
            }
            else -> {
                { it.toDoubleOrNull() != null }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.add_new_record_title, tracker.title)) },
        text = {
            TextField(
                value = newValue,
                label = { Text(tracker.unit.displayName) },
                keyboardType = keyboardType,
                activeColor = MaterialTheme.colors.onSurface,
                onValueChange = { newValue = it }
            )
        },
        confirmButton = {
            Button(
                enabled = validator(newValue),
                onClick = { onSave(newValue.trim()) }
            ) { Text(stringResource(R.string.button_save)) }
        }
    )
}