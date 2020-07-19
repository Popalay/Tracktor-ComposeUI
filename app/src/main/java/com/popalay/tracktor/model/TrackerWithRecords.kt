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
    val currentValue: ValueRecord? get() = records.lastOrNull()

    val progress: Double
        get() = progress(records.lastOrNull()?.value ?: 0.0)

    fun progress(lastValue: Double): Double {
        return try {
            (lastValue) / (records.firstOrNull()?.value ?: 0.0) - 1
        } catch (exception: Exception) {
            0.0
        }
    }
}