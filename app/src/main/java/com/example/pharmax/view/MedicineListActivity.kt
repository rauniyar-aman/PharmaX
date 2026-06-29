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

class MedicineListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val categoryId = intent.getStringExtra("categoryId") ?: ""
        val categoryName = intent.getStringExtra("categoryName") ?: "Medicines"
        val categoryIcon = intent.getStringExtra("categoryIcon") ?: "💊"
        setContent {
            MedicineListBody(
                categoryId = categoryId,
                categoryName = categoryName,
                categoryIcon = categoryIcon,
                onBack = { finish() }
            )
        }
    }
}

@Composable
fun MedicineListBody(
    categoryId: String = "",
    categoryName: String = "Medicines",
    categoryIcon: String = "💊",
    onBack: () -> Unit = {}
) {
    MedicineListScreen(categoryId = categoryId, categoryName = categoryName, categoryIcon = categoryIcon, onBack = onBack)
}

@Composable
fun MedicineListScreen(
    categoryId: String = "",
    categoryName: String = "Medicines",
    categoryIcon: String = "💊",
    onBack: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Coming soon: $categoryIcon $categoryName")
    }
}
