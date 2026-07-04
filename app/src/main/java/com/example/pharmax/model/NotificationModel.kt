package com.example.pharmax.model

import com.google.firebase.database.PropertyName

data class NotificationModel(
    val notificationId: String = "",
    val recipientId: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "",
    val referenceId: String = "",
    @get:PropertyName("isRead")
    @set:PropertyName("isRead")
    var isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "notificationId" to notificationId,
            "recipientId" to recipientId,
            "title" to title,
            "message" to message,
            "type" to type,
            "referenceId" to referenceId,
            "isRead" to isRead,
            "createdAt" to createdAt
        )
    }
}
