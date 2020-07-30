package com.popalay.tracktor.data

import androidx.room.TypeConverter
import com.popalay.tracktor.model.UnitValueType

class UnitValueTypeConverter {
    @TypeConverter
    fun decode(value: String?): UnitValueType? {
        if (value == null) return null
        return UnitValueType.valueOf(value)
    }

    @TypeConverter
    fun encode(value: UnitValueType?): String? = value?.name
}