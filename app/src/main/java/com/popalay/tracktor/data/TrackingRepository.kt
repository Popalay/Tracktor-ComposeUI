package com.popalay.tracktor.data

import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.model.ValueRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.util.UUID

class TrackingRepository(
    private val trackerDao: TrackerDao,
    private val recordDao: RecordDao
) {
    fun getAllTrackersWithRecords(): Flow<List<TrackerWithRecords>> = trackerDao.getAllTrackersWithRecords()

    fun getTrackerWithRecordsById(id: String): Flow<TrackerWithRecords> = trackerDao.getTrackerWithRecordsById(id)

    suspend fun saveTracker(tracker: Tracker) = trackerDao.insert(tracker)

    suspend fun saveRecord(tracker: Tracker, value: Double) {
        val record = ValueRecord(
            id = UUID.randomUUID().toString(),
            trackerId = tracker.id,
            value = value,
            date = LocalDateTime.now()
        )
        saveRecord(record)
    }

    suspend fun saveRecord(record: ValueRecord) = recordDao.insert(record)

    suspend fun deleteTracker(tracker: Tracker) = trackerDao.delete(tracker)
}