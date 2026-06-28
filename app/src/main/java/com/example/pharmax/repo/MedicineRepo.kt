package com.example.pharmax.repo

import com.example.pharmax.model.MedicineModel

interface MedicineRepo {
    fun addMedicine(model: MedicineModel, callback: (Boolean, String) -> Unit)
    fun getAllMedicines(callback: (Boolean, String, List<MedicineModel>) -> Unit)
    fun updateMedicine(model: MedicineModel, callback: (Boolean, String) -> Unit)
    fun deleteMedicine(medicineId: String, callback: (Boolean, String) -> Unit)
}
