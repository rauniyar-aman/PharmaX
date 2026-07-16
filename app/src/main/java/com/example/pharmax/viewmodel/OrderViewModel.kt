package com.example.pharmax.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pharmax.model.OrderModel
import com.example.pharmax.repo.NotificationRepo
import com.example.pharmax.repo.NotificationRepoImpl
import com.example.pharmax.repo.OrderRepo
import com.example.pharmax.repo.OrderRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

const val ADMIN_NOTIFICATION_BUCKET = "admin"

class OrderViewModel(
    private val repo: OrderRepo = OrderRepoImpl(),
    private val notificationRepo: NotificationRepo = NotificationRepoImpl()
) : ViewModel() {

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _orders = MutableStateFlow<List<OrderModel>>(emptyList())
    val orders: StateFlow<List<OrderModel>> = _orders.asStateFlow()

    fun clearMessage() {
        _message.value = null
    }

    fun loadAllOrders() {
        _loading.value = true
        repo.getAllOrders { success, msg, list ->
            _loading.value = false
            if (success) _orders.value = list else _message.value = msg
        }
    }

    fun loadUserOrders(userId: String) {
        _loading.value = true
        repo.getUserOrders(userId) { success, msg, list ->
            _loading.value = false
            if (success) _orders.value = list else _message.value = msg
        }
    }

    fun placeOrder(model: OrderModel, onSuccess: (OrderModel) -> Unit) {
        _loading.value = true
        repo.addOrder(model) { success, msg, saved ->
            _loading.value = false
            _message.value = msg
            if (success) {
                notificationRepo.addNotification(
                    com.example.pharmax.model.NotificationModel(
                        recipientId = ADMIN_NOTIFICATION_BUCKET,
                        title = "New order received",
                        message = "${saved.userName.ifBlank { "A customer" }} bought ${saved.quantity}x ${saved.medicineName} for NPR ${saved.totalAmount.toInt()}",
                        type = "order",
                        referenceId = saved.orderId
                    )
                ) { _, _ -> }
                notificationRepo.addNotification(
                    com.example.pharmax.model.NotificationModel(
                        recipientId = saved.userId,
                        title = "Order placed",
                        message = "Your order for ${saved.quantity}x ${saved.medicineName} was placed successfully.",
                        type = "order",
                        referenceId = saved.orderId
                    )
                ) { _, _ -> }
                onSuccess(saved)
            }
        }
    }

    fun updateOrderStatus(orderId: String, status: String, userId: String, medicineName: String) {
        repo.updateOrderStatus(orderId, status) { success, msg ->
            _message.value = msg
            if (success) {
                if (userId.isNotBlank()) {
                    notificationRepo.addNotification(
                        com.example.pharmax.model.NotificationModel(
                            recipientId = userId,
                            title = "Order $status",
                            message = "Your order${if (medicineName.isNotBlank()) " for $medicineName" else ""} is now $status.",
                            type = "order",
                            referenceId = orderId
                        )
                    ) { _, _ -> }
                }
                loadAllOrders()
            }
        }
    }
}
