package com.example.pharmax.repo

import com.example.pharmax.model.NotificationModel

interface NotificationRepo {
    fun addNotification(model: NotificationModel, callback: (Boolean, String) -> Unit)
    fun getNotifications(recipientId: String, callback: (Boolean, String, List<NotificationModel>) -> Unit)
    fun markAsRead(notificationId: String, callback: (Boolean, String) -> Unit)
    fun markAllAsRead(recipientId: String, callback: (Boolean, String) -> Unit)
}
