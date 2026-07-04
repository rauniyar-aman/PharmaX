package com.example.pharmax.model

import com.google.firebase.database.PropertyName

data class CategoryModel(
    val categoryId: String = "",
    val name: String = "",
    val slug: String = "",
    val description: String = "",
    val icon: String = "💊",
    @get:PropertyName("isActive")
    @set:PropertyName("isActive")
    var isActive: Boolean = true
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "categoryId" to categoryId,
            "name" to name,
            "slug" to slug,
            "description" to description,
            "icon" to icon,
            "isActive" to isActive
        )
    }
}
