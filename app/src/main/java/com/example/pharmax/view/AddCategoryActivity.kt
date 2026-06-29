package com.example.pharmax.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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

class AddCategoryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val editCategory = intent?.getStringExtra("categoryId")?.let { id ->
            CategoryModel(
                categoryId = id,
                name = intent.getStringExtra("name") ?: "",
                description = intent.getStringExtra("description") ?: "",
                icon = intent.getStringExtra("icon") ?: "💊",
                type = intent.getStringExtra("type") ?: "OTC",
                isActive = intent.getBooleanExtra("isActive", true),
                slug = intent.getStringExtra("slug") ?: "",
                medicineCount = intent.getIntExtra("medicineCount", 0)
            )
        }

        setContent {
            AddCategoryScreen(editCategory = editCategory)
        }
    }
}

@Composable
fun AddCategoryScreen(editCategory: CategoryModel? = null) {
    val context = LocalContext.current
    val vm: CategoryViewModel = viewModel()

    val message by vm.message.collectAsState()
    val isLoading by vm.loading.collectAsState()

    val isEditMode = editCategory != null
    val materialIcons = listOf("💊", "❤️", "🦠", "🧪", "🩹", "⚕️")
    val emojiIcons = listOf("🌿", "🩺", "🩻", "💉", "🔬", "🧬")
    val typeOptions = listOf("OTC", "Prescription", "Supplement", "Specialized")

    var selectedTab by remember { mutableStateOf(0) }
    var selectedIcon by remember { mutableStateOf(editCategory?.icon ?: "💊") }
    var categoryName by remember { mutableStateOf(editCategory?.name ?: "") }
    var description by remember { mutableStateOf(editCategory?.description ?: "") }
    var selectedType by remember { mutableStateOf(editCategory?.type ?: "OTC") }
    var isActive by remember { mutableStateOf(editCategory?.isActive ?: true) }

    LaunchedEffect(message) {
        if (!message.isNullOrBlank()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            vm.clearMessage()
        }
    }

    val currentIcons = if (selectedTab == 0) materialIcons else emojiIcons
    val title = if (isEditMode) "Edit Category" else "Add Category"

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
                modifier = Modifier
                    .size(24.dp)
                    .clickable { (context as? ComponentActivity)?.finish() }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF006B2C),
                modifier = Modifier.weight(1f)
            )
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Icon picker card ──────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Category Icon", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0E1D2A))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Tab toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F4F8), RoundedCornerShape(8.dp))
                            .padding(3.dp)
                    ) {
                        listOf("Material Symbol", "Emoji").forEachIndexed { idx, label ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (selectedTab == idx) Color.White else Color.Transparent,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { selectedTab = idx }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (selectedTab == idx) Color(0xFF006B2C) else Color(0xFF6F7A6E)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Icon grid (2 rows × 3 cols)
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        currentIcons.chunked(3).forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                row.forEach { icon ->
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .border(
                                                width = if (selectedIcon == icon) 2.dp else 1.dp,
                                                color = if (selectedIcon == icon) Color(0xFF006B2C) else Color(0xFFDDE1E7),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .background(
                                                if (selectedIcon == icon) Color(0xFFE8F5E9) else Color.White,
                                                RoundedCornerShape(10.dp)
                                            )
                                            .clickable { selectedIcon = icon },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = icon, fontSize = 28.sp)
                                    }
                                }
                                // Fill remaining cells if row is incomplete
                                repeat(3 - row.size) {
                                    Spacer(modifier = Modifier.size(72.dp))
                                }
                            }
                        }
                    }
                }
            }

            // ── Form card ─────────────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Category Name
                    Text(text = "Category Name", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF0E1D2A))
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = categoryName,
                        onValueChange = { categoryName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. Pain Relief", color = Color(0xFF9EA7A0)) },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color(0xFF006B2C),
                            unfocusedIndicatorColor = Color(0xFFDDE1E7)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Category Type
                    Text(text = "Category Type", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF0E1D2A))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        typeOptions.forEach { type ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (selectedType == type) Color(0xFF00501F) else Color(0xFFF1F4F8),
                                        RoundedCornerShape(50.dp)
                                    )
                                    .clickable { selectedType = type }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = type,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (selectedType == type) Color.White else Color(0xFF6F7A6E)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Description
                    Text(text = "Description", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF0E1D2A))
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier.fillMaxWidth().height(110.dp),
                        placeholder = { Text("Brief summary for this category...", color = Color(0xFF9EA7A0)) },
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color(0xFF006B2C),
                            unfocusedIndicatorColor = Color(0xFFDDE1E7)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Is Active toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Is Active", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color(0xFF0E1D2A))
                            Text(text = "Visible to customers in the storefront", fontSize = 12.sp, color = Color(0xFF6F7A6E))
                        }
                        Switch(
                            checked = isActive,
                            onCheckedChange = { isActive = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF006B2C))
                        )
                    }
                }
            }

            // ── Live preview ──────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "👁", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "LIVE PREVIEW", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF6F7A6E), letterSpacing = 1.sp)
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Row(
                            modifier = Modifier
                                .background(
                                    if (isActive) Color(0xFFE8F5E9) else Color(0xFFFFEDED),
                                    RoundedCornerShape(50.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        if (isActive) Color(0xFF006B2C) else Color(0xFFBA1A1A),
                                        CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (isActive) "ACTIVE" else "INACTIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) Color(0xFF006B2C) else Color(0xFFBA1A1A)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color(0xFFE3F2FD), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = selectedIcon, fontSize = 32.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = categoryName.ifBlank { "New Category" },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0E1D2A)
                    )
                    Text(text = "0 medicines available", fontSize = 13.sp, color = Color(0xFF6F7A6E))

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F4F8), RoundedCornerShape(50.dp))
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Browse Medicines", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF0E1D2A))
                    }
                }
            }

            // ── Clinical approval info card ────────────────────────────────
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FAF4)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = "🛡", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "Clinical Approval Required", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF006B2C))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "New categories are automatically audited by the compliance engine to ensure clinical accuracy before being pushed to global pharmacy nodes.",
                            fontSize = 12.sp,
                            color = Color(0xFF4A6659),
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Bottom action buttons ─────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { (context as? ComponentActivity)?.finish() },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF0E1D2A)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDE1E7))
                ) {
                    Text(text = "Discard", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                }

                ElevatedButton(
                    onClick = {
                        if (isEditMode(editCategory)) {
                            vm.updateCategory(
                                editCategory!!.copy(
                                    name = categoryName,
                                    description = description,
                                    icon = selectedIcon,
                                    type = selectedType,
                                    isActive = isActive
                                )
                            ) { (context as? ComponentActivity)?.finish() }
                        } else {
                            vm.addCategory(categoryName, description, selectedIcon, selectedType, isActive) {
                                (context as? ComponentActivity)?.finish()
                            }
                        }
                    },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = Color(0xFF00501F),
                        contentColor = Color.White
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text(text = "Save Category", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun isEditMode(editCategory: CategoryModel?): Boolean = editCategory != null

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddCategoryPreview() {
    AddCategoryScreen()
}

@Preview(showBackground = true, showSystemUi = true, name = "Edit Mode")
@Composable
fun EditCategoryPreview() {
    AddCategoryScreen(
        editCategory = CategoryModel("1", "Antibiotics", "/slug/antibiotics", "For bacterial infections", "💊", "OTC", true, 142)
    )
}
