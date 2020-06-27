package com.popalay.tracktor.model

data class TrackedValue(
    val title: String,
    val value: Double,
    val unit: TrackableUnit
) {

    val displayValue: String
        get() {
            return when (unit) {
                TrackableUnit.None -> ""
                TrackableUnit.Quantity -> value.toInt().toString()
                TrackableUnit.Minutes -> value.let { "$it\"" }
                TrackableUnit.Kilograms -> value.let { "$it kg" }
            }
        }
}