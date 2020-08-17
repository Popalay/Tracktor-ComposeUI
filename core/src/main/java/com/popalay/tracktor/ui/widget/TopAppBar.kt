package com.popalay.tracktor.ui.widget

import androidx.compose.foundation.ContentColorAmbient
import androidx.compose.foundation.ProvideTextStyle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
    androidx.compose.material.TopAppBar(modifier = modifier) {
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