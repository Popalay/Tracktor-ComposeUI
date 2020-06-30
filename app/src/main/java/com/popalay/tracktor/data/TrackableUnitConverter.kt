package com.popalay.tracktor.data

import androidx.room.TypeConverter
import com.popalay.tracktor.model.TrackableUnit

class TrackableUnitConverter {
    @TypeConverter
    fun decode(value: String?): TrackableUnit? {
        if (value == null) return null
        return TrackableUnit.valueOf(value)
    }

    @TypeConverter
    fun encode(value: TrackableUnit?): String? = value?.name
}