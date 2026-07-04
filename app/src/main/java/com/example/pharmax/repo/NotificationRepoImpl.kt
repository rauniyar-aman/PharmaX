package com.example.pharmax.repo

import com.example.pharmax.model.NotificationModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NotificationRepoImpl : NotificationRepo {

    private val ref = FirebaseDatabase.getInstance().getReference("notifications")

    override fun addNotification(model: NotificationModel, callback: (Boolean, String) -> Unit) {
        val id = ref.push().key ?: ""
        val newModel = model.copy(notificationId = id)
        ref.child(id).setValue(newModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Notification sent")
                } else {
                    callback(false, task.exception?.message ?: "Failed to send notification")
                }
            }
    }

    override fun getNotifications(recipientId: String, callback: (Boolean, String, List<NotificationModel>) -> Unit) {
        ref.orderByChild("recipientId").equalTo(recipientId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull { it.getValue(NotificationModel::class.java) }
                        .sortedByDescending { it.createdAt }
                    callback(true, "Success", list)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
    }

    override fun markAsRead(notificationId: String, callback: (Boolean, String) -> Unit) {
        ref.child(notificationId).updateChildren(mapOf("isRead" to true))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Marked as read")
                } else {
                    callback(false, task.exception?.message ?: "Failed to update notification")
                }
            }
    }

    override fun markAllAsRead(recipientId: String, callback: (Boolean, String) -> Unit) {
        ref.orderByChild("recipientId").equalTo(recipientId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val updates = snapshot.children.associate { "${it.key}/isRead" to true }
                    if (updates.isEmpty()) {
                        callback(true, "Nothing to update")
                        return
                    }
                    ref.updateChildren(updates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                callback(true, "All marked as read")
                            } else {
                                callback(false, task.exception?.message ?: "Failed to update notifications")
                            }
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message)
                }
            })
    }
}
