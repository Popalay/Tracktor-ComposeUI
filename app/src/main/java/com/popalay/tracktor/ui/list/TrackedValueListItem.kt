package com.popalay.tracktor.ui.list

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.shape.corner.CornerSize
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.height
import androidx.ui.layout.padding
import androidx.ui.material.Card
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Add
import androidx.ui.material.icons.filled.Delete
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
import com.popalay.tracktor.ui.widget.ProgressTextField
import java.time.LocalDateTime

@Composable
fun TrackerListItem(
    item: TrackerListItem,
    modifier: Modifier = Modifier,
    onAddClicked: () -> Unit,
    onRemoveClicked: () -> Unit
) {
    val gradient = gradients.getValue(item.data.tracker.unit)
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium.copy(CornerSize(16.dp))
    ) {
        Column {
            Header(item, gradient)
            Body(item, gradient)
            Footer(item, onAddClicked, onRemoveClicked)
        }
    }
}

@Composable
private fun Footer(
    item: TrackerListItem,
    onAddClicked: () -> Unit,
    onRemoveClicked: () -> Unit
) {
    Row(verticalGravity = Alignment.CenterVertically) {
        IconButton(onClick = onAddClicked) {
            Icon(asset = Icons.Default.Add)
        }
        IconButton(onClick = onRemoveClicked) {
            Icon(asset = Icons.Default.Delete)
        }
        Spacer(modifier = Modifier.weight(1F))
        Box(paddingEnd = 16.dp) {
            Text("Current: ${item.data.format(item.data.currentValue)}")
        }
    }
}

@Composable
private fun Header(item: TrackerListItem, gradient: List<Color>) {
    Row(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp), verticalGravity = Alignment.CenterVertically) {
        Text(item.data.tracker.title)
        Spacer(modifier = Modifier.weight(1F))
        ProgressTextField(item.data.progress, color = gradient.last())
    }
}

@Composable
private fun Body(
    item: TrackerListItem,
    gradient: List<Color>
) {
    if (item.data.records.size > 1) {
        ChartWidget(
            data = item.data.records.map { it.value },
            gradient = gradient,
            touchable = false
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
}

@Preview
@Composable
fun TrackedValueValueListItemPreview() {
    ThemedPreview(isDarkTheme = true) {
        TrackerListItem(fakeListItem(), Modifier, {}, {})
    }
}

private fun fakeListItem() = TrackerListItem(
    data = TrackerWithRecords(
        Tracker("trackerId", "title", TrackableUnit.Kilograms, LocalDateTime.now()),
        listOf(ValueRecord("valueId", "trackerId", 42.3, LocalDateTime.now()))
    )
)