package com.example.pharmax.repo

import com.example.pharmax.model.OrderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderRepoImpl : OrderRepo {

    private val ref = FirebaseDatabase.getInstance().getReference("orders")

    override fun addOrder(model: OrderModel, callback: (Boolean, String, OrderModel) -> Unit) {
        val id = ref.push().key ?: ""
        val newModel = model.copy(orderId = id)
        ref.child(id).setValue(newModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Order placed successfully", newModel)
                } else {
                    callback(false, task.exception?.message ?: "Failed to place order", newModel)
                }
            }
    }

    override fun getAllOrders(callback: (Boolean, String, List<OrderModel>) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(OrderModel::class.java) }
                    .sortedByDescending { it.orderedAt }
                callback(true, "Success", list)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }

    override fun getUserOrders(userId: String, callback: (Boolean, String, List<OrderModel>) -> Unit) {
        ref.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull { it.getValue(OrderModel::class.java) }
                        .sortedByDescending { it.orderedAt }
                    callback(true, "Success", list)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
    }

    override fun updateOrderStatus(orderId: String, status: String, callback: (Boolean, String) -> Unit) {
        ref.child(orderId).child("orderStatus").setValue(status)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Order marked as $status")
                } else {
                    callback(false, task.exception?.message ?: "Failed to update order status")
                }
            }
    }
}
