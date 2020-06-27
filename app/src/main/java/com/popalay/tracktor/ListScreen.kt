package com.popalay.tracktor

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.Modifier
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.foundation.drawBackground
import androidx.ui.foundation.shape.corner.CornerSize
import androidx.ui.graphics.Color
import androidx.ui.graphics.HorizontalGradient
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.height
import androidx.ui.layout.padding
import androidx.ui.layout.width
import androidx.ui.material.AlertDialog
import androidx.ui.material.Card
import androidx.ui.material.FilledTextField
import androidx.ui.material.MaterialTheme
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp

@Composable
fun ListScreen() {
    val (currentList, updateList) = state { listOf<Any>("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        AdapterList(data = currentList) {
            if (it is TrackedValue) {
                Spacer(modifier = Modifier.height(8.dp))
                TrackedValueValueListItem(it)
            } else {
                CreateTrackedValue { newValue ->
                    updateList(currentList.plus(newValue))
                }
            }
        }
    }
}

@Composable
fun TrackedValueValueListItem(item: TrackedValue) {
    val isDialogShowing = state { false }

    Card(
        shape = MaterialTheme.shapes.medium.copy(CornerSize(8.dp))
    ) {
        if (isDialogShowing.value) {
            AlertDialog(
                onCloseRequest = {
                    isDialogShowing.value = false
                },
                title = { Text(text = "Dialog") },
                text = {
                    FilledTextField(
                        value = "hellow",
                        label = { Text(text = "label") },
                        onValueChange = {})
                },
                buttons = {}
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = {
                    isDialogShowing.value = true
                })
                .drawBackground(
                    HorizontalGradient(
                        0.0f to Color.Red,
                        0.5f to Color.Green,
                        1.0f to Color.Blue,
                        startX = 0F,
                        endX = 500F
                    )
                )
                .padding(16.dp)

        ) {
            Text(item.title)
            Spacer(modifier = Modifier.width(8.dp))
            Text(item.value)
            Spacer(modifier = Modifier.width(8.dp))
            Text(item.unit.displayName)
        }
    }
}

@Preview
@Composable
fun TrackedValueValueListItemPreview() {
    ThemedPreview(isDarkTheme = true) {
        TrackedValueValueListItem(item = TrackedValue("title", "value", TrackableUnit.Kilograms))
    }
}