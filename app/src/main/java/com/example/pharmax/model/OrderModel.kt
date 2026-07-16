package com.example.pharmax.model

data class OrderModel(
    val orderId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhone: String = "",
    val medicineId: String = "",
    val medicineName: String = "",
    val medicineType: String = "OTC",
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val totalAmount: Double = 0.0,
    val prescriptionId: String = "",
    val paymentMethod: String = "Khalti",
    val paymentStatus: String = "Pending",
    val orderStatus: String = "Confirmed",
    val transactionId: String = "",
    val orderedAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "orderId" to orderId,
            "userId" to userId,
            "userName" to userName,
            "userPhone" to userPhone,
            "medicineId" to medicineId,
            "medicineName" to medicineName,
            "medicineType" to medicineType,
            "quantity" to quantity,
            "unitPrice" to unitPrice,
            "totalAmount" to totalAmount,
            "prescriptionId" to prescriptionId,
            "paymentMethod" to paymentMethod,
            "paymentStatus" to paymentStatus,
            "orderStatus" to orderStatus,
            "transactionId" to transactionId,
            "orderedAt" to orderedAt
        )
    }
}
