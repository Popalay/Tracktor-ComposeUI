package com.popalay.tracktor.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Tracker(
    @PrimaryKey val id: String,
    val title: String,
    @Embedded val unit: TrackableUnit,
    val direction: ProgressDirection,
    val date: LocalDateTime
) {
    val compatibleUnit: TrackableUnit
        get() = when (unit.name) {
            "Quantity" -> TrackableUnit.Quantity
            "Minutes" -> TrackableUnit.Time
            "Kilograms" -> TrackableUnit.Weight
            "Word" -> TrackableUnit.Word
            else -> unit
        }
}