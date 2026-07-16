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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pharmax.model.MedicineModel
import com.example.pharmax.ui.theme.PharmaXTheme
import com.example.pharmax.viewmodel.ADMIN_NOTIFICATION_BUCKET
import com.example.pharmax.viewmodel.MedicineViewModel
import com.example.pharmax.viewmodel.NotificationViewModel
import com.example.pharmax.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class AdminMedicineManagement : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PharmaXTheme {
                AdminMedicineManagementBody()
            }
        }
    }
}

@Composable
fun AdminMedicineManagementBody() {
    val context = LocalContext.current
    val vm: MedicineViewModel = viewModel()
    val userVm: UserViewModel = viewModel()
    val notificationVm: NotificationViewModel = viewModel()

    val message by vm.message.collectAsState()
    val medicines by vm.medicines.collectAsState()
    val isLoading by vm.loading.collectAsState()
    val adminUser by userVm.user.collectAsState()
    val isLoggedOut by userVm.isLoggedOut.collectAsState()
    val unreadCount by notificationVm.unreadCount.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadMedicines()
        userVm.loadCurrentUser()
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

    AdminMedicineManagementScreen(
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
        onNavigateCategories = { context.startActivity(Intent(context, AdminCategoryManagement::class.java)) },
        onNavigatePrescriptions = { context.startActivity(Intent(context, AdminPrescriptionManagement::class.java)) },
        onNavigateOrders = { context.startActivity(Intent(context, AdminOrderManagement::class.java)) },
        onNavigateProfile = { context.startActivity(Intent(context, AdminProfileActivity::class.java)) },
        onLogout = { userVm.logOut() },
        onDelete = { id -> vm.deleteMedicine(id) {} },
        onView = { medicine ->
            val intent = Intent(context, AdminMedicineDetailActivity::class.java).apply {
                putExtra("medicineId", medicine.medicineId)
                putExtra("name", medicine.name)
                putExtra("brand", medicine.brand)
                putExtra("category", medicine.category)
                putExtra("description", medicine.description)
                putExtra("price", medicine.price)
                putExtra("quantity", medicine.quantity)
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
                putExtra("quantity", medicine.quantity)
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
    adminName: String = "Admin",
    adminEmail: String = "",
    adminPhotoUrl: String = "",
    unreadNotificationCount: Int = 0,
    onNotificationsClick: () -> Unit = {},
    onDelete: (String) -> Unit = {},
    onView: (MedicineModel) -> Unit = {},
    onEdit: (MedicineModel) -> Unit = {},
    onAddClick: () -> Unit = {},
    onNavigateDashboard: () -> Unit = {},
    onNavigateCategories: () -> Unit = {},
    onNavigatePrescriptions: () -> Unit = {},
    onNavigateOrders: () -> Unit = {},
    onNavigateProfile: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    val filtered = medicines.filter { medicine ->
        medicine.name.contains(searchQuery, ignoreCase = true) ||
                medicine.brand.contains(searchQuery, ignoreCase = true)
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AdminSideDrawer(
                adminName = adminName,
                adminEmail = adminEmail,
                adminPhotoUrl = adminPhotoUrl,
                activeItem = "Medicines",
                onClose = { scope.launch { drawerState.close() } },
                onNavigateDashboard = { scope.launch { drawerState.close() }; onNavigateDashboard() },
                onNavigateMedicines = { scope.launch { drawerState.close() } },
                onNavigateCategories = { scope.launch { drawerState.close() }; onNavigateCategories() },
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
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Medicine")
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {

            // ── Top bar ───────────────────────────────────────────────────
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
                Text(text = "Medicine Management", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                Box {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp).clickable { onNotificationsClick() }
                    )
                    if (unreadNotificationCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color.Red, CircleShape)
                                .align(Alignment.TopEnd)
                        )
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

                // ── Search bar ────────────────────────────────────────────
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        placeholder = { Text("Search medicines...") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                        singleLine = true,
                        shape = RoundedCornerShape(50.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
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
                            Text(text = "No medicines found", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Tap + to add your first medicine", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
}

@Composable
fun MedicineListItem(
    medicine: MedicineModel,
    onView: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {

            // Image thumbnail
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (medicine.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = medicine.imageUrl,
                        contentDescription = medicine.name,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(text = "💊", fontSize = 36.sp)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = medicine.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = medicine.brand, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(4.dp))

                if (medicine.category.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = medicine.category.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
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
                        text = "Qty: ${medicine.quantity}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "View",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { onView() }
                        )
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { onEdit() }
                        )
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
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
        MedicineModel("1", "Paracetamol 500mg", "Panadol Professional", "Pain Relief", price = 120.0, quantity = 42),
        MedicineModel("2", "Amoxicillin Syrup", "GSK Pharma", "Antibiotics", price = 450.0, quantity = 8),
        MedicineModel("3", "Vitamin C 1000mg", "Nature's Health", "Supplements", price = 890.0, quantity = 124),
        MedicineModel("4", "Digital Thermometer", "Omron Medical", "Devices", price = 1200.0, quantity = 0)
    )
    AdminMedicineManagementScreen(medicines = sampleMedicines)
}
