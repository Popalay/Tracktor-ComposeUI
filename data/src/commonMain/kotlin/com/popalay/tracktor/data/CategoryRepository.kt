package com.popalay.tracktor.data

import com.popalay.tracktor.data.converter.map
import com.popalay.tracktor.data.model.Category
import com.popalay.tracktor.db.CategoryQueries
import com.popalay.tracktor.db.CategoryTrackerRef
import com.popalay.tracktor.db.CategoryTrackerRefQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryRepository(
    private val categoryDao: CategoryQueries,
    private val categoryTrackerRefDao: CategoryTrackerRefQueries
) {
    fun getAll(): Flow<List<Category>> = categoryDao.getAll { categoryId, name -> Category(categoryId, name) }.asFlow().mapToList()

    fun getAllByTracker(trackerId: String): Flow<List<Category>> = categoryTrackerRefDao.getAllByTrackerId(trackerId).asFlow().mapToList()
        .map { refs ->
            refs.map {
                categoryDao.getById(it.categoryId) { categoryId, name -> Category(categoryId, name) }.executeAsOne()
            }
        }

    suspend fun saveCategories(trackerId: String, categories: List<Category>) = withContext(Dispatchers.Default) {
        categoryTrackerRefDao.transaction {
            categoryTrackerRefDao.deleteAllByTracker(trackerId)
            categories.forEach {
                categoryDao.insert(it.map())
                categoryTrackerRefDao.insert(CategoryTrackerRef(trackerId, it.categoryId))
            }
        }
    }
}