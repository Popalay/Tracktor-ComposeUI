package com.popalay.tracktor.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.model.ValueRecord

@TypeConverters(value = [LocalDateTimeConverter::class, TrackableUnitConverter::class])
@Database(entities = [Tracker::class, ValueRecord::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackerDao(): TrackerDao
    abstract fun recordDao(): RecordDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE tracker ADD COLUMN date TEXT NOT NULL DEFAULT '2020-07-02T10:15:30'")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE ValueRecord ADD COLUMN stringValue TEXT NOT NULL DEFAULT ''")
    }
}