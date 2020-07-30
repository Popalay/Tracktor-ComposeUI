package com.popalay.tracktor.ui.featureflagslist

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.height
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.material.Checkbox
import androidx.ui.material.IconButton
import androidx.ui.material.Scaffold
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowBack
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import androidx.ui.tooling.preview.PreviewParameterProvider
import androidx.ui.unit.dp
import com.popalay.tracktor.WindowInsetsAmbient
import com.popalay.tracktor.ui.featureflagslist.FeatureFlagsListWorkflow.Action
import com.popalay.tracktor.ui.widget.DefaultTopAppBarHeight
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
                title = { Text("Feature toggles") },
                navigationIcon = {
                    IconButton(onClick = { onAction(Action.BackClicked) }) {
                        Icon(Icons.Default.ArrowBack)
                    }
                },
                modifier = Modifier.preferredHeight(DefaultTopAppBarHeight + WindowInsetsAmbient.current.top),
                contentModifier = Modifier.padding(top = WindowInsetsAmbient.current.top)
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