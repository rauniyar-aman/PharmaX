package com.example.pharmax.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.pharmax.model.CategoryModel
import com.example.pharmax.viewmodel.CategoryViewModel

class AdminCategoryManagement : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdminCategoryManagementBody()
        }
    }
}

@Composable
fun AdminCategoryManagementBody() {
    val context = LocalContext.current
    val vm: CategoryViewModel = viewModel()

    val message by vm.message.collectAsState()
    val categories by vm.categories.collectAsState()
    val isLoading by vm.loading.collectAsState()

    LaunchedEffect(Unit) { vm.loadCategories() }

    LaunchedEffect(message) {
        if (!message.isNullOrBlank()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            vm.clearMessage()
        }
    }

    AdminCategoryManagementScreen(
        categories = categories,
        isLoading = isLoading,
        onToggleStatus = { id, status -> vm.toggleStatus(id, status) },
        onDelete = { id -> vm.deleteCategory(id) {} },
        onEdit = { category ->
            val intent = Intent(context, AddCategoryActivity::class.java).apply {
                putExtra("categoryId", category.categoryId)
                putExtra("name", category.name)
                putExtra("description", category.description)
                putExtra("icon", category.icon)
                putExtra("type", category.type)
                putExtra("isActive", category.isActive)
                putExtra("slug", category.slug)
                putExtra("medicineCount", category.medicineCount)
            }
            context.startActivity(intent)
        },
        onAddClick = { context.startActivity(Intent(context, AddCategoryActivity::class.java)) }
    )
}

@Composable
fun AdminCategoryManagementScreen(
    categories: List<CategoryModel> = emptyList(),
    isLoading: Boolean = false,
    onToggleStatus: (String, Boolean) -> Unit = { _, _ -> },
    onDelete: (String) -> Unit = {},
    onEdit: (CategoryModel) -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All Categories") }

    val filters = listOf("All Categories", "Prescription", "OTC", "Supplement")

    val filtered = categories.filter { category ->
        val matchesSearch = category.name.contains(searchQuery, ignoreCase = true)
        val matchesFilter = selectedFilter == "All Categories" || category.type == selectedFilter
        matchesSearch && matchesFilter
    }

    val totalActive = categories.count { it.isActive }
    val totalMedicines = categories.sumOf { it.medicineCount }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = Color(0xFF00501F),
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Category")
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F9FF))
                .padding(paddingValues)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = null, tint = Color(0xFF0E1D2A), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Medicine Categories", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006B2C), modifier = Modifier.weight(1f))
                Box {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF0E1D2A), modifier = Modifier.size(24.dp))
                    Box(modifier = Modifier.size(8.dp).background(Color.Red, CircleShape).align(Alignment.TopEnd))
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

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AdminStatCard(label = "Total Categories", value = "${categories.size}", valueColor = Color(0xFF006B2C))
                        AdminStatCard(label = "Active Types", value = "$totalActive", valueColor = Color(0xFF0051D5))
                        AdminStatCard(label = "Total SKUs", value = "$totalMedicines", valueColor = Color(0xFF0E1D2A))
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0051D5))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Sync Status", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Cloud Ready", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Text(text = "Last update: 2 mins ago", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        placeholder = { Text("Search categories...") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF6F7A6E)) },
                        singleLine = true,
                        shape = RoundedCornerShape(50.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color(0xFF006B2C),
                            unfocusedIndicatorColor = Color(0xFF6F7A6E)
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    Text(
                        text = "Management List",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF006B2C),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filters.forEach { filter ->
                            FilterChip(
                                label = filter,
                                isSelected = selectedFilter == filter,
                                onClick = { selectedFilter = filter }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF006B2C))
                        }
                    }
                }

                if (!isLoading && filtered.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "📂", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "No categories yet", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0E1D2A))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Tap + to add your first category", fontSize = 13.sp, color = Color(0xFF6F7A6E))
                        }
                    }
                }

                items(filtered) { category ->
                    CategoryListItem(
                        category = category,
                        onToggle = { onToggleStatus(category.categoryId, it) },
                        onEdit = { onEdit(category) },
                        onDelete = { onDelete(category.categoryId) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun AdminStatCard(label: String, value: String, valueColor: Color) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
            Text(text = label, fontSize = 12.sp, color = Color(0xFF6F7A6E))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}

@Composable
fun FilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                color = if (isSelected) Color(0xFF00501F) else Color.White,
                shape = RoundedCornerShape(50.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.White else Color(0xFF0E1D2A)
        )
    }
}

@Composable
fun CategoryListItem(
    category: CategoryModel,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFE3F2FD), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = category.icon, fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = category.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0E1D2A))
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE3EEFF), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = category.type, fontSize = 10.sp, color = Color(0xFF0051D5), fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = category.slug, fontSize = 11.sp, color = Color(0xFF6F7A6E))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "•", fontSize = 11.sp, color = Color(0xFF6F7A6E))
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE8F5E9), RoundedCornerShape(50.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(text = "${category.medicineCount} medicines", fontSize = 11.sp, color = Color(0xFF006B2C), fontWeight = FontWeight.Medium)
                        }
                    }
                }
                Switch(
                    checked = category.isActive,
                    onCheckedChange = { onToggle(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF006B2C))
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onEdit() }
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF0051D5), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Edit", fontSize = 13.sp, color = Color(0xFF0051D5), fontWeight = FontWeight.Medium)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onDelete() }
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFBA1A1A), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Delete", fontSize = 13.sp, color = Color(0xFFBA1A1A), fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminCategoryManagementPreview() {
    val sampleCategories = listOf(
        CategoryModel("1", "Antibiotics", "/slug/antibiotics", "For bacterial infections", "💊", "OTC", true, 142),
        CategoryModel("2", "Vitamins", "/slug/vitamins", "Dietary supplements", "🌿", "OTC", true, 58),
        CategoryModel("3", "Pain Relief", "/slug/pain-relief", "Analgesics", "🩹", "Prescription", false, 34)
    )
    AdminCategoryManagementScreen(categories = sampleCategories)
}
