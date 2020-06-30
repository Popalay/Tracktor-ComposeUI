package com.popalay.tracktor.data

import androidx.room.Dao
import androidx.room.Insert
import com.popalay.tracktor.model.ValueRecord

@Dao
interface RecordDao {
    @Insert
    suspend fun insert(value: ValueRecord)
}