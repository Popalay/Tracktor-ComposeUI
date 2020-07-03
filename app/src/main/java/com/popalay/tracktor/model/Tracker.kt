package com.popalay.tracktor.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Tracker(
    @PrimaryKey val id: String,
    val title: String,
    val unit: TrackableUnit,
    val date: LocalDateTime
)