package com.popalay.tracktor.feature.settings

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.FeaturedPlayList
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.popalay.tracktor.core.R
import com.popalay.tracktor.feature.settings.SettingsWorkflow.Action
import com.popalay.tracktor.ui.widget.TopAppBar
import com.popalay.tracktor.utils.onBackPressed
import com.squareup.workflow.ui.compose.composedViewFactory

val SettingsBinding = composedViewFactory<SettingsWorkflow.Rendering> { rendering, _ ->
    onBackPressed { rendering.onAction(Action.BackClicked) }
    SettingsScreen(rendering.onAction)
}

@Preview
@Composable
fun SettingsScreen(
    onAction: (Action) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = { onAction(Action.BackClicked) }) {
                        Icon(Icons.Default.ArrowBack)
                    }
                }
            )
        }
    ) {
        Column {
            SettingItem(
                icon = { Icon(Icons.Default.FeaturedPlayList) },
                title = { Text(stringResource(R.string.feature_toggles_title)) },
                onClick = { onAction(Action.FeatureTogglesClicked) }
            )
            if (false) {
                SettingItem(
                    icon = { Icon(Icons.Default.RateReview) },
                    title = { Text(stringResource(R.string.settings_review_title)) },
                    onClick = {}
                )
                SettingItem(
                    icon = { Icon(Icons.Default.CallMade) },
                    title = { Text(stringResource(R.string.settings_import_title)) },
                    onClick = {}
                )
                SettingItem(
                    icon = { Icon(Icons.Default.CallReceived) },
                    title = { Text(stringResource(R.string.settings_export_title)) },
                    onClick = {}
                )
            }
        }
    }
}

@Composable
fun SettingItem(
    icon: @Composable () -> Unit,
    title: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp)) {
        icon()
        Spacer(Modifier.width(16.dp))
        title()
    }
}