package com.example.pharmax.model

data class MedicineModel(
    val medicineId: String = "",
    val name: String = "",
    val brand: String = "",
    val category: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val dosage: String = "",
    val requiresPrescription: Boolean = false,
    val type: String = "OTC",
    val ingredients: List<String> = emptyList(),
    val howToUse: String = "",
    val imageUrl: String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "medicineId" to medicineId,
            "name" to name,
            "brand" to brand,
            "category" to category,
            "description" to description,
            "price" to price,
            "stock" to stock,
            "dosage" to dosage,
            "requiresPrescription" to requiresPrescription,
            "type" to type,
            "ingredients" to ingredients,
            "howToUse" to howToUse,
            "imageUrl" to imageUrl
        )
    }
}
