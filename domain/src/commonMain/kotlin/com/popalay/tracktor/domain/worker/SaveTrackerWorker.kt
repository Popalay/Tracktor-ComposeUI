package com.popalay.tracktor.domain.worker

import com.popalay.tracktor.data.TrackingRepository
import com.popalay.tracktor.data.model.TrackableUnit
import com.popalay.tracktor.data.model.Tracker
import com.squareup.workflow.Worker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SaveTrackerWorker(
    private val tracker: Tracker,
    private val initialValue: String,
    private val trackingRepository: TrackingRepository
) : Worker<Unit> {
    override fun run(): Flow<Unit> = flow {
        trackingRepository.saveTracker(tracker)
        if (tracker.unit == TrackableUnit.Word) {
            trackingRepository.saveRecord(tracker, initialValue)
        } else {
            trackingRepository.saveRecord(tracker, initialValue.toDoubleOrNull() ?: 0.0)
        }
        emit(Unit)
    }

    override fun doesSameWorkAs(otherWorker: Worker<*>): Boolean =
        super.doesSameWorkAs(otherWorker) &&
                tracker.title == (otherWorker as? SaveTrackerWorker)?.tracker?.title &&
                initialValue == (otherWorker as? SaveTrackerWorker)?.initialValue
}