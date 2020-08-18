package com.popalay.tracktor.domain.worker

import com.popalay.tracktor.data.TrackingRepository
import com.popalay.tracktor.data.model.TrackerWithRecords
import com.squareup.workflow.Worker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull

class GetTrackerByIdWorker(
    private val trackerId: String,
    private val trackingRepository: TrackingRepository
) : Worker<TrackerWithRecords> {
    override fun run(): Flow<TrackerWithRecords> = trackingRepository.getTrackerWithRecordsById(trackerId).filterNotNull()

    override fun doesSameWorkAs(otherWorker: Worker<*>): Boolean =
        super.doesSameWorkAs(otherWorker) && trackerId == (otherWorker as? GetTrackerByIdWorker)?.trackerId
}