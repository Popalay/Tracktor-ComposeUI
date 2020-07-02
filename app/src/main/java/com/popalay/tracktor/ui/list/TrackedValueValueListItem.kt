package com.popalay.tracktor.ui.list

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.foundation.shape.corner.CornerSize
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.material.Card
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.ripple
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.popalay.tracktor.ThemedPreview
import com.popalay.tracktor.gradients
import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.model.ValueRecord
import java.time.LocalDateTime

@Composable
fun TrackedValueValueListItem(
    item: ListItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .ripple(radius = 8.dp)
            .clickable(onClick = onClick, onLongClick = onLongClick),
        shape = MaterialTheme.shapes.medium.copy(CornerSize(8.dp))
    ) {
        Column {
            ChartWidget(data = item.data.records.map { it.value }, gradient = gradients.getValue(item.data.tracker.unit))
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(item.data.tracker.title)
                Spacer(modifier = Modifier.weight(1F))
                Text(item.data.displayValue)
            }
        }
    }
}

@Preview
@Composable
fun TrackedValueValueListItemPreview() {
    ThemedPreview(isDarkTheme = true) {
        TrackedValueValueListItem(fakeListItem(), {}, {})
    }
}

private fun fakeListItem() = ListItem(
    data = TrackerWithRecords(
        Tracker("trackerId", "title", TrackableUnit.Kilograms),
        listOf(ValueRecord("valueId", "trackerId", 42.3, LocalDateTime.now()))
    )
)