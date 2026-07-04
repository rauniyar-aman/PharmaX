package com.example.pharmax.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pharmax.model.NotificationModel
import com.example.pharmax.model.PrescriptionModel
import com.example.pharmax.repo.NotificationRepo
import com.example.pharmax.repo.NotificationRepoImpl
import com.example.pharmax.repo.PrescriptionRepo
import com.example.pharmax.repo.PrescriptionRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PrescriptionViewModel(
    private val repo: PrescriptionRepo = PrescriptionRepoImpl(),
    private val notificationRepo: NotificationRepo = NotificationRepoImpl()
) : ViewModel() {

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _prescriptions = MutableStateFlow<List<PrescriptionModel>>(emptyList())
    val prescriptions: StateFlow<List<PrescriptionModel>> = _prescriptions.asStateFlow()

    fun clearMessage() {
        _message.value = null
    }

    fun loadAllPrescriptions() {
        _loading.value = true
        repo.getAllPrescriptions { success, msg, list ->
            _loading.value = false
            if (success) _prescriptions.value = list else _message.value = msg
        }
    }

    fun loadUserPrescriptions(userId: String) {
        _loading.value = true
        repo.getUserPrescriptions(userId) { success, msg, list ->
            _loading.value = false
            if (success) _prescriptions.value = list else _message.value = msg
        }
    }

    fun addPrescription(model: PrescriptionModel, onSuccess: (PrescriptionModel) -> Unit) {
        if (model.name.isBlank()) {
            _message.value = "Please give this prescription a name"
            return
        }
        if (model.imageUrl.isBlank()) {
            _message.value = "Please upload a prescription image"
            return
        }
        _loading.value = true
        repo.addPrescription(model) { success, msg, saved ->
            _loading.value = false
            _message.value = msg
            if (success) {
                notificationRepo.addNotification(
                    NotificationModel(
                        recipientId = ADMIN_NOTIFICATION_BUCKET,
                        title = "New prescription submitted",
                        message = "${saved.userName.ifBlank { "A customer" }} submitted a prescription${if (saved.medicineName.isNotBlank()) " for ${saved.medicineName}" else ""}.",
                        type = "prescription",
                        referenceId = saved.prescriptionId
                    )
                ) { _, _ -> }
                onSuccess(saved)
            }
        }
    }

    fun updateStatus(prescriptionId: String, status: String, comment: String, userId: String, medicineName: String, onSuccess: () -> Unit) {
        _loading.value = true
        repo.updatePrescriptionStatus(prescriptionId, status, comment) { success, msg ->
            _loading.value = false
            _message.value = msg
            if (success) {
                if (userId.isNotBlank()) {
                    notificationRepo.addNotification(
                        NotificationModel(
                            recipientId = userId,
                            title = "Prescription $status",
                            message = "Your prescription${if (medicineName.isNotBlank()) " for $medicineName" else ""} has been $status.${if (comment.isNotBlank()) " Note: $comment" else ""}",
                            type = "prescription",
                            referenceId = prescriptionId
                        )
                    ) { _, _ -> }
                }
                onSuccess()
            }
        }
    }

    fun updatePrescription(prescriptionId: String, name: String, imageUrl: String, notes: String, onSuccess: () -> Unit) {
        if (name.isBlank()) {
            _message.value = "Please give this prescription a name"
            return
        }
        if (imageUrl.isBlank()) {
            _message.value = "Please upload a prescription image"
            return
        }
        _loading.value = true
        repo.updatePrescription(prescriptionId, name, imageUrl, notes) { success, msg ->
            _loading.value = false
            _message.value = msg
            if (success) onSuccess()
        }
    }
}
