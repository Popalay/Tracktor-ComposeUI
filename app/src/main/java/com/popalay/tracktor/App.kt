package com.popalay.tracktor

import androidx.compose.Composable
import androidx.ui.foundation.isSystemInDarkTheme
import com.popalay.tracktor.ui.list.ListBinding
import com.popalay.tracktor.ui.list.ListWorkflow
import com.squareup.workflow.diagnostic.SimpleLoggingDiagnosticListener
import com.squareup.workflow.ui.ViewEnvironment
import com.squareup.workflow.ui.ViewRegistry
import com.squareup.workflow.ui.compose.WorkflowContainer

private val viewRegistry = ViewRegistry(ListBinding)
private val viewEnvironment = ViewEnvironment(viewRegistry)

@Composable
fun App() {
    AppTheme(isDarkTheme = isSystemInDarkTheme()) {
        WorkflowContainer(
            workflow = ListWorkflow,
            viewEnvironment = viewEnvironment,
            diagnosticListener = SimpleLoggingDiagnosticListener()
        )
    }
}