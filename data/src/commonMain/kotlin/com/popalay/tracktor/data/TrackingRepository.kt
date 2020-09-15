package com.popalay.tracktor.data

import com.benasher44.uuid.uuid4
import com.popalay.tracktor.data.converter.map
import com.popalay.tracktor.data.extensions.now
import com.popalay.tracktor.data.model.Tracker
import com.popalay.tracktor.data.model.TrackerWithRecords
import com.popalay.tracktor.data.model.ValueRecord
import com.popalay.tracktor.db.TrackerQueries
import com.popalay.tracktor.db.ValueRecordQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class TrackingRepository(
    private val trackerDao: TrackerQueries,
    private val recordDao: ValueRecordQueries,
    private val categoryRepository: CategoryRepository
) {
    fun getAllTrackersWithRecords(): Flow<List<TrackerWithRecords>> = trackerDao.getAll().asFlow().mapToList()
        .flatMapLatest { trackers -> combine(trackers.map { fetchDataForTracker(it.map()) }) { it.toList() } }

    fun getTrackerWithRecordsById(id: String): Flow<TrackerWithRecords> = trackerDao.getById(id).asFlow().mapToOne()
        .flatMapLatest { fetchDataForTracker(it.map()) }

    suspend fun saveTracker(tracker: Tracker) = withContext(Dispatchers.Default) {
        trackerDao.insert(tracker.map())
    }

    suspend fun saveRecord(tracker: Tracker, value: Double) = withContext(Dispatchers.Default) {
        val record = ValueRecord(
            id = uuid4().toString(),
            trackerId = tracker.id,
            value = value,
            stringValue = "",
            date = LocalDateTime.now()
        )
        saveRecord(record)
    }

    suspend fun saveRecord(tracker: Tracker, value: String) = withContext(Dispatchers.Default) {
        val size = recordDao.getAllByTrackerId(tracker.id).executeAsList().size
        val record = ValueRecord(
            id = uuid4().toString(),
            trackerId = tracker.id,
            value = size + 1.0,
            stringValue = value,
            date = LocalDateTime.now()
        )
        saveRecord(record)
    }

    suspend fun saveRecord(record: ValueRecord) = withContext(Dispatchers.Default) {
        recordDao.insert(record.map())
    }

    suspend fun deleteTracker(tracker: Tracker) = withContext(Dispatchers.Default) {
        trackerDao.deleteById(tracker.id)
    }

    suspend fun softDeleteTracker(trackerId: String) = withContext(Dispatchers.Default) {
        trackerDao.softDeleteById(trackerId)
    }

    suspend fun deleteRecord(record: ValueRecord) = withContext(Dispatchers.Default) {
        recordDao.deleteById(record.id)
    }

    suspend fun restoreTracker(trackerId: String) = withContext(Dispatchers.Default) {
        trackerDao.restoreById(trackerId)
    }

    suspend fun restoreRecord(record: ValueRecord) = withContext(Dispatchers.Default) {
        recordDao.insert(record.map())
    }

    private fun fetchDataForTracker(tracker: Tracker) = recordDao.getAllByTrackerId(tracker.id).asFlow().mapToList()
        .combine(categoryRepository.getAllByTracker(tracker.id)) { records, categories ->
            TrackerWithRecords(tracker, records.map { it.map() }, categories)
        }
}