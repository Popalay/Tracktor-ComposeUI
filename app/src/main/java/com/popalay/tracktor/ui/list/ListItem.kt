package com.popalay.tracktor.ui.list

import com.popalay.tracktor.model.TrackerWithRecords

data class ListItem(
    val data: TrackerWithRecords
)

fun TrackerWithRecords.toListItem() = ListItem(this)