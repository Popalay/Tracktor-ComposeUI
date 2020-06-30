package com.popalay.tracktor.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Tracker(
    @PrimaryKey val id: String,
    val title: String,
    val unit: TrackableUnit
)