package com.example.pharmax.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pharmax.model.NotificationModel
import com.example.pharmax.repo.NotificationRepo
import com.example.pharmax.repo.NotificationRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class NotificationViewModel(private val repo: NotificationRepo = NotificationRepoImpl()) : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationModel>>(emptyList())
    val notifications: StateFlow<List<NotificationModel>> = _notifications.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    val unreadCount: StateFlow<Int> = _notifications
        .map { list -> list.count { !it.isRead } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun loadNotifications(recipientId: String) {
        if (recipientId.isBlank()) return
        _loading.value = true
        repo.getNotifications(recipientId) { success, _, list ->
            _loading.value = false
            if (success) _notifications.value = list
        }
    }

    fun notify(recipientId: String, title: String, message: String, type: String, referenceId: String = "") {
        if (recipientId.isBlank()) return
        repo.addNotification(
            NotificationModel(
                recipientId = recipientId,
                title = title,
                message = message,
                type = type,
                referenceId = referenceId
            )
        ) { _, _ -> }
    }

    fun markAsRead(notificationId: String) {
        repo.markAsRead(notificationId) { _, _ -> }
    }

    fun markAllAsRead(recipientId: String) {
        repo.markAllAsRead(recipientId) { _, _ -> }
    }
}
