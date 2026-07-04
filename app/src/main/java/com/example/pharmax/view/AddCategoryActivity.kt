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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.material3.MaterialTheme
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
import android.content.Intent
import com.example.pharmax.model.CategoryModel
import com.example.pharmax.ui.theme.PharmaXTheme
import com.example.pharmax.viewmodel.ADMIN_NOTIFICATION_BUCKET
import com.example.pharmax.viewmodel.CategoryViewModel
import com.example.pharmax.viewmodel.NotificationViewModel
import com.example.pharmax.viewmodel.UserViewModel

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
                isActive = intent.getBooleanExtra("isActive", true),
                slug = intent.getStringExtra("slug") ?: ""
            )
        }

        setContent {
            PharmaXTheme {
                AddCategoryBody(editCategory = editCategory, onBack = { finish() })
            }
        }
    }
}

@Composable
fun AddCategoryBody(editCategory: CategoryModel? = null, onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val vm: CategoryViewModel = viewModel()
    val userVm: UserViewModel = viewModel()
    val notificationVm: NotificationViewModel = viewModel()
    val message by vm.message.collectAsState()
    val isLoading by vm.loading.collectAsState()
    val adminUser by userVm.user.collectAsState()
    val unreadCount by notificationVm.unreadCount.collectAsState()

    LaunchedEffect(Unit) {
        userVm.loadCurrentUser()
        notificationVm.loadNotifications(ADMIN_NOTIFICATION_BUCKET)
    }

    LaunchedEffect(message) {
        if (!message.isNullOrBlank()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            vm.clearMessage()
        }
    }

    AddCategoryScreen(
        editCategory = editCategory,
        isLoading = isLoading,
        adminName = adminUser?.fullName ?: "Admin",
        adminPhotoUrl = adminUser?.profileImageUrl ?: "",
        unreadNotificationCount = unreadCount,
        onNotificationsClick = {
            val i = Intent(context, NotificationCenterActivity::class.java)
            i.putExtra("recipientId", ADMIN_NOTIFICATION_BUCKET)
            context.startActivity(i)
        },
        onSave = { name, description, icon, active ->
            if (editCategory != null) {
                vm.updateCategory(editCategory.copy(name = name, description = description, icon = icon, isActive = active)) { onBack() }
            } else {
                vm.addCategory(name, description, icon, active) { onBack() }
            }
        },
        onBack = onBack
    )
}

@Composable
fun AddCategoryScreen(
    editCategory: CategoryModel? = null,
    isLoading: Boolean = false,
    adminName: String = "Admin",
    adminPhotoUrl: String = "",
    unreadNotificationCount: Int = 0,
    onNotificationsClick: () -> Unit = {},
    onSave: (String, String, String, Boolean) -> Unit = { _, _, _, _ -> },
    onBack: () -> Unit = {}
) {
    val materialIcons = listOf("💊", "❤️", "🦠", "🧪", "🩹", "⚕️")
    val emojiIcons = listOf("🌿", "🩺", "🩻", "💉", "🔬", "🧬")

    var selectedTab by remember { mutableStateOf(0) }
    var selectedIcon by remember { mutableStateOf(editCategory?.icon ?: "💊") }
    var categoryName by remember { mutableStateOf(editCategory?.name ?: "") }
    var description by remember { mutableStateOf(editCategory?.description ?: "") }
    var isActive by remember { mutableStateOf(editCategory?.isActive ?: true) }

    val currentIcons = if (selectedTab == 0) materialIcons else emojiIcons
    val title = if (editCategory != null) "Edit Category" else "Add Category"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.systemBars)
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
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBack() }
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
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp).clickable { onNotificationsClick() }
                )
                if (unreadNotificationCount > 0) {
                    Box(modifier = Modifier.size(8.dp).background(Color.Red, CircleShape).align(Alignment.TopEnd))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            AvatarCircle(
                photoUrl = adminPhotoUrl,
                fallbackText = adminName.firstOrNull()?.uppercaseChar()?.toString() ?: "A",
                size = 36.dp
            )
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Category Icon", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Tab toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                            .padding(3.dp)
                    ) {
                        listOf("Material Symbol", "Emoji").forEachIndexed { idx, label ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (selectedTab == idx) MaterialTheme.colorScheme.surface else Color.Transparent,
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
                                    color = if (selectedTab == idx) Color(0xFF006B2C) else MaterialTheme.colorScheme.onSurfaceVariant
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
                                                color = if (selectedIcon == icon) Color(0xFF006B2C) else MaterialTheme.colorScheme.outline,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .background(
                                                if (selectedIcon == icon) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surface,
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Category Name
                    Text(text = "Category Name", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = categoryName,
                        onValueChange = { categoryName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. Pain Relief", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = Color(0xFF006B2C),
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Description
                    Text(text = "Description", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier.fillMaxWidth().height(110.dp),
                        placeholder = { Text("Brief summary for this category...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = Color(0xFF006B2C),
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Is Active toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Is Active", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                            Text(text = "Visible to customers in the storefront", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = isActive,
                            onCheckedChange = { isActive = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF006B2C))
                        )
                    }
                }
            }

            // ── Bottom action buttons ─────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onBack() },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(text = "Discard", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                }

                ElevatedButton(
                    onClick = { onSave(categoryName, description, selectedIcon, isActive) },
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddCategoryPreview() {
    AddCategoryScreen()
}
