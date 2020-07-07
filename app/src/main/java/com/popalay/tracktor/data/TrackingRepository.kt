package com.popalay.tracktor.data

import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.model.ValueRecord
import kotlinx.coroutines.flow.Flow

class TrackingRepository(
    private val trackerDao: TrackerDao,
    private val recordDao: RecordDao
) {
    fun getAllTrackerWithRecords(): Flow<List<TrackerWithRecords>> = trackerDao.getAllTrackerWithRecords()

    suspend fun saveTracker(tracker: Tracker) = trackerDao.insert(tracker)

    suspend fun saveRecord(record: ValueRecord) = recordDao.insert(record)

    suspend fun deleteTracker(tracker: Tracker) = trackerDao.delete(tracker)
}