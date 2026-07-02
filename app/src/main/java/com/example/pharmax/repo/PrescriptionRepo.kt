package com.example.pharmax.repo

import com.example.pharmax.model.PrescriptionModel

interface PrescriptionRepo {
    fun addPrescription(model: PrescriptionModel, callback: (Boolean, String) -> Unit)
    fun getAllPrescriptions(callback: (Boolean, String, List<PrescriptionModel>) -> Unit)
    fun getUserPrescriptions(userId: String, callback: (Boolean, String, List<PrescriptionModel>) -> Unit)
    fun updatePrescriptionStatus(prescriptionId: String, status: String, comment: String, callback: (Boolean, String) -> Unit)
}
