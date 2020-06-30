package com.popalay.tracktor.list

import androidx.ui.core.Modifier
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.height
import androidx.ui.layout.padding
import androidx.ui.material.Scaffold
import androidx.ui.material.TopAppBar
import androidx.ui.unit.dp
import com.popalay.tracktor.create.CreateTrackedValue
import com.popalay.tracktor.model.TrackedValue
import com.squareup.workflow.ui.compose.composedViewFactory

val ListBinding = composedViewFactory<ListWorkflow.Rendering> { rendering, _ ->
    Scaffold(
        topAppBar = {
            TopAppBar(
                title = { Text("Tracktor") }
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            AdapterList(data = rendering.state.items) {
                if (it is TrackedValue) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TrackedValueValueListItem(it, rendering.onNewRecordSubmitted)
                } else {
                    CreateTrackedValue(rendering.onSubmitClicked)
                }
            }
        }
    }
}