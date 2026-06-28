package com.example.pharmax.repo

import com.example.pharmax.model.CategoryModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CategoryRepoImpl : CategoryRepo {

    private val ref = FirebaseDatabase.getInstance().getReference("categories")

    override fun addCategory(model: CategoryModel, callback: (Boolean, String) -> Unit) {
        val id = ref.push().key ?: ""
        val newModel = model.copy(categoryId = id, slug = "/slug/${model.name.lowercase().replace(" ", "-")}")
        ref.child(id).setValue(newModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Category added successfully")
                } else {
                    callback(false, task.exception?.message ?: "Failed to add category")
                }
            }
    }

    override fun getAllCategories(callback: (Boolean, String, List<CategoryModel>) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(CategoryModel::class.java) }
                callback(true, "Success", list)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }

    override fun updateCategory(model: CategoryModel, callback: (Boolean, String) -> Unit) {
        ref.child(model.categoryId).setValue(model)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Category updated successfully")
                } else {
                    callback(false, task.exception?.message ?: "Failed to update category")
                }
            }
    }

    override fun deleteCategory(categoryId: String, callback: (Boolean, String) -> Unit) {
        ref.child(categoryId).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Category deleted successfully")
                } else {
                    callback(false, task.exception?.message ?: "Failed to delete category")
                }
            }
    }

    override fun toggleCategoryStatus(categoryId: String, isActive: Boolean, callback: (Boolean, String) -> Unit) {
        ref.child(categoryId).child("isActive").setValue(isActive)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, if (isActive) "Category activated" else "Category deactivated")
                } else {
                    callback(false, task.exception?.message ?: "Failed to update status")
                }
            }
    }
}
