package com.popalay.tracktor.list

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.Modifier
import androidx.ui.core.WithConstraints
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.foundation.drawBackground
import androidx.ui.foundation.shape.corner.CornerSize
import androidx.ui.graphics.Color
import androidx.ui.graphics.HorizontalGradient
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.height
import androidx.ui.layout.padding
import androidx.ui.material.Card
import androidx.ui.material.MaterialTheme
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import androidx.ui.unit.toPx
import com.popalay.tracktor.ThemedPreview
import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.model.TrackedValue

@Composable
fun TrackedValueValueListItem(
    item: TrackedValue,
    onNewRecordSubmitted: (TrackedValue, Double) -> Unit
) {
    val isDialogShowing = state { false }

    val gradients = mapOf(
        TrackableUnit.Kilograms to listOf(Color(0xFF64BFE1), Color(0xFFA091B7), Color(0xFFE0608A)),
        TrackableUnit.Quantity to listOf(Color(0xFF64BFE1), Color(0xFF45A190), Color(0xFF348F50)),
        TrackableUnit.Minutes to listOf(Color(0xFF64BFE1), Color(0xFF86A7E7), Color(0xFF8360C3))
    )

    Card(
        shape = MaterialTheme.shapes.medium.copy(CornerSize(8.dp))
    ) {
        if (isDialogShowing.value) {
            UpdateTrackedValueDialog(
                onCloseRequest = { isDialogShowing.value = false },
                onSave = { onNewRecordSubmitted(item, it) }
            )
        }
        WithConstraints {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {
                        isDialogShowing.value = true
                    })
                    .drawBackground(
                        HorizontalGradient(
                            gradients.getValue(item.unit),
                            startX = 0F,
                            endX = constraints.maxWidth.toPx().value
                        )
                    )
                    .padding(16.dp)

            ) {
                Text(item.title)
                Spacer(modifier = Modifier.weight(1F))
                Text(item.displayValue)
            }
        }
    }
}

@Preview
@Composable
fun TrackedValueValueListItemPreview() {
    ThemedPreview(isDarkTheme = true) {
        Column {
            TrackableUnit.values().drop(1).forEach {
                TrackedValueValueListItem(
                    TrackedValue(
                        "title",
                        42.3,
                        it
                    )
                ) { _, _ -> }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}