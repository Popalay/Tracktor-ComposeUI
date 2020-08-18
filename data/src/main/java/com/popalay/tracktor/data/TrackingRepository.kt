package com.popalay.tracktor.data

import com.popalay.tracktor.data.model.Tracker
import com.popalay.tracktor.data.model.TrackerWithRecords
import com.popalay.tracktor.data.model.ValueRecord
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
            stringValue = "",
            date = LocalDateTime.now()
        )
        saveRecord(record)
    }

    suspend fun saveRecord(tracker: Tracker, value: String) {
        val size = recordDao.getAllByTrackerId(tracker.id).size
        val record = ValueRecord(
            id = UUID.randomUUID().toString(),
            trackerId = tracker.id,
            value = size + 1.0,
            stringValue = value,
            date = LocalDateTime.now()
        )
        saveRecord(record)
    }

    suspend fun saveRecord(record: ValueRecord) = recordDao.insert(record)

    suspend fun deleteTracker(tracker: Tracker) = trackerDao.delete(tracker)

    suspend fun deleteRecord(record: ValueRecord) = recordDao.delete(record)

    suspend fun restoreTracker(trackerWithRecords: TrackerWithRecords) {
        trackerDao.insert(trackerWithRecords.tracker)
        recordDao.insertAll(trackerWithRecords.records)
    }

    suspend fun restoreRecord(record: ValueRecord) {
        recordDao.insert(record)
    }
}