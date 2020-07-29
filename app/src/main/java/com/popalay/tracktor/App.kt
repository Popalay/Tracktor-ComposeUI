package com.popalay.tracktor

import androidx.compose.Composable
import androidx.ui.foundation.isSystemInDarkTheme
import com.popalay.tracktor.ui.createtracker.CreateTrackerBinding
import com.popalay.tracktor.ui.featureflagslist.FeatureFlagsListBinding
import com.popalay.tracktor.ui.list.ListBinding
import com.popalay.tracktor.ui.trackerdetail.TrackerDetailBinding
import com.popalay.tracktor.utils.inject
import com.squareup.workflow.diagnostic.SimpleLoggingDiagnosticListener
import com.squareup.workflow.ui.ViewEnvironment
import com.squareup.workflow.ui.ViewRegistry
import com.squareup.workflow.ui.compose.WorkflowContainer

private val viewRegistry = ViewRegistry(ListBinding, TrackerDetailBinding, FeatureFlagsListBinding, CreateTrackerBinding)
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