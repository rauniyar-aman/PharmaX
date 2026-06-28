package com.example.pharmax.repo

import com.example.pharmax.model.MedicineModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MedicineRepoImpl : MedicineRepo {

    private val ref = FirebaseDatabase.getInstance().getReference("medicines")

    override fun addMedicine(model: MedicineModel, callback: (Boolean, String) -> Unit) {
        val id = ref.push().key ?: ""
        val newModel = model.copy(medicineId = id)
        ref.child(id).setValue(newModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Medicine added successfully")
                } else {
                    callback(false, task.exception?.message ?: "Failed to add medicine")
                }
            }
    }

    override fun getAllMedicines(callback: (Boolean, String, List<MedicineModel>) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(MedicineModel::class.java) }
                callback(true, "Success", list)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }

    override fun updateMedicine(model: MedicineModel, callback: (Boolean, String) -> Unit) {
        ref.child(model.medicineId).setValue(model)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Medicine updated successfully")
                } else {
                    callback(false, task.exception?.message ?: "Failed to update medicine")
                }
            }
    }

    override fun deleteMedicine(medicineId: String, callback: (Boolean, String) -> Unit) {
        ref.child(medicineId).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Medicine deleted successfully")
                } else {
                    callback(false, task.exception?.message ?: "Failed to delete medicine")
                }
            }
    }
}
