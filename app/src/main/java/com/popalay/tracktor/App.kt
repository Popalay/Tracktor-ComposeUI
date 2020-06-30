package com.popalay.tracktor

import androidx.compose.Composable
import com.popalay.tracktor.list.ListBinding
import com.popalay.tracktor.list.ListWorkflow
import com.squareup.workflow.diagnostic.SimpleLoggingDiagnosticListener
import com.squareup.workflow.ui.ViewEnvironment
import com.squareup.workflow.ui.ViewRegistry
import com.squareup.workflow.ui.compose.WorkflowContainer

private val viewRegistry = ViewRegistry(ListBinding)
private val viewEnvironment = ViewEnvironment(viewRegistry)

@Composable
fun App() {
    AppTheme(isDarkTheme = true) {
        WorkflowContainer(
            workflow = ListWorkflow,
            viewEnvironment = viewEnvironment,
            diagnosticListener = SimpleLoggingDiagnosticListener()
        )
    }
}