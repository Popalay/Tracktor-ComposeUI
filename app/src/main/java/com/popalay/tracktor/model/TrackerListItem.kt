package com.popalay.tracktor.model

data class TrackerListItem(
    val data: TrackerWithRecords
) : ListItem

fun TrackerWithRecords.toListItem() = TrackerListItem(this)