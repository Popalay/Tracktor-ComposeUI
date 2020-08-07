package com.popalay.tracktor.data.converter

import androidx.room.TypeConverter
import com.popalay.tracktor.model.ProgressDirection

class ProgressDirectionConverter {
    @TypeConverter
    fun decode(value: String?): ProgressDirection? {
        if (value == null) return null
        return ProgressDirection.valueOf(value)
    }

    @TypeConverter
    fun encode(value: ProgressDirection?): String? = value?.name
}