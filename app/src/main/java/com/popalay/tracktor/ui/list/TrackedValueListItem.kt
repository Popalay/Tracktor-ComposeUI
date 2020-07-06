package com.popalay.tracktor.ui.list

import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.compose.remember
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Icon
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
import java.time.LocalDateTime

@Composable
fun TrackedValueListItem(
    item: ListItem,
    modifier: Modifier = Modifier,
    onAddClicked: () -> Unit,
    onRemoveClicked: () -> Unit
) {
    val selectedValue = state<Double?> { null }
    val gradient = remember { gradients.getValue(item.data.tracker.unit) }
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium.copy(CornerSize(16.dp))
    ) {
        Column {
            Header(item, gradient)
            Body(item, gradient, selectedValue)
            Footer(selectedValue.value, item, onAddClicked, onRemoveClicked)
        }
    }
}

@Composable
private fun Footer(
    selectedValue: Double?,
    item: ListItem,
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
            if (selectedValue == null) {
                Text("Current: ${item.data.format(item.data.currentValue)}")
            } else {
                Text(
                    "Selected: ${item.data.format(selectedValue)}",
                    style = currentTextStyle().copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
private fun Header(item: ListItem, gradient: List<Color>) {
    Row(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp), verticalGravity = Alignment.CenterVertically) {
        Text(item.data.tracker.title)
        Spacer(modifier = Modifier.weight(1F))
        ProgressTextField(item.data.progress, color = gradient.last())
    }
}

@Composable
private fun Body(
    item: ListItem,
    gradient: List<Color>,
    selectedValue: MutableState<Double?>
) {
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
        TrackedValueListItem(fakeListItem(), Modifier, {}, {})
    }
}

private fun fakeListItem() = ListItem(
    data = TrackerWithRecords(
        Tracker("trackerId", "title", TrackableUnit.Kilograms, LocalDateTime.now()),
        listOf(ValueRecord("valueId", "trackerId", 42.3, LocalDateTime.now()))
    )
)