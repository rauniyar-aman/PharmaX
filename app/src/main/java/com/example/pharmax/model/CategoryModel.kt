package com.example.pharmax.model

data class CategoryModel(
    val categoryId: String = "",
    val name: String = "",
    val slug: String = "",
    val description: String = "",
    val icon: String = "💊",
    val type: String = "OTC",
    val isActive: Boolean = true,
    val medicineCount: Int = 0
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "categoryId" to categoryId,
            "name" to name,
            "slug" to slug,
            "description" to description,
            "icon" to icon,
            "type" to type,
            "isActive" to isActive,
            "medicineCount" to medicineCount
        )
    }
}
