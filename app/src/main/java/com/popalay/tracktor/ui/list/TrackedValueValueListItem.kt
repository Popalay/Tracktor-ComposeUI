package com.popalay.tracktor.ui.list

import androidx.compose.Composable
import androidx.compose.remember
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.foundation.currentTextStyle
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
import androidx.ui.text.font.FontWeight
import androidx.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.popalay.tracktor.ThemedPreview
import com.popalay.tracktor.gradients
import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.model.ValueRecord
import com.popalay.tracktor.ui.widget.ChartWidget
import java.time.LocalDateTime

@Composable
fun TrackedValueValueListItem(
    item: ListItem,
    modifier: Modifier = Modifier
) {
    val selectedValue = state<Double?> { null }
    val gradient = remember { gradients.getValue(item.data.tracker.unit) }
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium.copy(CornerSize(16.dp))
    ) {
        Column {
            Row(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
                Text(item.data.tracker.title)
                Spacer(modifier = Modifier.weight(1F))
                ProgressTextField(item.data.progress, color = gradient.last())
            }
            if (item.data.records.size > 1) {
                ChartWidget(
                    data = item.data.records.map { it.value },
                    gradient = gradient,
                    onPointSelected = { selectedValue.value = it },
                    onPointUnSelected = { selectedValue.value = null }
                )
            } else {
                Row(modifier = Modifier.height(100.dp).fillMaxWidth()) {
                    Text(
                        text = item.data.format(item.data.currentValue),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h2.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.gravity(Alignment.CenterVertically).fillMaxWidth()
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Spacer(modifier = Modifier.weight(1F))
                if (selectedValue.value == null) {
                    Text("Current: ${item.data.format(item.data.currentValue)}")
                } else {
                    Text(
                        "Selected: ${item.data.format(selectedValue.value!!)}",
                        style = currentTextStyle().copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressTextField(
    progress: Double,
    color: Color = MaterialTheme.colors.secondary,
    modifier: Modifier = Modifier
) {
    val progressPercent = (progress * 100).toInt()
    val arrow = if (progressPercent >= 0) "↑" else "↓"
    Text(
        text = "$arrow$progressPercent%",
        color = color,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.caption,
        modifier = modifier
    )
}

@Preview
@Composable
fun TrackedValueValueListItemPreview() {
    ThemedPreview(isDarkTheme = true) {
        TrackedValueValueListItem(fakeListItem())
    }
}

private fun fakeListItem() = ListItem(
    data = TrackerWithRecords(
        Tracker("trackerId", "title", TrackableUnit.Kilograms, LocalDateTime.now()),
        listOf(ValueRecord("valueId", "trackerId", 42.3, LocalDateTime.now()))
    )
)