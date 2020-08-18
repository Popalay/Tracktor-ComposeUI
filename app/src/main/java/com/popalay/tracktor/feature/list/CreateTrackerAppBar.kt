package com.popalay.tracktor.feature.list

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.popalay.tracktor.R
import com.popalay.tracktor.feature.widget.TopAppBar

@Preview
@Composable
fun LogoAppBar(onActionClick: () -> Unit = {}) {
    TopAppBar(
        title = {
            Image(
                vectorResource(R.drawable.ic_logo_text),
                modifier = Modifier.padding(start = 16.dp)
            )
        },
        actions = {
            IconButton(onClick = onActionClick) {
                Icon(Icons.Default.Settings)
            }
        }
    )
}