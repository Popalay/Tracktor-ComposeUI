package com.popalay.tracktor.ui.list

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import androidx.ui.tooling.preview.PreviewParameterProvider
import com.popalay.tracktor.domain.formatter.ValueRecordFormatter
import com.popalay.tracktor.gradient
import com.popalay.tracktor.model.TrackerListItem
import com.popalay.tracktor.ui.widget.ProgressTextField
import com.popalay.tracktor.ui.widget.SimpleChartWidget
import com.popalay.tracktor.utils.Faker
import com.popalay.tracktor.utils.inject

class TrackerListItemPreviewProvider : PreviewParameterProvider<TrackerListItem> {
    override val values: Sequence<TrackerListItem>
        get() = sequenceOf(
            TrackerListItem(Faker.fakeTrackerWithRecords().copy(records = emptyList())),
            TrackerListItem(Faker.fakeTrackerWithRecords())
        )
}

@Preview
@Composable
fun TrackerListItem(
    @PreviewParameter(TrackerListItemPreviewProvider::class) item: TrackerListItem,
    modifier: Modifier = Modifier,
    onAddClicked: () -> Unit = {},
    onRemoveClicked: () -> Unit = {}
) {
    val gradient = remember(item) { item.data.tracker.compatibleUnit.gradient }
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.preferredHeight(120.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Body(item, gradient)
            Footer(onAddClicked, onRemoveClicked, item)
        }
    }
}

@Composable
private fun Footer(
    onAddClicked: () -> Unit,
    onRemoveClicked: () -> Unit,
    item: TrackerListItem
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
        if (item.data.records.isNotEmpty()) {
            ProgressTextField(item.data.progress())
            Spacer(modifier = Modifier.width(8.dp))
            Text(formatter.format(item.data.tracker, item.data.currentValue))
        } else {
            Text("Start tracking now", style = MaterialTheme.typography.caption)
        }
    }
}

@Composable
private fun Body(item: TrackerListItem, gradient: List<Color>) {
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
        if (item.data.records.isNotEmpty()) {
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