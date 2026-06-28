package com.example.pharmax.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pharmax.model.MedicineModel
import com.example.pharmax.repo.MedicineRepo
import com.example.pharmax.repo.MedicineRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MedicineViewModel(private val repo: MedicineRepo = MedicineRepoImpl()) : ViewModel() {

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _medicines = MutableStateFlow<List<MedicineModel>>(emptyList())
    val medicines: StateFlow<List<MedicineModel>> = _medicines.asStateFlow()

    fun clearMessage() {
        _message.value = null
    }

    fun loadMedicines() {
        _loading.value = true
        repo.getAllMedicines { success, msg, list ->
            _loading.value = false
            if (success) {
                _medicines.value = list
            } else {
                _message.value = msg
            }
        }
    }

    fun addMedicine(model: MedicineModel, onSuccess: () -> Unit) {
        if (model.name.isBlank()) {
            _message.value = "Medicine name is required"
            return
        }
        if (model.brand.isBlank()) {
            _message.value = "Brand name is required"
            return
        }
        if (model.price <= 0) {
            _message.value = "Price must be greater than 0"
            return
        }
        _loading.value = true
        repo.addMedicine(model) { success, msg ->
            _loading.value = false
            _message.value = msg
            if (success) onSuccess()
        }
    }

    fun updateMedicine(model: MedicineModel, onSuccess: () -> Unit) {
        if (model.name.isBlank()) {
            _message.value = "Medicine name is required"
            return
        }
        _loading.value = true
        repo.updateMedicine(model) { success, msg ->
            _loading.value = false
            _message.value = msg
            if (success) onSuccess()
        }
    }

    fun deleteMedicine(medicineId: String, onSuccess: () -> Unit) {
        _loading.value = true
        repo.deleteMedicine(medicineId) { success, msg ->
            _loading.value = false
            _message.value = msg
            if (success) onSuccess()
        }
    }
}
