package com.popalay.tracktor.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.model.ValueRecord

@TypeConverters(value = [LocalDateTimeConverter::class, TrackableUnitConverter::class])
@Database(entities = [Tracker::class, ValueRecord::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackerDao(): TrackerDao
    abstract fun recordDao(): RecordDao
}