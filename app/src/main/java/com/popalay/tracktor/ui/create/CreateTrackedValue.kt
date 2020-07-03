package com.popalay.tracktor.ui.create

import androidx.compose.Composable
import androidx.compose.state
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
import androidx.ui.material.Surface
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Add
import androidx.ui.material.primarySurface
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp

@Preview
@Composable
fun CreateTrackedValue(onSubmit: (String) -> Unit = {}) {
    val (currentState, updateState) = state { "" }

    Surface(
        color = MaterialTheme.colors.primarySurface,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(InnerPadding(16.dp))
        ) {
            FilledTextField(
                value = currentState,
                onValueChange = { updateState(it) },
                modifier = Modifier.weight(3F),
                label = { Text(text = "Create new tracker") },
                textStyle = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.onPrimary),
                inactiveColor = MaterialTheme.colors.onPrimary,
                activeColor = MaterialTheme.colors.onPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                text = { Icon(asset = Icons.Default.Add) },
                modifier = Modifier.gravity(Alignment.CenterVertically).preferredSize(48.dp),
                backgroundColor = MaterialTheme.colors.secondary,
                enabled = currentState.isNotBlank(),
                shape = CircleShape,
                onClick = {
                    onSubmit(currentState)
                    updateState("")
                }
            )
        }
    }
}