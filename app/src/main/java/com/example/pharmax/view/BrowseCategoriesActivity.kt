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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.pharmax.model.CategoryModel
import com.example.pharmax.viewmodel.CategoryViewModel

class BrowseCategoriesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BrowseCategoriesBody(
                onCategoryClick = { category ->
                    val intent = Intent(this, MedicineListActivity::class.java)
                    intent.putExtra("categoryId", category.categoryId)
                    intent.putExtra("categoryName", category.name)
                    intent.putExtra("categoryIcon", category.icon)
                    startActivity(intent)
                },
                onBack = { finish() }
            )
        }
    }
}

@Composable
fun BrowseCategoriesBody(
    onCategoryClick: (CategoryModel) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val vm: CategoryViewModel = viewModel()
    val categories by vm.categories.collectAsState()
    val isLoading by vm.loading.collectAsState()

    LaunchedEffect(Unit) { vm.loadCategories() }

    BrowseCategoriesScreen(
        categories = categories.filter { it.isActive },
        isLoading = isLoading,
        onCategoryClick = onCategoryClick,
        onBack = onBack
    )
}

@Composable
fun BrowseCategoriesScreen(
    categories: List<CategoryModel> = emptyList(),
    isLoading: Boolean = false,
    onCategoryClick: (CategoryModel) -> Unit = {},
    onBack: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    val filtered = categories.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.type.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FF))
    ) {

        // ── Top bar ───────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
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
            Text(
                text = "Browse Categories",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF006B2C),
                modifier = Modifier.weight(1f)
            )
            Box {
                Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF0E1D2A), modifier = Modifier.size(24.dp))
                Box(modifier = Modifier.size(8.dp).background(Color.Red, CircleShape).align(Alignment.TopEnd))
            }
        }

        // ── Search bar ────────────────────────────────────────────────────
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            placeholder = { Text("Search categories...", color = Color(0xFF6F7A6E), fontSize = 14.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF6F7A6E)) },
            singleLine = true,
            shape = RoundedCornerShape(50.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFBFCABB),
                focusedBorderColor = Color(0xFF006B2C),
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        // ── Count row ─────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filtered.size} ${if (filtered.size == 1) "category" else "categories"} found",
                fontSize = 13.sp,
                color = Color(0xFF6F7A6E)
            )
        }

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
                            text = if (searchQuery.isBlank()) "No categories available" else "No results for \"$searchQuery\"",
                            fontSize = 15.sp,
                            color = Color(0xFF6F7A6E)
                        )
                    }
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filtered) { category ->
                        CategoryCard(category = category, onClick = { onCategoryClick(category) })
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(category: CategoryModel, onClick: () -> Unit) {
    val typeColor = when (category.type) {
        "Prescription" -> Color(0xFFFFEDED)
        "Supplement"   -> Color(0xFFFFF9C4)
        "Specialized"  -> Color(0xFFE3EFFF)
        else           -> Color(0xFFE8F5E9)
    }
    val typeLabelColor = when (category.type) {
        "Prescription" -> Color(0xFFBA1A1A)
        "Supplement"   -> Color(0xFFF9A825)
        "Specialized"  -> Color(0xFF0051D5)
        else           -> Color(0xFF006B2C)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(typeColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = category.icon, fontSize = 30.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = category.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0E1D2A),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .background(typeColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(text = category.type, fontSize = 10.sp, color = typeLabelColor, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${category.medicineCount} medicines",
                fontSize = 11.sp,
                color = Color(0xFF6F7A6E)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BrowseCategoriesPreview() {
    BrowseCategoriesScreen(
        categories = listOf(
            CategoryModel("1", "Pain Relief", "", "For pain", "💊", "OTC", true, 12),
            CategoryModel("2", "Antibiotics", "", "For infections", "🦠", "Prescription", true, 8),
            CategoryModel("3", "Vitamins", "", "Supplements", "🌿", "Supplement", true, 20),
            CategoryModel("4", "Skincare", "", "Skin products", "🩺", "Specialized", true, 5),
        )
    )
}
