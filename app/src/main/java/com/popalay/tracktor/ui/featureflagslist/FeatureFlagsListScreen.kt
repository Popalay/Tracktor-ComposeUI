package com.popalay.tracktor.ui.featureflagslist

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import androidx.ui.tooling.preview.PreviewParameterProvider
import com.popalay.tracktor.core.R
import com.popalay.tracktor.ui.featureflagslist.FeatureFlagsListWorkflow.Action
import com.popalay.tracktor.ui.widget.TopAppBar
import com.popalay.tracktor.utils.Faker
import com.popalay.tracktor.utils.onBackPressed
import com.squareup.workflow.ui.compose.composedViewFactory

val FeatureFlagsListBinding = composedViewFactory<FeatureFlagsListWorkflow.Rendering> { rendering, _ ->
    onBackPressed { rendering.onAction(Action.BackClicked) }
    FeatureFlagsListScreen(rendering.state, rendering.onAction)
}

class FeatureFlagsListPreviewProvider : PreviewParameterProvider<FeatureFlagsListWorkflow.State> {
    override val values: Sequence<FeatureFlagsListWorkflow.State>
        get() = sequenceOf(FeatureFlagsListWorkflow.State(List(5) { Faker.fakeFeatureFlag() }))
}

@Preview
@Composable
fun FeatureFlagsListScreen(
    @PreviewParameter(FeatureFlagsListPreviewProvider::class) state: FeatureFlagsListWorkflow.State,
    onAction: (Action) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.feature_toggles_title)) },
                navigationIcon = {
                    IconButton(onClick = { onAction(Action.BackClicked) }) {
                        Icon(Icons.Default.ArrowBack)
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            state.featureFlags.forEach { featureFlagItem ->
                Row {
                    Text(text = featureFlagItem.displayName)
                    Spacer(modifier = Modifier.weight(1F))
                    Checkbox(
                        checked = featureFlagItem.isEnabled,
                        onCheckedChange = { onAction(Action.FeatureFlagChanged(featureFlagItem, it)) }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}