package com.popalay.tracktor.domain.formatter

import com.popalay.tracktor.data.model.TrackableUnit
import com.popalay.tracktor.data.model.Tracker
import com.popalay.tracktor.data.model.UnitValueType
import com.popalay.tracktor.data.model.ValueRecord

interface ValueRecordFormatter {
    fun isForUnit(unit: TrackableUnit): Boolean
    fun format(tracker: Tracker, record: ValueRecord?): String
}

class ValueRecordFormatterFacade(
    private val formatters: Set<ValueRecordFormatter>
) : ValueRecordFormatter {
    override fun isForUnit(unit: TrackableUnit): Boolean = formatters.any { it.isForUnit(unit) }

    override fun format(tracker: Tracker, record: ValueRecord?): String =
        formatters.first { it.isForUnit(tracker.compatibleUnit) }.format(tracker, record)
}

class NumberValueRecordFormatter : ValueRecordFormatter {
    override fun isForUnit(unit: TrackableUnit): Boolean =
        unit.valueType == UnitValueType.DOUBLE || unit.valueType == UnitValueType.INTEGER

    override fun format(tracker: Tracker, record: ValueRecord?): String {
        val value = record?.value ?: 0.0
        val roundedValue: Number = if (value == value.toInt().toDouble()) value.toInt() else value
        return "$roundedValue${tracker.compatibleUnit.symbol}"
    }
}

class TextValueRecordFormatter : ValueRecordFormatter {
    override fun isForUnit(unit: TrackableUnit): Boolean = unit.valueType == UnitValueType.TEXT

    override fun format(tracker: Tracker, record: ValueRecord?): String = record?.stringValue ?: "~"
}