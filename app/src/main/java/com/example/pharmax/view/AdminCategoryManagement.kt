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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.pharmax.model.MedicineModel
import com.example.pharmax.ui.theme.PharmaXTheme
import com.example.pharmax.viewmodel.ADMIN_NOTIFICATION_BUCKET
import com.example.pharmax.viewmodel.CategoryViewModel
import com.example.pharmax.viewmodel.MedicineViewModel
import com.example.pharmax.viewmodel.NotificationViewModel
import com.example.pharmax.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class AdminCategoryManagement : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PharmaXTheme {
                AdminCategoryManagementBody()
            }
        }
    }
}

@Composable
fun AdminCategoryManagementBody() {
    val context = LocalContext.current
    val vm: CategoryViewModel = viewModel()
    val userVm: UserViewModel = viewModel()
    val medicineVm: MedicineViewModel = viewModel()

    val message by vm.message.collectAsState()
    val categories by vm.categories.collectAsState()
    val isLoading by vm.loading.collectAsState()
    val adminUser by userVm.user.collectAsState()
    val isLoggedOut by userVm.isLoggedOut.collectAsState()
    val medicines by medicineVm.medicines.collectAsState()
    val notificationVm: NotificationViewModel = viewModel()
    val unreadCount by notificationVm.unreadCount.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadCategories()
        userVm.loadCurrentUser()
        medicineVm.loadMedicines()
        notificationVm.loadNotifications(ADMIN_NOTIFICATION_BUCKET)
    }

    LaunchedEffect(message) {
        if (!message.isNullOrBlank()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            vm.clearMessage()
        }
    }

    LaunchedEffect(isLoggedOut) {
        if (isLoggedOut) {
            val intent = Intent(context, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }

    RequireAdminAccess(role = adminUser?.role)

    AdminCategoryManagementScreen(
        categories = categories,
        medicines = medicines,
        isLoading = isLoading,
        adminName = adminUser?.fullName ?: "Admin",
        adminEmail = adminUser?.email ?: "",
        adminPhotoUrl = adminUser?.profileImageUrl ?: "",
        unreadNotificationCount = unreadCount,
        onNotificationsClick = {
            val i = Intent(context, NotificationCenterActivity::class.java)
            i.putExtra("recipientId", ADMIN_NOTIFICATION_BUCKET)
            context.startActivity(i)
        },
        onNavigateDashboard = { context.startActivity(Intent(context, AdminDashboardActivity::class.java)) },
        onNavigateMedicines = { context.startActivity(Intent(context, AdminMedicineManagement::class.java)) },
        onNavigatePrescriptions = { context.startActivity(Intent(context, AdminPrescriptionManagement::class.java)) },
        onNavigateOrders = { context.startActivity(Intent(context, AdminOrderManagement::class.java)) },
        onNavigateProfile = { context.startActivity(Intent(context, AdminProfileActivity::class.java)) },
        onLogout = { userVm.logOut() },
        onToggleStatus = { id, status -> vm.toggleStatus(id, status) },
        onDelete = { id -> vm.deleteCategory(id) {} },
        onEdit = { category ->
            val intent = Intent(context, AddCategoryActivity::class.java).apply {
                putExtra("categoryId", category.categoryId)
                putExtra("name", category.name)
                putExtra("description", category.description)
                putExtra("icon", category.icon)
                putExtra("isActive", category.isActive)
                putExtra("slug", category.slug)
            }
            context.startActivity(intent)
        },
        onAddClick = { context.startActivity(Intent(context, AddCategoryActivity::class.java)) }
    )
}

@Composable
fun AdminCategoryManagementScreen(
    categories: List<CategoryModel> = emptyList(),
    medicines: List<MedicineModel> = emptyList(),
    isLoading: Boolean = false,
    adminName: String = "Admin",
    adminEmail: String = "",
    adminPhotoUrl: String = "",
    unreadNotificationCount: Int = 0,
    onNotificationsClick: () -> Unit = {},
    onToggleStatus: (String, Boolean) -> Unit = { _, _ -> },
    onDelete: (String) -> Unit = {},
    onEdit: (CategoryModel) -> Unit = {},
    onAddClick: () -> Unit = {},
    onNavigateDashboard: () -> Unit = {},
    onNavigateMedicines: () -> Unit = {},
    onNavigatePrescriptions: () -> Unit = {},
    onNavigateOrders: () -> Unit = {},
    onNavigateProfile: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    val filtered = categories.filter { category ->
        category.name.contains(searchQuery, ignoreCase = true)
    }

    val totalActive = categories.count { it.isActive }
    val totalMedicines = medicines.size

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AdminSideDrawer(
                adminName = adminName,
                adminEmail = adminEmail,
                adminPhotoUrl = adminPhotoUrl,
                activeItem = "Categories",
                onClose = { scope.launch { drawerState.close() } },
                onNavigateDashboard = { scope.launch { drawerState.close() }; onNavigateDashboard() },
                onNavigateMedicines = { scope.launch { drawerState.close() }; onNavigateMedicines() },
                onNavigateCategories = { scope.launch { drawerState.close() } },
                onNavigatePrescriptions = { scope.launch { drawerState.close() }; onNavigatePrescriptions() },
                onNavigateOrders = { scope.launch { drawerState.close() }; onNavigateOrders() },
                onLogout = { scope.launch { drawerState.close() }; onLogout() }
            )
        }
    ) {
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
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { scope.launch { drawerState.open() } }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Medicine Categories", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006B2C), modifier = Modifier.weight(1f))
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
                    size = 36.dp,
                    modifier = Modifier.clickable { onNavigateProfile() }
                )
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
                        AdminStatCard(label = "Total SKUs", value = "$totalMedicines", valueColor = MaterialTheme.colorScheme.onSurface)
                    }
                }

                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        placeholder = { Text("Search categories...") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                        singleLine = true,
                        shape = RoundedCornerShape(50.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = Color(0xFF006B2C),
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant
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
                            Text(text = "No categories yet", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Tap + to add your first category", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                items(filtered) { category ->
                    CategoryListItem(
                        category = category,
                        medicineCount = medicines.count { it.category == category.name },
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
}

@Composable
fun AdminStatCard(label: String, value: String, valueColor: Color) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
            Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}

@Composable
fun CategoryListItem(
    category: CategoryModel,
    medicineCount: Int = 0,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                        .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = category.icon, fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = category.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(50.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(text = "$medicineCount medicines", fontSize = 11.sp, color = Color(0xFF006B2C), fontWeight = FontWeight.Medium)
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
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Delete", fontSize = 13.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminCategoryManagementPreview() {
    val sampleCategories = listOf(
        CategoryModel("1", "Antibiotics", "/slug/antibiotics", "For bacterial infections", "💊", true),
        CategoryModel("2", "Vitamins", "/slug/vitamins", "Dietary supplements", "🌿", true),
        CategoryModel("3", "Pain Relief", "/slug/pain-relief", "Analgesics", "🩹", false)
    )
    AdminCategoryManagementScreen(categories = sampleCategories)
}
