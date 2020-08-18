package com.popalay.tracktor.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Tracker::class,
            parentColumns = ["id"],
            childColumns = ["trackerId"],
            onUpdate = ForeignKey.NO_ACTION,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ValueRecord(
    @PrimaryKey val id: String,
    val trackerId: String,
    val value: Double,
    val stringValue: String,
    val date: LocalDateTime
)