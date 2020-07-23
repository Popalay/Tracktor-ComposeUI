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
            Button(onClick = { onSubmit() }) {
                Text(text = "Yes")
            }
        },
        dismissButton = {
            Button(onClick = onCloseRequest) {
                Text(text = "Cancel")
            }
        }
    )
}