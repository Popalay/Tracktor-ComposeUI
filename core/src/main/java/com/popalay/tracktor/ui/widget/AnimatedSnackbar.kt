package com.popalay.tracktor.ui.widget

import androidx.compose.animation.animate
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.height
import androidx.compose.material.Snackbar
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedSnackbar(
    message: String,
    actionText: String,
    shouldDisplay: Boolean,
    onActionClick: () -> Unit
) {
    val snackbarOffset = animate(if (shouldDisplay) 56.dp else 0.dp)
    Snackbar(
        text = { Text(text = message.takeIf { shouldDisplay } ?: "") },
        action = {
            TextButton(onClick = onActionClick) {
                Text(text = actionText)
            }
        },
        modifier = Modifier.height(snackbarOffset)
    )
}