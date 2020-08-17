package com.popalay.tracktor.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.popalay.tracktor.data.model.Category
import com.popalay.tracktor.data.model.TrackerCategoryCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Transaction
    @Query("SELECT * FROM category ORDER by name DESC")
    fun getAll(): Flow<List<Category>>

    @Insert
    suspend fun insert(value: Category)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(values: List<TrackerCategoryCrossRef>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllRefs(values: List<Category>)

    @Delete
    suspend fun delete(value: Category)

    @Query("DELETE FROM trackercategorycrossref WHERE id = :trackerId")
    suspend fun deleteAllByTrackerId(trackerId: String)

    @Transaction
    suspend fun updateForTracker(trackerId: String, categories: List<Category>) {
        deleteAllByTrackerId(trackerId)
        insertAllRefs(categories)
        insertAll(categories.map { TrackerCategoryCrossRef(trackerId, it.categoryId) })
    }
}