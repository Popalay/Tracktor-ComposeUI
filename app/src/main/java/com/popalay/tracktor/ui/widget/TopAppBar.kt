package com.popalay.tracktor.ui.widget

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.layout.Column
import androidx.ui.layout.ColumnScope
import androidx.ui.layout.Row
import androidx.ui.layout.RowScope
import androidx.ui.unit.dp

val DefaultTopAppBarHeight = 56.dp

@Composable
fun TopAppBar(
    title: @Composable RowScope.() -> Unit,
    navigationIcon: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    androidx.ui.material.TopAppBar(modifier = modifier) {
        Column(modifier = contentModifier) {
            Row {
                navigationIcon()
                title()
            }
            content()
        }
    }
}