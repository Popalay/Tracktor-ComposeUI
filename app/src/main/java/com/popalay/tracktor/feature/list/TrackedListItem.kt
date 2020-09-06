package com.popalay.tracktor.feature.list

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.Dimension
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
        ConstraintLayout(contentModifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
            val (titleId, chartId, progressId, valueId) = createRefs()

            Text(
                item.data.tracker.title,
                style = MaterialTheme.typography.h5,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.constrainAs(titleId) {
                    centerVerticallyTo(parent)
                    start.linkTo(parent.start)
                    end.linkTo(chartId.start)
                    width = Dimension.fillToConstraints
                }
            )

            SimpleChartWidget(
                data = item.data.records.map { it.value },
                gradient = gradient,
                animate = item.animate,
                modifier = Modifier.constrainAs(chartId) {
                    width = Dimension.value(100.dp)
                    height = Dimension.value(50.dp)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(progressId.top)
                }
            )

            ProgressTextField(
                progress = item.data.progress(),
                direction = item.data.tracker.direction,
                modifier = Modifier.constrainAs(progressId) {
                    end.linkTo(valueId.start, margin = 4.dp)
                    top.linkTo(valueId.top)
                    bottom.linkTo(valueId.bottom)
                }
            )

            Text(
                text = formatter.format(item.data.tracker, item.data.currentValue),
                modifier = Modifier.constrainAs(valueId) {
                    end.linkTo(parent.end)
                    top.linkTo(chartId.bottom, margin = 8.dp)
                    bottom.linkTo(parent.bottom)
                }
            )
        }
    }
}