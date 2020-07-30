package com.popalay.tracktor.ui.trackerdetail

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.width
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import com.popalay.tracktor.domain.formatter.ValueRecordFormatter
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.model.ValueRecord
import com.popalay.tracktor.success
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
            text = record.date.toRelativeFormat(),
            style = MaterialTheme.typography.subtitle1
        )
        Spacer(modifier = Modifier.weight(1F))

        val previousRecord = trackerWithRecords.records.getOrNull(trackerWithRecords.records.indexOf(record) - 1)
        val progress = trackerWithRecords.progress(previousRecord?.value, record.value)
        ProgressTextField(
            progress,
            color = if (progress >= 0) MaterialTheme.colors.success else MaterialTheme.colors.error
        )

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = formatter.format(trackerWithRecords.tracker, record),
            style = MaterialTheme.typography.subtitle2
        )
    }
}