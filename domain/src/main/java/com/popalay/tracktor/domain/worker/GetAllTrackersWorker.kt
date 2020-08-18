package com.popalay.tracktor.domain.worker

import com.popalay.tracktor.data.TrackingRepository
import com.popalay.tracktor.data.model.TrackerWithRecords
import com.squareup.workflow.Worker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class GetAllTrackersWorker(
    private val trackingRepository: TrackingRepository
) : Worker<List<TrackerWithRecords>> {
    override fun run(): Flow<List<TrackerWithRecords>> = trackingRepository.getAllTrackersWithRecords()
        .flowOn(Dispatchers.IO)
}