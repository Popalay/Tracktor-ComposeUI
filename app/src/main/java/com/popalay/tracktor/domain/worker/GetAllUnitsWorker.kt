package com.popalay.tracktor.domain.worker

import com.popalay.tracktor.data.model.TrackableUnit
import com.squareup.workflow.Worker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetAllUnitsWorker : Worker<List<TrackableUnit>> {
    override fun run(): Flow<List<TrackableUnit>> = flowOf(
        listOf(
            TrackableUnit.Quantity,
            TrackableUnit.Time,
            TrackableUnit.Weight,
            TrackableUnit.Speed,
            TrackableUnit.Energy,
            TrackableUnit.Word,
            TrackableUnit.Length,
            TrackableUnit.Temperature
        )
    )
}