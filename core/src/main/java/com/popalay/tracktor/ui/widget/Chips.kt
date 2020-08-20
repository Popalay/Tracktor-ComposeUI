package com.popalay.tracktor.ui.widget

import androidx.compose.animation.animate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentColorAmbient
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.MainAxisAlignment
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.SizeMode
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayout::class)
@Composable
fun ChipGroup(
    children: @Composable () -> Unit
) {
    FlowRow(
        mainAxisSize = SizeMode.Expand,
        mainAxisSpacing = 16.dp,
        crossAxisSpacing = 8.dp,
        mainAxisAlignment = MainAxisAlignment.Center
    ) {
        children()
    }
}

@Composable
fun Chip(
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    shape: Shape = CircleShape,
    activeColor: Color = MaterialTheme.colors.secondary,
    inactiveColor: Color = EmphasisAmbient.current.disabled.applyEmphasis(activeColor),
    contentColor: Color = MaterialTheme.colors.onSecondary,
    bordered: Boolean = false,
    content: @Composable RowScope.() -> Unit
) {
    val color = animate(if (isSelected) activeColor else inactiveColor)

    Box(
        shape = shape,
        backgroundColor = if (bordered) Color.Transparent else color,
        border = if (bordered) BorderStroke(2.dp, color) else null,
        paddingBottom = 4.dp,
        paddingTop = 4.dp,
        paddingStart = 16.dp,
        paddingEnd = 16.dp,
        gravity = Alignment.Center,
        modifier = Modifier.clip(shape).clickable(onClick = onClick)
    ) {
        Providers(ContentColorAmbient provides contentColor) {
            Row {
                content()
            }
        }
    }
}