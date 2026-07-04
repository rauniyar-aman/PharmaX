package com.example.pharmax.repo

import com.example.pharmax.model.PrescriptionModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PrescriptionRepoImpl : PrescriptionRepo {

    private val ref = FirebaseDatabase.getInstance().getReference("prescriptions")

    override fun addPrescription(model: PrescriptionModel, callback: (Boolean, String, PrescriptionModel) -> Unit) {
        val id = ref.push().key ?: ""
        val newModel = model.copy(prescriptionId = id)
        ref.child(id).setValue(newModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Prescription submitted successfully", newModel)
                } else {
                    callback(false, task.exception?.message ?: "Failed to submit prescription", newModel)
                }
            }
    }

    override fun getAllPrescriptions(callback: (Boolean, String, List<PrescriptionModel>) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(PrescriptionModel::class.java) }
                    .sortedByDescending { it.uploadedAt }
                callback(true, "Success", list)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }

    override fun getUserPrescriptions(userId: String, callback: (Boolean, String, List<PrescriptionModel>) -> Unit) {
        ref.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull { it.getValue(PrescriptionModel::class.java) }
                        .sortedByDescending { it.uploadedAt }
                    callback(true, "Success", list)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
    }

    override fun updatePrescriptionStatus(prescriptionId: String, status: String, comment: String, callback: (Boolean, String) -> Unit) {
        val updates = mapOf("status" to status, "adminComment" to comment)
        ref.child(prescriptionId).updateChildren(updates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Prescription $status")
                } else {
                    callback(false, task.exception?.message ?: "Failed to update prescription")
                }
            }
    }

    override fun updatePrescription(prescriptionId: String, name: String, imageUrl: String, notes: String, callback: (Boolean, String) -> Unit) {
        val updates = mapOf("name" to name, "imageUrl" to imageUrl, "notes" to notes)
        ref.child(prescriptionId).updateChildren(updates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Prescription updated successfully")
                } else {
                    callback(false, task.exception?.message ?: "Failed to update prescription")
                }
            }
    }
}
