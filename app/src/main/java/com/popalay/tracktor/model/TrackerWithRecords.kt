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
    val currentValue: Double get() = records.firstOrNull()?.value ?: 0.0

    val displayValue: String
        get() = records.map { it.value }.ifEmpty { listOf(0.0) }.joinToString {
            when (tracker.unit) {
                TrackableUnit.None -> ""
                TrackableUnit.Quantity -> it.toInt().toString()
                TrackableUnit.Minutes -> it.let { "$it\"" }
                TrackableUnit.Kilograms -> it.let { "$it kg" }
            }
        }
}