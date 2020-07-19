package com.popalay.tracktor.domain.formatter

import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.model.ValueRecord

interface ValueRecordFormatter {
    fun isForUnit(unit: TrackableUnit): Boolean
    fun format(tracker: Tracker, record: ValueRecord?): String
}

class ValueRecordFormatterFacade(
    private val formatters: Set<ValueRecordFormatter>
) : ValueRecordFormatter {
    override fun isForUnit(unit: TrackableUnit): Boolean = formatters.any { it.isForUnit(unit) }

    override fun format(tracker: Tracker, record: ValueRecord?): String =
        formatters.first { it.isForUnit(tracker.unit) }.format(tracker, record)
}

class NumberValueRecordFormatter : ValueRecordFormatter {
    override fun isForUnit(unit: TrackableUnit): Boolean = unit == TrackableUnit.None ||
            unit == TrackableUnit.Quantity || unit == TrackableUnit.Minutes || unit == TrackableUnit.Kilograms

    override fun format(tracker: Tracker, record: ValueRecord?): String {
        val value = record?.value ?: 0.0
        val roundedValue: Number = if (value == value.toInt().toDouble()) value.toInt() else value
        return when (tracker.unit) {
            TrackableUnit.None -> ""
            TrackableUnit.Quantity -> roundedValue.toInt().toString()
            TrackableUnit.Minutes -> roundedValue.let { "$it\"" }
            TrackableUnit.Kilograms -> roundedValue.let { "$it kg" }
            else -> throw IllegalArgumentException("Unit ${tracker.unit} is not supported")
        }
    }
}

class StringValueRecordFormatter : ValueRecordFormatter {
    override fun isForUnit(unit: TrackableUnit): Boolean = unit == TrackableUnit.Word

    override fun format(tracker: Tracker, record: ValueRecord?): String = record?.stringValue ?: "~"
}