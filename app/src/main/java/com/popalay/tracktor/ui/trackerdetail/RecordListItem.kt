package com.popalay.tracktor.ui.trackerdetail

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.width
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.model.ValueRecord
import com.popalay.tracktor.ui.widget.ProgressTextField
import com.popalay.tracktor.utils.toRelativeFormat

@Composable
fun RecordListItem(tracker: TrackerWithRecords, record: ValueRecord, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalGravity = Alignment.CenterVertically
    ) {
        Text(
            text = record.date.toRelativeFormat(),
            style = MaterialTheme.typography.subtitle1
        )
        Spacer(modifier = Modifier.weight(1F))

        val progress = tracker.progress(record.value)
        ProgressTextField(
            progress,
            color = if (progress >= 0) Color.Green else Color.Red
        )

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = tracker.format(record.value),
            style = MaterialTheme.typography.subtitle2
        )
    }
}