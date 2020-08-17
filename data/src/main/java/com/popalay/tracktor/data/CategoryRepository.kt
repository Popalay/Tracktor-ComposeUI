package com.popalay.tracktor.data

import com.popalay.tracktor.data.model.Category
import kotlinx.coroutines.flow.Flow

class CategoryRepository(
    private val categoryDao: CategoryDao
) {
    fun getAll(): Flow<List<Category>> = categoryDao.getAll()

    suspend fun saveCategories(trackerId: String, categories: List<Category>) {
        categoryDao.updateForTracker(trackerId, categories)
    }
}