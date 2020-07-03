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

    fun format(value: Double): String {
        val roundedValue: Number = if (value == value.toInt().toDouble()) value.toInt() else value
        return when (tracker.unit) {
            TrackableUnit.None -> ""
            TrackableUnit.Quantity -> roundedValue.toInt().toString()
            TrackableUnit.Minutes -> roundedValue.let { "$it\"" }
            TrackableUnit.Kilograms -> roundedValue.let { "$it kg" }
        }
    }

    val progress: Double
        get() = try {
            (records.lastOrNull()?.value ?: 0.0) / (records.firstOrNull()?.value ?: 0.0) - 1
        } catch (exception: Exception) {
            0.0
        }
}