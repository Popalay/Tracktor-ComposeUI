package com.popalay.tracktor.ui.list

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.foundation.shape.corner.CornerSize
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.height
import androidx.ui.layout.padding
import androidx.ui.material.Card
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.ripple
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.popalay.tracktor.ThemedPreview
import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.model.ValueRecord
import java.time.LocalDateTime

@Composable
fun TrackedValueValueListItem(
    item: TrackerWithRecords,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val gradients = mapOf(
        TrackableUnit.Kilograms to listOf(Color(0xFF64BFE1), Color(0xFFA091B7), Color(0xFFE0608A)),
        TrackableUnit.Quantity to listOf(Color(0xFF64BFE1), Color(0xFF45A190), Color(0xFF348F50)),
        TrackableUnit.Minutes to listOf(Color(0xFF64BFE1), Color(0xFF86A7E7), Color(0xFF8360C3)),
        TrackableUnit.None to emptyList()
    )

    Card(
        modifier = Modifier
            .ripple(radius = 8.dp)
            .clickable(onClick = onClick, onLongClick = onLongClick),
        shape = MaterialTheme.shapes.medium.copy(CornerSize(8.dp))
    ) {
        Column {
            ChartWidget(data = item.records.map { it.value }, gradient = gradients.getValue(item.tracker.unit))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)

            ) {
                Text(item.tracker.title)
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
                    TrackerWithRecords(
                        Tracker("trackerId", "title", it),
                        listOf(ValueRecord("valueId", "trackerId", 42.3, LocalDateTime.now()))
                    ), {}, {})
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}