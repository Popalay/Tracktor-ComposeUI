package com.popalay.tracktor.data

import com.popalay.tracktor.data.model.Tracker
import com.popalay.tracktor.data.model.TrackerWithRecords
import com.popalay.tracktor.data.model.ValueRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.UUID

class TrackingRepository(
    private val trackerDao: TrackerDao,
    private val recordDao: RecordDao,
    private val categoryDao: CategoryDao
) {
    fun getAllTrackersWithRecords(): Flow<List<TrackerWithRecords>> = trackerDao.getAllTrackersWithRecords()
        .flowOn(Dispatchers.IO)

    fun getTrackerWithRecordsById(id: String): Flow<TrackerWithRecords> = trackerDao.getTrackerWithRecordsById(id)
        .flowOn(Dispatchers.IO)

    suspend fun saveTracker(tracker: Tracker) = withContext(Dispatchers.IO) {
        trackerDao.insert(tracker)
    }

    suspend fun saveRecord(tracker: Tracker, value: Double) = withContext(Dispatchers.IO) {
        val record = ValueRecord(
            id = UUID.randomUUID().toString(),
            trackerId = tracker.id,
            value = value,
            stringValue = "",
            date = LocalDateTime.now()
        )
        saveRecord(record)
    }

    suspend fun saveRecord(tracker: Tracker, value: String) = withContext(Dispatchers.IO) {
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

    suspend fun saveRecord(record: ValueRecord) = withContext(Dispatchers.IO) {
        recordDao.insert(record)
    }

    suspend fun deleteTracker(tracker: Tracker) = withContext(Dispatchers.IO) {
        trackerDao.delete(tracker)
    }

    suspend fun softDeleteTracker(trackerId: String) = withContext(Dispatchers.IO) {
        trackerDao.softDelete(trackerId)
    }

    suspend fun deleteRecord(record: ValueRecord) = withContext(Dispatchers.IO) {
        recordDao.delete(record)
    }

    suspend fun restoreTracker(trackerId: String) = withContext(Dispatchers.IO) {
        trackerDao.restore(trackerId)
    }

    suspend fun restoreRecord(record: ValueRecord) = withContext(Dispatchers.IO) {
        recordDao.insert(record)
    }
}