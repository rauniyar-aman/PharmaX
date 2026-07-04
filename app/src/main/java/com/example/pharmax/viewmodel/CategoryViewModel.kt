package com.example.pharmax.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pharmax.model.CategoryModel
import com.example.pharmax.repo.CategoryRepo
import com.example.pharmax.repo.CategoryRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoryViewModel(private val repo: CategoryRepo = CategoryRepoImpl()) : ViewModel() {

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _categories = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categories: StateFlow<List<CategoryModel>> = _categories.asStateFlow()

    fun clearMessage() {
        _message.value = null
    }

    fun loadCategories() {
        _loading.value = true
        repo.getAllCategories { success, msg, list ->
            _loading.value = false
            if (success) {
                _categories.value = list
            } else {
                _message.value = msg
            }
        }
    }

    fun addCategory(name: String, description: String, icon: String, isActive: Boolean, onSuccess: () -> Unit) {
        if (name.isBlank()) {
            _message.value = "Category name is required"
            return
        }
        _loading.value = true
        repo.checkCategoryNameExists(name) { exists ->
            if (exists) {
                _loading.value = false
                _message.value = "A category with this name already exists"
                return@checkCategoryNameExists
            }
            val model = CategoryModel(
                name = name,
                description = description,
                icon = icon,
                isActive = isActive
            )
            repo.addCategory(model) { success, msg ->
                _loading.value = false
                _message.value = msg
                if (success) onSuccess()
            }
        }
    }

    fun updateCategory(model: CategoryModel, onSuccess: () -> Unit) {
        if (model.name.isBlank()) {
            _message.value = "Category name is required"
            return
        }
        _loading.value = true
        repo.checkCategoryNameExists(model.name, excludeId = model.categoryId) { exists ->
            if (exists) {
                _loading.value = false
                _message.value = "A category with this name already exists"
                return@checkCategoryNameExists
            }
            repo.updateCategory(model) { success, msg ->
                _loading.value = false
                _message.value = msg
                if (success) onSuccess()
            }
        }
    }

    fun deleteCategory(categoryId: String, onSuccess: () -> Unit) {
        _loading.value = true
        repo.deleteCategory(categoryId) { success, msg ->
            _loading.value = false
            _message.value = msg
            if (success) onSuccess()
        }
    }

    fun toggleStatus(categoryId: String, isActive: Boolean) {
        repo.toggleCategoryStatus(categoryId, isActive) { success, msg ->
            if (!success) _message.value = msg
        }
    }
}
