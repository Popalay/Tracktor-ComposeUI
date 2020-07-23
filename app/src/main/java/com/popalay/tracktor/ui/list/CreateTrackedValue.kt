package com.popalay.tracktor.ui.list

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.layout.InnerPadding
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.padding
import androidx.ui.layout.preferredSize
import androidx.ui.layout.width
import androidx.ui.material.Button
import androidx.ui.material.FilledTextField
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Done
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp

@Preview
@Composable
fun CreateTrackedValue(
    title: String = "",
    onValueChange: (String) -> Unit = {},
    onSubmit: () -> Unit = {}
) {
    Row(
        modifier = Modifier.padding(InnerPadding(16.dp)),
        verticalGravity = Alignment.CenterVertically
    ) {
        FilledTextField(
            value = title,
            onValueChange = onValueChange,
            modifier = Modifier.weight(3F),
            label = { Text(text = "Create new tracker") },
            textStyle = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.onPrimary),
            inactiveColor = MaterialTheme.colors.onPrimary,
            activeColor = MaterialTheme.colors.onPrimary
        )
        if (title.isNotBlank()) {
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                modifier = Modifier.preferredSize(48.dp),
                backgroundColor = MaterialTheme.colors.secondary,
                shape = CircleShape,
                onClick = onSubmit
            ) { Icon(asset = Icons.Default.Done) }
        }
    }
}