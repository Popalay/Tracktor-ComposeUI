package com.popalay.tracktor.ui.list

import com.popalay.tracktor.model.TrackerWithRecords

data class TrackerListItem(
    val data: TrackerWithRecords
)

fun TrackerWithRecords.toListItem() = TrackerListItem(this)