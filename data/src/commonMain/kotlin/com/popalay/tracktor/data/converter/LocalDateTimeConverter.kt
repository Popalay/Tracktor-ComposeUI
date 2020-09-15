package com.popalay.tracktor.data.converter

import com.squareup.sqldelight.ColumnAdapter
import kotlinx.datetime.LocalDateTime

class LocalDateTimeConverter : ColumnAdapter<LocalDateTime, String> {
    override fun decode(databaseValue: String): LocalDateTime = LocalDateTime.parse(databaseValue)
    override fun encode(value: LocalDateTime): String = value.toString()
}