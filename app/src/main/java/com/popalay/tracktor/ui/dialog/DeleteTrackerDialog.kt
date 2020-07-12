package com.popalay.tracktor.ui.dialog

import androidx.compose.Composable
import androidx.ui.foundation.Text
import androidx.ui.material.AlertDialog
import androidx.ui.material.Button

@Composable
fun DeleteTrackerDialog(
    onCloseRequest: () -> Unit,
    onSubmit: () -> Unit
) {
    AlertDialog(
        onCloseRequest = onCloseRequest,
        title = { Text(text = "Delete") },
        text = { Text(text = "Are you sure to delete this tracker?") },
        confirmButton = {
            Button(
                text = { Text(text = "Yes") },
                onClick = { onSubmit() }
            )
        },
        dismissButton = {
            Button(
                text = { Text(text = "Cancel") },
                onClick = onCloseRequest
            )
        }
    )
}