package com.example.pharmax.repo

import com.example.pharmax.model.CategoryModel

interface CategoryRepo {
    fun addCategory(model: CategoryModel, callback: (Boolean, String) -> Unit)
    fun getAllCategories(callback: (Boolean, String, List<CategoryModel>) -> Unit)
    fun updateCategory(model: CategoryModel, callback: (Boolean, String) -> Unit)
    fun deleteCategory(categoryId: String, callback: (Boolean, String) -> Unit)
    fun toggleCategoryStatus(categoryId: String, isActive: Boolean, callback: (Boolean, String) -> Unit)
    fun checkCategoryNameExists(name: String, excludeId: String = "", callback: (Boolean) -> Unit)
}
