package com.popalay.tracktor.feature.list

import androidx.compose.animation.animate
import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.drawLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import androidx.ui.tooling.preview.PreviewParameterProvider
import com.popalay.tracktor.core.R
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

    Card(modifier = modifier) {
        Column(
            modifier = contentModifier.preferredHeight(120.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Body(item, gradient)
            Footer(item)
        }
    }
}

@Composable
private fun Footer(item: TrackerListItem) {
    val formatter: ValueRecordFormatter by inject()

    Row(verticalGravity = Alignment.CenterVertically, modifier = Modifier.padding(end = 16.dp)) {
        Spacer(Modifier.weight(1F))
        if (item.data.records.isNotEmpty()) {
            ProgressTextField(item.data.progress(), item.data.tracker.direction)
            Spacer(Modifier.width(8.dp))
            Text(formatter.format(item.data.tracker, item.data.currentValue))
        } else {
            Text(stringResource(R.string.tracker_item_empty_message), style = MaterialTheme.typography.caption)
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDismissBackground(dismissState: DismissState) {
    val direction = dismissState.dismissDirection ?: return
    val (gravity, icon) = when (direction) {
        DismissDirection.StartToEnd -> ContentGravity.CenterStart to Icons.Default.Add
        DismissDirection.EndToStart -> ContentGravity.CenterEnd to Icons.Default.Delete
    }
    val scale = animate(if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f)

    Box(
        modifier = Modifier.fillMaxSize(),
        paddingStart = 24.dp,
        paddingEnd = 24.dp,
        gravity = gravity
    ) {
        Icon(icon, modifier = Modifier.drawLayer(scaleX = scale, scaleY = scale))
    }
}