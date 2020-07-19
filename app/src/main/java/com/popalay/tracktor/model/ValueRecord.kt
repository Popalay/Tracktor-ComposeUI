package com.popalay.tracktor.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class ValueRecord(
    @PrimaryKey val id: String,
    val trackerId: String,
    val value: Double,
    val stringValue: String,
    val date: LocalDateTime
)