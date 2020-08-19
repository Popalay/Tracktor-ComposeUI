package com.popalay.tracktor.feature.trackerdetail

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.popalay.tracktor.data.model.TrackerWithRecords
import com.popalay.tracktor.data.model.ValueRecord
import com.popalay.tracktor.domain.formatter.ValueRecordFormatter
import com.popalay.tracktor.ui.widget.ProgressTextField
import com.popalay.tracktor.utils.inject
import com.popalay.tracktor.utils.toRelativeFormat

@Composable
fun RecordListItem(trackerWithRecords: TrackerWithRecords, record: ValueRecord, modifier: Modifier = Modifier) {
    val formatter: ValueRecordFormatter by inject()

    Row(
        modifier = modifier,
        verticalGravity = Alignment.CenterVertically
    ) {
        Text(
            text = formatter.format(trackerWithRecords.tracker, record),
            style = MaterialTheme.typography.subtitle1
        )
        Spacer(modifier = Modifier.width(8.dp))
        val previousRecord = trackerWithRecords.records.getOrNull(trackerWithRecords.records.indexOf(record) - 1)
        val progress = trackerWithRecords.progress(previousRecord?.value, record.value)
        ProgressTextField(progress, trackerWithRecords.tracker.direction)

        Spacer(modifier = Modifier.weight(1F))

        Text(
            text = record.date.toRelativeFormat(),
            style = MaterialTheme.typography.subtitle2
        )
    }
}