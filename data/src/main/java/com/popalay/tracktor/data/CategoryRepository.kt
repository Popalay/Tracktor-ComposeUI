package com.popalay.tracktor.data

import com.popalay.tracktor.data.model.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class CategoryRepository(
    private val categoryDao: CategoryDao
) {
    fun getAll(): Flow<List<Category>> = categoryDao.getAll()
        .flowOn(Dispatchers.IO)

    suspend fun saveCategories(trackerId: String, categories: List<Category>) = withContext(Dispatchers.IO) {
        categoryDao.updateForTracker(trackerId, categories)
    }
}