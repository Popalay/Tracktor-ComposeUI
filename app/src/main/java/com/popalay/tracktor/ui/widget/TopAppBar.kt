package com.popalay.tracktor.ui.widget

import androidx.compose.Composable
import androidx.compose.Providers
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.ContentColorAmbient
import androidx.ui.foundation.ProvideTextStyle
import androidx.ui.layout.Column
import androidx.ui.layout.ColumnScope
import androidx.ui.layout.Row
import androidx.ui.layout.RowScope
import androidx.ui.layout.Spacer
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp

val DefaultTopAppBarHeight = 56.dp

@Composable
fun TopAppBar(
    title: @Composable RowScope.() -> Unit,
    navigationIcon: @Composable RowScope.() -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    androidx.ui.material.TopAppBar(modifier = modifier) {
        Column(modifier = contentModifier) {
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