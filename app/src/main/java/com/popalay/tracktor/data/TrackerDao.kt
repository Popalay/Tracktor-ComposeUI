package com.popalay.tracktor.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.model.TrackerWithRecords
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackerDao {
    @Transaction
    @Query("SELECT * FROM tracker ORDER by date DESC")
    fun getAllTrackerWithRecords(): Flow<List<TrackerWithRecords>>

    @Insert
    suspend fun insert(value: Tracker)

    @Insert
    fun insertSync(value: Tracker)

    @Insert
    suspend fun insertAll(values: Tracker)

    @Delete
    suspend fun delete(value: Tracker)
}