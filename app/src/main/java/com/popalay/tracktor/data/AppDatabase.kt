package com.popalay.tracktor.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.model.ValueRecord

@TypeConverters(value = [LocalDateTimeConverter::class, UnitValueTypeConverter::class])
@Database(entities = [Tracker::class, ValueRecord::class], version = 4)
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

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("BEGIN TRANSACTION;")
        // Create the new table
        database.execSQL(
            "CREATE TABLE tracker_new (" +
                    "id TEXT NOT NULL," +
                    "title TEXT NOT NULL," +
                    "date TEXT NOT NULL," +
                    "name TEXT NOT NULL," +
                    "symbol TEXT NOT NULL DEFAULT ''," +
                    "valueType TEXT NOT NULL DEFAULT 'DOUBLE'," +
                    "PRIMARY KEY(id)" +
                    ")"
        )
        // Copy the data
        database.execSQL(
            "INSERT INTO tracker_new (id, title, date, name) "
                    + "SELECT id, title, date, tracker.unit "
                    + "FROM tracker"
        )
        // Remove the old table
        database.execSQL("DROP TABLE tracker")
        // Change the table name to the correct one
        database.execSQL("ALTER TABLE tracker_new RENAME TO tracker")
        database.execSQL("COMMIT;")
    }
}