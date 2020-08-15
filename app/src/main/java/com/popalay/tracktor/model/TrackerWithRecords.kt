package com.popalay.tracktor.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.Relation

data class TrackerWithRecords(
    @Embedded val tracker: Tracker,
    @Relation(
        parentColumn = "id",
        entityColumn = "trackerId"
    )
    val records: List<ValueRecord>,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId",
        associateBy = Junction(TrackerCategoryCrossRef::class)
    )
    val categories: List<Category>
) {
    val currentValue: ValueRecord? get() = records.lastOrNull()

    fun progress(
        previousValue: Double? = records.firstOrNull()?.value,
        currentValue: Double? = records.lastOrNull()?.value
    ): Double {
        return (currentValue ?: 0.0) / (previousValue ?: return 0.0) - 1
    }
}

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