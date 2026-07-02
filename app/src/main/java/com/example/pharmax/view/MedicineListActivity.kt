package com.example.pharmax.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pharmax.model.MedicineModel
import com.example.pharmax.ui.theme.PharmaXTheme
import com.example.pharmax.viewmodel.MedicineViewModel

class MedicineListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val categoryId = intent.getStringExtra("categoryId") ?: ""
        val categoryName = intent.getStringExtra("categoryName") ?: "Medicines"
        val categoryIcon = intent.getStringExtra("categoryIcon") ?: "💊"
        setContent {
            PharmaXTheme {
                MedicineListBody(
                    categoryId = categoryId,
                    categoryName = categoryName,
                    categoryIcon = categoryIcon,
                    onMedicineClick = { medicine ->
                        val i = Intent(this, UserMedicineDetailActivity::class.java)
                        i.putExtra("medicineId", medicine.medicineId)
                        i.putExtra("name", medicine.name)
                        i.putExtra("brand", medicine.brand)
                        i.putExtra("category", medicine.category)
                        i.putExtra("description", medicine.description)
                        i.putExtra("price", medicine.price)
                        i.putExtra("stock", medicine.stock)
                        i.putExtra("dosage", medicine.dosage)
                        i.putExtra("requiresPrescription", medicine.requiresPrescription)
                        i.putExtra("type", medicine.type)
                        i.putExtra("howToUse", medicine.howToUse)
                        startActivity(i)
                    },
                    onBack = { finish() }
                )
            }
        }
    }
}

@Composable
fun MedicineListBody(
    categoryId: String = "",
    categoryName: String = "Medicines",
    categoryIcon: String = "💊",
    onMedicineClick: (MedicineModel) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val vm: MedicineViewModel = viewModel()
    val medicines by vm.medicines.collectAsState()
    val isLoading by vm.loading.collectAsState()

    LaunchedEffect(Unit) { vm.loadMedicines() }

    MedicineListScreen(
        categoryName = categoryName,
        categoryIcon = categoryIcon,
        medicines = medicines.filter { it.category == categoryName },
        isLoading = isLoading,
        onMedicineClick = onMedicineClick,
        onBack = onBack
    )
}

@Composable
fun MedicineListScreen(
    categoryName: String = "Medicines",
    categoryIcon: String = "💊",
    medicines: List<MedicineModel> = emptyList(),
    isLoading: Boolean = false,
    onMedicineClick: (MedicineModel) -> Unit = {},
    onBack: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    val filtered = medicines.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.brand.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        bottomBar = { DashboardBottomNav(activeTab = "Medicines") }
    ) { innerPadding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
    ) {

        // ── Top bar ───────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF006B2C),
                modifier = Modifier.size(24.dp).clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = categoryIcon, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = categoryName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF006B2C),
                modifier = Modifier.weight(1f)
            )
            Box {
                Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(24.dp))
                Box(modifier = Modifier.size(8.dp).background(Color.Red, CircleShape).align(Alignment.TopEnd))
            }
        }

        // ── Search bar ────────────────────────────────────────────────────
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            placeholder = { Text("Search medicines, brands...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            singleLine = true,
            shape = RoundedCornerShape(50.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedBorderColor = Color(0xFF006B2C),
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        // ── Count ─────────────────────────────────────────────────────────
        Text(
            text = "${filtered.size} ${if (filtered.size == 1) "medicine" else "medicines"} found",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Content ───────────────────────────────────────────────────────
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF006B2C))
                }
            }
            filtered.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "💊", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isBlank()) "No medicines in this category yet" else "No results for \"$searchQuery\"",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filtered) { medicine ->
                        MedicineCard(medicine = medicine, onClick = { onMedicineClick(medicine) })
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
    } // end Scaffold
}

@Composable
private fun MedicineCard(medicine: MedicineModel, onClick: () -> Unit) {
    val isOutOfStock = medicine.stock == 0
    val isLowStock = medicine.stock in 1..10
    val stockColor = if (isOutOfStock || isLowStock) MaterialTheme.colorScheme.error else Color(0xFF006B2C)
    val stockLabel = when {
        isOutOfStock -> "Out of Stock"
        isLowStock   -> "Low Stock"
        else         -> "In Stock"
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "💊", fontSize = 26.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = medicine.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .background(
                                if (medicine.type == "Rx" || medicine.requiresPrescription) Color(0xFFFFEDED) else Color(0xFFE8F5E9),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (medicine.requiresPrescription) "Rx" else "OTC",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (medicine.requiresPrescription) MaterialTheme.colorScheme.error else Color(0xFF006B2C)
                        )
                    }
                }

                Text(text = medicine.brand, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(6.dp))

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NPR ${medicine.price.toInt()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0051D5)
                    )
                    Text(
                        text = stockLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = stockColor
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MedicineListPreview() {
    MedicineListScreen(
        categoryName = "Pain Relief",
        categoryIcon = "💊",
        medicines = listOf(
            MedicineModel("1", "Paracetamol 500mg", "Panadol", "Pain Relief", price = 120.0, stock = 45, requiresPrescription = false, type = "OTC"),
            MedicineModel("2", "Ibuprofen 400mg", "Brufen", "Pain Relief", price = 85.0, stock = 5, requiresPrescription = false, type = "OTC"),
            MedicineModel("3", "Tramadol 50mg", "Tramal", "Pain Relief", price = 250.0, stock = 0, requiresPrescription = true, type = "Rx"),
        )
    )
}
