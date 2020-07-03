package com.popalay.tracktor.model

import androidx.room.Embedded
import androidx.room.Relation

data class TrackerWithRecords(
    @Embedded val tracker: Tracker,
    @Relation(
        parentColumn = "id",
        entityColumn = "trackerId"
    )
    val records: List<ValueRecord>
) {
    val currentValue: Double get() = records.lastOrNull()?.value ?: 0.0

    fun format(value: Double) =
        when (tracker.unit) {
            TrackableUnit.None -> ""
            TrackableUnit.Quantity -> value.toInt().toString()
            TrackableUnit.Minutes -> value.let { "$it\"" }
            TrackableUnit.Kilograms -> value.let { "$it kg" }
        }
}