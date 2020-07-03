package com.popalay.tracktor.ui.list.dialog

import androidx.compose.Composable
import androidx.ui.foundation.Text
import androidx.ui.material.AlertDialog
import androidx.ui.material.ListItem
import com.popalay.tracktor.model.TrackableUnit

@Composable
fun ChooseUnitDialog(
    onCloseRequest: () -> Unit,
    onSubmit: (TrackableUnit) -> Unit
) {
    AlertDialog(
        onCloseRequest = onCloseRequest,
        title = { Text(text = "Choose unit") },
        text = {
            TrackableUnit.values()
                .filter { it != TrackableUnit.None }
                .forEach {
                    ListItem(
                        text = it.displayName,
                        onClick = {
                            onSubmit(it)
                            onCloseRequest()
                        }
                    )
                }
        },
        buttons = {}
    )
}