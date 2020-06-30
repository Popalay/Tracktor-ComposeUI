package com.popalay.tracktor.data

import androidx.room.TypeConverter
import java.time.LocalDateTime

class LocalDateTimeConverter {
    @TypeConverter
    fun decode(value: String?): LocalDateTime? {
        if (value == null) return null
        return LocalDateTime.parse(value)
    }

    @TypeConverter
    fun encode(value: LocalDateTime?): String? = value?.toString()
}