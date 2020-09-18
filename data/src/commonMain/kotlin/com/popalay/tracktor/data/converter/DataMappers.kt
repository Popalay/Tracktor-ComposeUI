package com.popalay.tracktor.data.converter

import com.popalay.tracktor.data.model.Category
import com.popalay.tracktor.data.model.TrackableUnit
import com.popalay.tracktor.data.model.Tracker
import com.popalay.tracktor.data.model.ValueRecord
import com.popalay.tracktor.db.Category as CategoryDto
import com.popalay.tracktor.db.Tracker as TrackerDto
import com.popalay.tracktor.db.ValueRecord as ValueRecordDto

fun Tracker.map() = TrackerDto(
    id = id,
    title = title,
    direction = direction,
    date = date,
    unitName = unit.name,
    unitSymbol = unit.symbol,
    unitValueType = unit.valueType,
    isDeleted = isDeleted,
)

fun TrackerDto.map() = Tracker(
    id = id,
    title = title,
    direction = direction,
    date = date,
    unit = TrackableUnit(
        name = unitName,
        symbol = unitSymbol,
        valueType = unitValueType
    ),
    isDeleted = isDeleted,
)

fun ValueRecord.map() = ValueRecordDto(
    id = id,
    trackerId = trackerId,
    value = value,
    stringValue = stringValue,
    date = date,
)

fun ValueRecordDto.map() = ValueRecord(
    id = id,
    trackerId = trackerId,
    value = value,
    stringValue = stringValue,
    date = date,
)

fun Category.map() = CategoryDto(
    categoryId = categoryId,
    name = name
)

fun CategoryDto.map() = Category(
    categoryId = categoryId,
    name = name
)