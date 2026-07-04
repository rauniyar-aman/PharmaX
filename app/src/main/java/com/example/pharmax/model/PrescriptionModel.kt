package com.example.pharmax.model

data class PrescriptionModel(
    val prescriptionId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhone: String = "",
    val medicineId: String = "",
    val medicineName: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val notes: String = "",
    val status: String = "Pending",
    val adminComment: String = "",
    val uploadedAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "prescriptionId" to prescriptionId,
            "userId" to userId,
            "userName" to userName,
            "userPhone" to userPhone,
            "medicineId" to medicineId,
            "medicineName" to medicineName,
            "name" to name,
            "imageUrl" to imageUrl,
            "notes" to notes,
            "status" to status,
            "adminComment" to adminComment,
            "uploadedAt" to uploadedAt
        )
    }
}
