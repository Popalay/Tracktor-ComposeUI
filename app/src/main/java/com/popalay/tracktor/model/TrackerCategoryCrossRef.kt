package com.popalay.tracktor.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["id", "categoryId"],
    indices = [Index("categoryId"), Index("id")],
    foreignKeys = [
        ForeignKey(
            entity = Tracker::class,
            parentColumns = ["id"],
            childColumns = ["id"]
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"]
        )
    ]
)
data class TrackerCategoryCrossRef(
    val id: String,
    val categoryId: String
)