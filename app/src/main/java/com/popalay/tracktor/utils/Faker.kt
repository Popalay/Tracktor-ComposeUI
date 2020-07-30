package com.popalay.tracktor.utils

import com.popalay.tracktor.model.FeatureFlagListItem
import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.model.TrackerWithRecords
import com.popalay.tracktor.model.ValueRecord
import java.time.LocalDateTime

object Faker {
    fun fakeTracker(
        id: String = "trackerId",
        title: String = "title",
        unit: TrackableUnit = TrackableUnit.Weight,
        date: LocalDateTime = LocalDateTime.now()
    ): Tracker = Tracker(id, title, unit, date)

    fun fakeRecord(
        id: String = "valueId",
        trackerId: String = "trackerId",
        value: Double = 42.3,
        stringValue: String = "value",
        date: LocalDateTime = LocalDateTime.now()
    ): ValueRecord = ValueRecord(id, trackerId, value, stringValue, date)

    fun fakeTrackerWithRecords(
        tracker: Tracker = fakeTracker(),
        records: List<ValueRecord> = List(5) { fakeRecord() }
    ): TrackerWithRecords = TrackerWithRecords(
        tracker = tracker,
        records = records
    )

    fun fakeFeatureFlag(
        id: String = "id",
        displayName: String = "Feature toggle 1",
        isEnabled: Boolean = true
    ) = FeatureFlagListItem(id, displayName, isEnabled)
}