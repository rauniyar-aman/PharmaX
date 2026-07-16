package com.example.pharmax.repo

import com.example.pharmax.model.OrderModel

interface OrderRepo {
    fun addOrder(model: OrderModel, callback: (Boolean, String, OrderModel) -> Unit)
    fun getAllOrders(callback: (Boolean, String, List<OrderModel>) -> Unit)
    fun getUserOrders(userId: String, callback: (Boolean, String, List<OrderModel>) -> Unit)
    fun updateOrderStatus(orderId: String, status: String, callback: (Boolean, String) -> Unit)
}
