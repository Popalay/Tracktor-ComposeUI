package com.popalay.tracktor

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.popalay.tracktor.feature.createtracker.CreateTrackerBinding
import com.popalay.tracktor.feature.featureflagslist.FeatureFlagsListBinding
import com.popalay.tracktor.feature.list.ListBinding
import com.popalay.tracktor.feature.settings.SettingsBinding
import com.popalay.tracktor.feature.trackerdetail.TrackerDetailBinding
import com.popalay.tracktor.utils.inject
import com.squareup.workflow.diagnostic.SimpleLoggingDiagnosticListener
import com.squareup.workflow.ui.ViewEnvironment
import com.squareup.workflow.ui.ViewRegistry
import com.squareup.workflow.ui.compose.WorkflowContainer

private val viewRegistry = ViewRegistry(
    ListBinding,
    TrackerDetailBinding,
    FeatureFlagsListBinding,
    CreateTrackerBinding,
    SettingsBinding
)
private val viewEnvironment = ViewEnvironment(viewRegistry)

@Composable
fun App() {
    AppTheme(isDarkTheme = isSystemInDarkTheme()) {
        val workflow: AppWorkflow by inject()
        WorkflowContainer(
            workflow = workflow,
            viewEnvironment = viewEnvironment,
            diagnosticListener = SimpleLoggingDiagnosticListener()
        )
    }
}