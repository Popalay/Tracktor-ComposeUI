package com.popalay.tracktor.domain.worker

import com.popalay.tracktor.data.CategoryRepository
import com.popalay.tracktor.data.model.Category
import com.squareup.workflow.Worker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class GetAllCategoriesWorker(
    private val categoryRepository: CategoryRepository
) : Worker<List<Category>> {
    override fun run(): Flow<List<Category>> = categoryRepository.getAll()
        .map { categories -> listOf(Category.All).plus(categories).plus(Category.defaultList()).distinct().sortedBy { it.name } }
        .flowOn(Dispatchers.IO)
}