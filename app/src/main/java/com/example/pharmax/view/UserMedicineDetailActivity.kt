package com.example.pharmax.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.pharmax.model.MedicineModel

class UserMedicineDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val medicine = MedicineModel(
            medicineId = intent.getStringExtra("medicineId") ?: "",
            name = intent.getStringExtra("name") ?: "",
            brand = intent.getStringExtra("brand") ?: "",
            category = intent.getStringExtra("category") ?: "",
            description = intent.getStringExtra("description") ?: "",
            price = intent.getDoubleExtra("price", 0.0),
            stock = intent.getIntExtra("stock", 0),
            dosage = intent.getStringExtra("dosage") ?: "",
            requiresPrescription = intent.getBooleanExtra("requiresPrescription", false),
            type = intent.getStringExtra("type") ?: "OTC",
            howToUse = intent.getStringExtra("howToUse") ?: ""
        )
        setContent {
            UserMedicineDetailBody(medicine = medicine, onBack = { finish() })
        }
    }
}

@Composable
fun UserMedicineDetailBody(medicine: MedicineModel = MedicineModel(), onBack: () -> Unit = {}) {
    UserMedicineDetailScreen(medicine = medicine, onBack = onBack)
}

@Composable
fun UserMedicineDetailScreen(medicine: MedicineModel = MedicineModel(), onBack: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Medicine Detail: ${medicine.name}")
    }
}
