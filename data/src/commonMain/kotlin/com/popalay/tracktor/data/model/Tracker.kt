package com.popalay.tracktor.data.model

import kotlinx.datetime.LocalDateTime

data class Tracker(
    val id: String,
    val title: String,
    val unit: TrackableUnit,
    val direction: ProgressDirection,
    val date: LocalDateTime,
    val isDeleted: Boolean,
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