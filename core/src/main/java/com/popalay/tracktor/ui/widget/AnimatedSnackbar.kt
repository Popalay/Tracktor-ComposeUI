package com.popalay.tracktor.ui.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Text
import androidx.compose.material.Snackbar
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedSnackbar(
    message: String,
    actionText: String,
    shouldDisplay: Boolean,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = shouldDisplay,
        enter = fadeIn() + expandHorizontally(
            initialWidth = { it / 2 },
            expandFrom = Alignment.CenterHorizontally
        ),
        exit = fadeOut() + shrinkHorizontally(
            shrinkTowards = Alignment.CenterHorizontally
        )
    ) {
        Snackbar(
            text = { Text(text = message.takeIf { shouldDisplay } ?: "") },
            action = {
                TextButton(onClick = onActionClick) {
                    Text(text = actionText)
                }
            },
            modifier = modifier
        )
    }
}