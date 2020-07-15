package com.popalay.tracktor.ui.dialog

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.layout.padding
import androidx.ui.material.ListItem
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.ui.widget.BottomSheetDialog

@Composable
fun ChooseUnitDialog(
    onCloseRequest: () -> Unit,
    onSubmit: (TrackableUnit) -> Unit
) {
    BottomSheetDialog(onCloseRequest = onCloseRequest) {
        Text(
            "Choose unit",
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(16.dp)
        )
        TrackableUnit.values()
            .filter { it != TrackableUnit.None }
            .forEach {
                ListItem(
                    text = { Text(it.displayName, color = MaterialTheme.colors.onBackground) },
                    onClick = { onSubmit(it) }
                )
            }
    }
}