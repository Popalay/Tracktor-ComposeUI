package com.popalay.tracktor.feature.list

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import androidx.ui.tooling.preview.PreviewParameterProvider
import com.popalay.tracktor.data.model.TrackerListItem
import com.popalay.tracktor.domain.formatter.ValueRecordFormatter
import com.popalay.tracktor.gradient
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
    contentModifier: Modifier = Modifier
) {
    val gradient = remember(item) { item.data.tracker.compatibleUnit.gradient }
    val formatter: ValueRecordFormatter by inject()

    Card(modifier = modifier) {
        Row(
            modifier = contentModifier.preferredHeight(120.dp).padding(16.dp),
            verticalGravity = Alignment.CenterVertically
        ) {
            Text(
                item.data.tracker.title,
                style = MaterialTheme.typography.h5,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1F)
            )
            Spacer(Modifier.width(24.dp))
            Column(
                horizontalGravity = Alignment.CenterHorizontally
            ) {
                SimpleChartWidget(
                    data = item.data.records.map { it.value },
                    gradient = gradient,
                    animate = item.animate,
                    modifier = Modifier.preferredSize(100.dp, 50.dp)
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalGravity = Alignment.CenterVertically) {
                    ProgressTextField(item.data.progress(), item.data.tracker.direction)
                    Spacer(Modifier.width(8.dp))
                    Text(formatter.format(item.data.tracker, item.data.currentValue))
                }
            }
        }
    }
}