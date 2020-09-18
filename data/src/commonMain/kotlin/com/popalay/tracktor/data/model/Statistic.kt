package com.popalay.tracktor.data.model

import com.popalay.tracktor.data.model.ProgressDirection.ASCENDING
import com.popalay.tracktor.data.model.ProgressDirection.DESCENDING
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

data class Statistic(
    val trackerCount: Int,
    val trackersWithProgress: Int,
    val lastUpdatedTracker: Tracker,
    val lastUpdatedTrackerDate: LocalDateTime,
    val mostProgressiveTracker: Tracker
) {
    val overallProgress: Double get() = trackersWithProgress.toDouble() / trackerCount

    companion object {
        fun generateFor(trackers: List<TrackerWithRecords>): Statistic? {
            if (trackers.isEmpty()) return null
            val trackerCount = trackers.size
            val trackersWithProgress = trackers.count {
                it.progress() > 0 && it.tracker.direction == ASCENDING ||
                        it.progress() < 0 && it.tracker.direction == DESCENDING
            }
            val lastUpdatedTracker = trackers.maxByOrNull { trackerWithRecords ->
                trackerWithRecords.records.map { it.date.toInstant(TimeZone.UTC).epochSeconds }.maxOrNull() ?: 0
            }
            val lastUpdatedTrackerDate = lastUpdatedTracker?.let {
                lastUpdatedTracker.records.maxByOrNull { it.date.toInstant(TimeZone.UTC).epochSeconds }?.date ?: lastUpdatedTracker.tracker.date
            }
            val mostProgressiveTracker = trackers.maxByOrNull {
                it.progress() * if (it.tracker.direction == DESCENDING) -1 else 1
            }
            return Statistic(
                trackerCount = trackerCount,
                trackersWithProgress = trackersWithProgress,
                lastUpdatedTracker = lastUpdatedTracker!!.tracker,
                lastUpdatedTrackerDate = lastUpdatedTrackerDate ?: LocalDateTime.parse(""),
                mostProgressiveTracker = mostProgressiveTracker!!.tracker
            )
        }
    }
}