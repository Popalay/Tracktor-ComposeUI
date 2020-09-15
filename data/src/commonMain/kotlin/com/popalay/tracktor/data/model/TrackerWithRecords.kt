package com.popalay.tracktor.data.model

data class TrackerWithRecords(
    val tracker: Tracker,
    val records: List<ValueRecord>,
    val categories: List<Category>
) {
    val currentValue: ValueRecord? get() = records.lastOrNull()

    fun progress(
        previousValue: Double? = records.firstOrNull()?.value,
        currentValue: Double? = records.lastOrNull()?.value
    ): Double {
        return (currentValue ?: 0.0) / (previousValue ?: return 0.0) - 1
    }
}

