package com.popalay.tracktor.model

import java.time.LocalDateTime

data class TrackedValue(
    val title: String,
    val records: List<ValueRecord>,
    val unit: TrackableUnit
) {
    constructor(
        title: String,
        record: ValueRecord,
        unit: TrackableUnit
    ) : this(title, listOf(record), unit)

    constructor(
        title: String,
        value: Double,
        unit: TrackableUnit
    ) : this(title, ValueRecord(value, LocalDateTime.now()), unit)

    val currentValue: Double get() = records.firstOrNull()?.value ?: 0.0

    val displayValue: String
        get() = records.map { it.value }.joinToString {
            when (unit) {
                TrackableUnit.None -> ""
                TrackableUnit.Quantity -> it.toInt().toString()
                TrackableUnit.Minutes -> it.let { "$it\"" }
                TrackableUnit.Kilograms -> it.let { "$it kg" }
            }
        }
}