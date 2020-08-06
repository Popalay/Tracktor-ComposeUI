package com.popalay.tracktor.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.popalay.tracktor.model.ValueRecord

@Dao
interface RecordDao {
    @Insert
    suspend fun insert(value: ValueRecord)

    @Insert
    suspend fun insertAll(value: List<ValueRecord>)

    @Query("SELECT * FROM ValueRecord WHERE trackerId=:trackerId")
    suspend fun getAllByTrackerId(trackerId: String): List<ValueRecord>

    @Delete
    suspend fun delete(value: ValueRecord)
}