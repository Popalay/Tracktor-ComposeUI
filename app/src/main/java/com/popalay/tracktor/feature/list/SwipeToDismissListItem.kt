package com.popalay.tracktor.feature.list

import androidx.compose.animation.animate
import androidx.compose.animation.asDisposableClock
import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.Icon
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.drawLayer
import androidx.compose.ui.platform.AnimationClockAmbient
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDismissListItem(
    state: Any,
    onDismissedToEnd: () -> Unit = {},
    onDismissedToStart: () -> Unit = {},
    itemContent: @Composable () -> Unit
) {
    val clock = AnimationClockAmbient.current.asDisposableClock()
    val dismissState = remember(state, clock) {
        DismissState(
            DismissValue.Default,
            clock
        ) {
            when (it) {
                DismissValue.DismissedToEnd -> onDismissedToEnd()
                DismissValue.DismissedToStart -> onDismissedToStart()
                DismissValue.Default -> Unit
            }
            true
        }
    }
    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
        dismissThresholds = { direction ->
            FractionalThreshold(if (direction == DismissDirection.StartToEnd) 0.25f else 0.5f)
        },
        background = { SwipeToDismissBackground(dismissState) },
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        itemContent()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDismissBackground(dismissState: DismissState) {
    val direction = dismissState.dismissDirection ?: return
    val (gravity, icon) = when (direction) {
        DismissDirection.StartToEnd -> ContentGravity.CenterStart to Icons.Default.Add
        DismissDirection.EndToStart -> ContentGravity.CenterEnd to Icons.Default.Delete
    }
    val scale = animate(if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f)

    Box(
        modifier = Modifier.fillMaxSize(),
        paddingStart = 24.dp,
        paddingEnd = 24.dp,
        gravity = gravity
    ) {
        Icon(icon, modifier = Modifier.drawLayer(scaleX = scale, scaleY = scale))
    }
}