package com.popalay.tracktor.utils

import com.popalay.tracktor.data.model.Category
import com.popalay.tracktor.data.model.FeatureFlagListItem
import com.popalay.tracktor.data.model.ProgressDirection
import com.popalay.tracktor.data.model.TrackableUnit
import com.popalay.tracktor.data.model.Tracker
import com.popalay.tracktor.data.model.TrackerWithRecords
import com.popalay.tracktor.data.model.ValueRecord
import java.time.LocalDateTime

object Faker {
    fun fakeTracker(
        id: String = "trackerId",
        title: String = "title",
        unit: TrackableUnit = TrackableUnit.Weight,
        direction: ProgressDirection = ProgressDirection.ASCENDING,
        date: LocalDateTime = LocalDateTime.now()
    ): Tracker = Tracker(id, title, unit, direction, date)

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
        records = records,
        categories = List(5) { fakeCategory() }
    )

    fun fakeCategory(
        id: String = "id",
        name: String = "Category"
    ) = Category(id, name)

    fun fakeFeatureFlag(
        id: String = "id",
        displayName: String = "Feature toggle 1",
        isEnabled: Boolean = true
    ) = FeatureFlagListItem(id, displayName, isEnabled)
}