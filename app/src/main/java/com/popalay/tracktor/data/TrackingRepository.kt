package com.popalay.tracktor.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.model.ValueRecord
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Executors

object TrackingRepository {
    private lateinit var dataBase: AppDatabase

    fun init(context: Context) {
        dataBase = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "database-name"
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    Executors.newSingleThreadScheduledExecutor()
                        .execute {
                            dataBase.trackerDao().insertSync(Tracker("id", "title", TrackableUnit.Kilograms))
                        }
                }
            })
            .build()
    }

    fun getAllTrackerWithRecords(): Flow<List<TrackerWithRecords>> = dataBase.trackerDao().getAllTrackerWithRecords()

    suspend fun saveTracker(tracker: Tracker) = dataBase.trackerDao().insert(tracker)

    suspend fun saveRecord(record: ValueRecord) = dataBase.recordDao().insert(record)

    suspend fun deleteTracker(tracker: Tracker) = dataBase.trackerDao().delete(tracker)
}