package com.popalay.tracktor.ui.list

import androidx.compose.Composable
import androidx.compose.remember
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
import androidx.ui.layout.preferredSize
import androidx.ui.layout.preferredWidth
import androidx.ui.layout.width
import androidx.ui.material.Card
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Add
import androidx.ui.material.icons.filled.Delete
import androidx.ui.text.font.FontWeight
import androidx.ui.text.style.TextAlign
import androidx.ui.text.style.TextOverflow
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import androidx.ui.tooling.preview.PreviewParameterProvider
import androidx.ui.unit.dp
import com.popalay.tracktor.data.featureflags.FeatureFlagsManager
import com.popalay.tracktor.domain.formatter.ValueRecordFormatter
import com.popalay.tracktor.gradients
import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.model.TrackerListItem
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.model.ValueRecord
import com.popalay.tracktor.ui.widget.ChartWidget
import com.popalay.tracktor.ui.widget.ProgressTextField
import com.popalay.tracktor.ui.widget.SimpleChartWidget
import com.popalay.tracktor.utils.inject
import java.time.LocalDateTime

class TrackerListItemPreviewProvider : PreviewParameterProvider<TrackerListItem> {
    override val values: Sequence<TrackerListItem>
        get() {
            val records = listOf(
                ValueRecord("valueId", "trackerId", 42.3, LocalDateTime.now()),
                ValueRecord("valueId", "trackerId", 12.3, LocalDateTime.now()),
                ValueRecord("valueId", "trackerId", 62.3, LocalDateTime.now()),
                ValueRecord("valueId", "trackerId", 2.3, LocalDateTime.now())
            )
            val tracker = TrackerWithRecords(Tracker("id", "title", TrackableUnit.Kilograms, LocalDateTime.now()), records)

            return sequenceOf(
                TrackerListItem(tracker.copy(records = emptyList())),
                TrackerListItem(tracker)
            )
        }
}

@Preview
@Composable
fun TrackerListItem(
    @PreviewParameter(TrackerListItemPreviewProvider::class) item: TrackerListItem,
    modifier: Modifier = Modifier,
    onAddClicked: () -> Unit = {},
    onRemoveClicked: () -> Unit = {}
) {
    val featureFlagsManager by inject<FeatureFlagsManager>()

    if (featureFlagsManager.isSmallTrackerListItemEnabled()) {
        SimpleTrackerListItem(item, modifier, onAddClicked, onRemoveClicked)
    } else {
        DetailedTrackerListItem(item, modifier, onAddClicked, onRemoveClicked)
    }
}

@Preview
@Composable
fun SimpleTrackerListItem(
    @PreviewParameter(TrackerListItemPreviewProvider::class) item: TrackerListItem,
    modifier: Modifier = Modifier,
    onAddClicked: () -> Unit = {},
    onRemoveClicked: () -> Unit = {}
) {
    val gradient = remember(item) { gradients.getValue(item.data.tracker.unit) }
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium.copy(CornerSize(16.dp))
    ) {
        Column {
            SimpleBody(item, gradient)
            SimpleFooter(onAddClicked, onRemoveClicked, item, gradient)
        }
    }
}

@Preview
@Composable
fun DetailedTrackerListItem(
    @PreviewParameter(TrackerListItemPreviewProvider::class) item: TrackerListItem,
    modifier: Modifier = Modifier,
    onAddClicked: () -> Unit = {},
    onRemoveClicked: () -> Unit = {}
) {
    val gradient = remember(item) { gradients.getValue(item.data.tracker.unit) }
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
    val formatter: ValueRecordFormatter by inject()

    Row(verticalGravity = Alignment.CenterVertically) {
        IconButton(onClick = onAddClicked) {
            Icon(asset = Icons.Default.Add)
        }
        IconButton(onClick = onRemoveClicked) {
            Icon(asset = Icons.Default.Delete)
        }
        Spacer(modifier = Modifier.weight(1F))
        Box(paddingEnd = 16.dp) {
            Text("Current: ${formatter.format(item.data.tracker, item.data.currentValue)}")
        }
    }
}

@Composable
private fun Header(item: TrackerListItem, gradient: List<Color>) {
    Row(
        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
        verticalGravity = Alignment.CenterVertically
    ) {
        Text(item.data.tracker.title, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1F))
        ProgressTextField(item.data.progress, color = gradient.last())
    }
}

@Composable
private fun Body(
    item: TrackerListItem,
    gradient: List<Color>
) {
    val formatter: ValueRecordFormatter by inject()

    if (item.data.records.size > 1) {
        ChartWidget(
            data = item.data.records.map { it.value },
            gradient = gradient,
            touchable = false,
            animate = item.animate
        )
    } else {
        Row(modifier = Modifier.height(100.dp).fillMaxWidth()) {
            Text(
                text = formatter.format(item.data.tracker, item.data.currentValue),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h2.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.gravity(Alignment.CenterVertically).fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SimpleFooter(
    onAddClicked: () -> Unit,
    onRemoveClicked: () -> Unit,
    item: TrackerListItem,
    gradient: List<Color>
) {
    val formatter: ValueRecordFormatter by inject()

    Row(verticalGravity = Alignment.CenterVertically, modifier = Modifier.padding(end = 16.dp)) {
        IconButton(onClick = onAddClicked) {
            Icon(asset = Icons.Default.Add)
        }
        IconButton(onClick = onRemoveClicked) {
            Icon(asset = Icons.Default.Delete)
        }
        Spacer(modifier = Modifier.weight(1F))
        if (item.data.records.size > 1) {
            ProgressTextField(item.data.progress, color = gradient.last())
            Spacer(modifier = Modifier.width(8.dp))
            Text(formatter.format(item.data.tracker, item.data.currentValue))
        } else {
            Text("Start tracking now", style = MaterialTheme.typography.caption)
        }
    }
}

@Composable
private fun SimpleBody(item: TrackerListItem, gradient: List<Color>) {
    val formatter: ValueRecordFormatter by inject()

    Row(
        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
        verticalGravity = Alignment.CenterVertically
    ) {
        Text(
            item.data.tracker.title,
            style = MaterialTheme.typography.h5,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1F)
        )
        Spacer(modifier = Modifier.width(16.dp))
        if (item.data.records.size > 1) {
            SimpleChartWidget(
                data = item.data.records.map { it.value },
                gradient = gradient,
                animate = item.animate,
                modifier = Modifier.preferredSize(100.dp, 50.dp)
            )
        } else {
            Text(
                text = formatter.format(item.data.tracker, item.data.currentValue),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.gravity(Alignment.CenterVertically).preferredWidth(100.dp)
            )
        }
    }
}