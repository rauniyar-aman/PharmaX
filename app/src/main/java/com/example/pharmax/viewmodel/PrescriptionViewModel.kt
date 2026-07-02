package com.example.pharmax.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pharmax.model.PrescriptionModel
import com.example.pharmax.repo.PrescriptionRepo
import com.example.pharmax.repo.PrescriptionRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PrescriptionViewModel(private val repo: PrescriptionRepo = PrescriptionRepoImpl()) : ViewModel() {

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

    fun addPrescription(model: PrescriptionModel, onSuccess: () -> Unit) {
        if (model.imageUrl.isBlank()) {
            _message.value = "Please upload a prescription image"
            return
        }
        _loading.value = true
        repo.addPrescription(model) { success, msg ->
            _loading.value = false
            _message.value = msg
            if (success) onSuccess()
        }
    }

    fun updateStatus(prescriptionId: String, status: String, comment: String, onSuccess: () -> Unit) {
        _loading.value = true
        repo.updatePrescriptionStatus(prescriptionId, status, comment) { success, msg ->
            _loading.value = false
            _message.value = msg
            if (success) onSuccess()
        }
    }
}
