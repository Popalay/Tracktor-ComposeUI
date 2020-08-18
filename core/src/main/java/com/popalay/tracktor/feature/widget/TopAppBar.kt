package com.popalay.tracktor.feature.widget

import androidx.compose.foundation.ContentColorAmbient
import androidx.compose.foundation.ProvideTextStyle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.contentColorFor
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.popalay.tracktor.utils.statusBarHeight

private val TopAppBarElevation = 4.dp
private val AppBarHorizontalPadding = 4.dp
private val AppBarVerticalPadding = 4.dp

@Composable
fun TopAppBar(
    title: @Composable RowScope.() -> Unit,
    navigationIcon: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = TopAppBarElevation,
    actions: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        elevation = elevation,
        shape = RectangleShape,
        modifier = modifier
    ) {
        Column(
            modifier = modifier.fillMaxWidth()
                .padding(horizontal = AppBarHorizontalPadding, vertical = AppBarVerticalPadding)
        ) {
            Spacer(Modifier.statusBarHeight())
            Row(verticalGravity = Alignment.CenterVertically) {
                Providers(ContentColorAmbient provides MaterialTheme.colors.onPrimary) {
                    navigationIcon()
                    ProvideTextStyle(value = MaterialTheme.typography.h6) {
                        title()
                    }
                    Spacer(modifier = Modifier.weight(1F))
                    actions()
                }
            }
            content()
        }
    }
}