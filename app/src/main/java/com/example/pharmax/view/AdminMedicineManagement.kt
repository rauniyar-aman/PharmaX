package com.example.pharmax.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pharmax.model.MedicineModel
import com.example.pharmax.viewmodel.MedicineViewModel

class AdminMedicineManagement : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdminMedicineManagementBody()
        }
    }
}

@Composable
fun AdminMedicineManagementBody() {
    val context = LocalContext.current
    val vm: MedicineViewModel = viewModel()

    val message by vm.message.collectAsState()
    val medicines by vm.medicines.collectAsState()
    val isLoading by vm.loading.collectAsState()

    LaunchedEffect(Unit) { vm.loadMedicines() }

    LaunchedEffect(message) {
        if (!message.isNullOrBlank()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            vm.clearMessage()
        }
    }

    AdminMedicineManagementScreen(
        medicines = medicines,
        isLoading = isLoading,
        onDelete = { id -> vm.deleteMedicine(id) {} },
        onView = { medicine ->
            val intent = Intent(context, AdminMedicineDetailActivity::class.java).apply {
                putExtra("medicineId", medicine.medicineId)
                putExtra("name", medicine.name)
                putExtra("brand", medicine.brand)
                putExtra("category", medicine.category)
                putExtra("description", medicine.description)
                putExtra("price", medicine.price)
                putExtra("stock", medicine.stock)
                putExtra("dosage", medicine.dosage)
                putExtra("requiresPrescription", medicine.requiresPrescription)
                putExtra("type", medicine.type)
                putExtra("howToUse", medicine.howToUse)
                putExtra("imageUrl", medicine.imageUrl)
            }
            context.startActivity(intent)
        },
        onEdit = { medicine ->
            val intent = Intent(context, AddMedicineActivity::class.java).apply {
                putExtra("medicineId", medicine.medicineId)
                putExtra("name", medicine.name)
                putExtra("brand", medicine.brand)
                putExtra("category", medicine.category)
                putExtra("description", medicine.description)
                putExtra("price", medicine.price)
                putExtra("stock", medicine.stock)
                putExtra("dosage", medicine.dosage)
                putExtra("requiresPrescription", medicine.requiresPrescription)
                putExtra("type", medicine.type)
                putExtra("howToUse", medicine.howToUse)
                putExtra("imageUrl", medicine.imageUrl)
            }
            context.startActivity(intent)
        },
        onAddClick = { context.startActivity(Intent(context, AddMedicineActivity::class.java)) }
    )
}

@Composable
fun AdminMedicineManagementScreen(
    medicines: List<MedicineModel> = emptyList(),
    isLoading: Boolean = false,
    onDelete: (String) -> Unit = {},
    onView: (MedicineModel) -> Unit = {},
    onEdit: (MedicineModel) -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filters = listOf("All", "In Stock", "Low Stock", "Out of Stock")

    val filtered = medicines.filter { medicine ->
        val matchesSearch = medicine.name.contains(searchQuery, ignoreCase = true) ||
                medicine.brand.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "In Stock" -> medicine.stock > 10
            "Low Stock" -> medicine.stock in 1..10
            "Out of Stock" -> medicine.stock == 0
            else -> true
        }
        matchesSearch && matchesFilter
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = Color(0xFF00501F),
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Medicine")
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F9FF))
                .padding(paddingValues)
        ) {

            // ── Top bar ───────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = null, tint = Color(0xFF0E1D2A), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Medicine Management", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0E1D2A), modifier = Modifier.weight(1f))
                Box {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF0E1D2A), modifier = Modifier.size(24.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color.Red, CircleShape)
                            .align(Alignment.TopEnd)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier.size(36.dp).background(Color(0xFF006B2C), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "A", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {

                // ── Search bar ────────────────────────────────────────────
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        placeholder = { Text("Search medicines...") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF6F7A6E)) },
                        singleLine = true,
                        shape = RoundedCornerShape(50.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFE3EFFF),
                            unfocusedContainerColor = Color(0xFFE3EFFF),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }

                // ── Filter row ────────────────────────────────────────────
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Filter button
                        Box(
                            modifier = Modifier
                                .border(1.dp, Color(0xFFBFCABB), RoundedCornerShape(50.dp))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Menu, contentDescription = null, tint = Color(0xFF0E1D2A), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Filter", fontSize = 13.sp, color = Color(0xFF0E1D2A), fontWeight = FontWeight.Medium)
                            }
                        }
                        filters.forEach { filter ->
                            MedicineFilterChip(
                                label = filter,
                                isSelected = selectedFilter == filter,
                                onClick = { selectedFilter = filter }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // ── Loading ───────────────────────────────────────────────
                if (isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF006B2C))
                        }
                    }
                }

                // ── Empty state ───────────────────────────────────────────
                if (!isLoading && filtered.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "💊", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "No medicines found", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0E1D2A))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Tap + to add your first medicine", fontSize = 13.sp, color = Color(0xFF6F7A6E))
                        }
                    }
                }

                // ── Medicine list ─────────────────────────────────────────
                items(filtered) { medicine ->
                    MedicineListItem(
                        medicine = medicine,
                        onView = { onView(medicine) },
                        onEdit = { onEdit(medicine) },
                        onDelete = { onDelete(medicine.medicineId) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun MedicineFilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val isLow = label == "Low Stock"
    Box(
        modifier = Modifier
            .background(
                color = when {
                    isSelected && isLow -> Color(0xFFFFF3E0)
                    isSelected -> Color(0xFF00501F)
                    else -> Color.White
                },
                shape = RoundedCornerShape(50.dp)
            )
            .border(
                width = 1.dp,
                color = when {
                    isSelected && isLow -> Color(0xFFFF6D00)
                    isSelected -> Color.Transparent
                    else -> Color(0xFFBFCABB)
                },
                shape = RoundedCornerShape(50.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = when {
                isSelected && isLow -> Color(0xFFFF6D00)
                isSelected -> Color.White
                else -> Color(0xFF0E1D2A)
            }
        )
    }
}

@Composable
fun MedicineListItem(
    medicine: MedicineModel,
    onView: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val isLowStock = medicine.stock in 1..10
    val isOutOfStock = medicine.stock == 0
    val stockColor = if (isLowStock || isOutOfStock) Color(0xFFBA1A1A) else Color(0xFF0E1D2A)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {

            // Image thumbnail placeholder
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(Color(0xFFE3EFFF), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "💊", fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = medicine.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0E1D2A))
                Text(text = medicine.brand, fontSize = 13.sp, color = Color(0xFF6F7A6E))

                Spacer(modifier = Modifier.height(4.dp))

                if (medicine.category.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE3EFFF), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = medicine.category.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0E1D2A)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "NPR ${medicine.price.toInt()}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0051D5)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Stock: ${medicine.stock}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = stockColor
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "View",
                            tint = Color(0xFF6F7A6E),
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { onView() }
                        )
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color(0xFF6F7A6E),
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { onEdit() }
                        )
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFBA1A1A),
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { onDelete() }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminMedicineManagementPreview() {
    val sampleMedicines = listOf(
        MedicineModel("1", "Paracetamol 500mg", "Panadol Professional", "Pain Relief", price = 120.0, stock = 42),
        MedicineModel("2", "Amoxicillin Syrup", "GSK Pharma", "Antibiotics", price = 450.0, stock = 8),
        MedicineModel("3", "Vitamin C 1000mg", "Nature's Health", "Supplements", price = 890.0, stock = 124),
        MedicineModel("4", "Digital Thermometer", "Omron Medical", "Devices", price = 1200.0, stock = 0)
    )
    AdminMedicineManagementScreen(medicines = sampleMedicines)
}
