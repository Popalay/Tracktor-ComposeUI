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

    fun progress(
        previousValue: Double? = records.firstOrNull()?.value,
        currentValue: Double? = records.lastOrNull()?.value
    ): Double {
        return (currentValue ?: 0.0) / (previousValue ?: return 0.0) - 1
    }
}