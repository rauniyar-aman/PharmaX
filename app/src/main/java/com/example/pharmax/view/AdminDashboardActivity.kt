package com.example.pharmax.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pharmax.model.MedicineModel
import com.example.pharmax.model.PrescriptionModel
import com.example.pharmax.ui.theme.PharmaXTheme
import com.example.pharmax.viewmodel.ADMIN_NOTIFICATION_BUCKET
import com.example.pharmax.viewmodel.MedicineViewModel
import com.example.pharmax.viewmodel.NotificationViewModel
import com.example.pharmax.viewmodel.PrescriptionViewModel
import com.example.pharmax.viewmodel.UserViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.launch

class AdminDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PharmaXTheme {
                AdminDashboardBody()
            }
        }
    }
}

@Composable
fun AdminDashboardBody() {
    val context = LocalContext.current
    val vm: MedicineViewModel = viewModel()
    val userVm: UserViewModel = viewModel()
    val prescriptionVm: PrescriptionViewModel = viewModel()
    val notificationVm: NotificationViewModel = viewModel()

    val message by vm.message.collectAsState()
    val medicines by vm.medicines.collectAsState()
    val isLoggedOut by userVm.isLoggedOut.collectAsState()
    val adminUser by userVm.user.collectAsState()
    val prescriptions by prescriptionVm.prescriptions.collectAsState()
    val unreadCount by notificationVm.unreadCount.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadMedicines()
        userVm.loadCurrentUser()
        prescriptionVm.loadAllPrescriptions()
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

    val pendingPrescriptions = prescriptions.filter { it.status == "Pending" }

    AdminDashboardScreen(
        adminName = adminUser?.fullName ?: "Admin",
        adminEmail = adminUser?.email ?: "",
        adminPhotoUrl = adminUser?.profileImageUrl ?: "",
        unreadNotificationCount = unreadCount,
        onNotificationsClick = {
            val i = Intent(context, NotificationCenterActivity::class.java)
            i.putExtra("recipientId", ADMIN_NOTIFICATION_BUCKET)
            context.startActivity(i)
        },
        totalMedicineCount = medicines.size,
        pendingPrescriptions = pendingPrescriptions,
        pendingPrescriptionCount = pendingPrescriptions.size,
        onAddMedicine = { context.startActivity(Intent(context, AddMedicineActivity::class.java)) },
        onAddCategory = { context.startActivity(Intent(context, AddCategoryActivity::class.java)) },
        onNavigateMedicines = { context.startActivity(Intent(context, AdminMedicineManagement::class.java)) },
        onNavigateCategories = { context.startActivity(Intent(context, AdminCategoryManagement::class.java)) },
        onNavigatePrescriptions = { context.startActivity(Intent(context, AdminPrescriptionManagement::class.java)) },
        onNavigateOrders = { context.startActivity(Intent(context, AdminOrderManagement::class.java)) },
        onPrescriptionClick = { prescription ->
            val intent = Intent(context, AdminPrescriptionDetailActivity::class.java)
            intent.putExtra("prescriptionId", prescription.prescriptionId)
            intent.putExtra("userId", prescription.userId)
            intent.putExtra("userName", prescription.userName)
            intent.putExtra("userPhone", prescription.userPhone)
            intent.putExtra("name", prescription.name)
            intent.putExtra("medicineName", prescription.medicineName)
            intent.putExtra("imageUrl", prescription.imageUrl)
            intent.putExtra("notes", prescription.notes)
            intent.putExtra("status", prescription.status)
            intent.putExtra("adminComment", prescription.adminComment)
            intent.putExtra("uploadedAt", prescription.uploadedAt)
            context.startActivity(intent)
        },
        onNavigateProfile = { context.startActivity(Intent(context, AdminProfileActivity::class.java)) },
        onLogout = { userVm.logOut() }
    )
}

@Composable
fun AdminDashboardScreen(
    adminName: String = "Admin",
    adminEmail: String = "",
    adminPhotoUrl: String = "",
    unreadNotificationCount: Int = 0,
    onNotificationsClick: () -> Unit = {},
    totalMedicineCount: Int = 0,
    pendingPrescriptions: List<PrescriptionModel> = emptyList(),
    pendingPrescriptionCount: Int = 0,
    onAddMedicine: () -> Unit = {},
    onAddCategory: () -> Unit = {},
    onNavigateMedicines: () -> Unit = {},
    onNavigateCategories: () -> Unit = {},
    onNavigatePrescriptions: () -> Unit = {},
    onNavigateOrders: () -> Unit = {},
    onPrescriptionClick: (PrescriptionModel) -> Unit = {},
    onNavigateProfile: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AdminSideDrawer(
                adminName = adminName,
                adminEmail = adminEmail,
                adminPhotoUrl = adminPhotoUrl,
                activeItem = "Dashboard",
                onClose = { scope.launch { drawerState.close() } },
                onNavigateDashboard = { scope.launch { drawerState.close() } },
                onNavigateMedicines = { scope.launch { drawerState.close() }; onNavigateMedicines() },
                onNavigateCategories = { scope.launch { drawerState.close() }; onNavigateCategories() },
                onNavigatePrescriptions = { scope.launch { drawerState.close() }; onNavigatePrescriptions() },
                onNavigateOrders = { scope.launch { drawerState.close() }; onNavigateOrders() },
                onLogout = { scope.launch { drawerState.close() }; onLogout() }
            )
        }
    ) {
        Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
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
                Text(
                    text = "PharmaX Admin",
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
                    size = 36.dp,
                    modifier = Modifier.clickable { onNavigateProfile() }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                // ── 3 Stat cards ──────────────────────────────────────────
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdminStatDashCard(
                        label = "Pending Prescription",
                        value = "$pendingPrescriptionCount",
                        color = Color(0xFFE65100),
                        bgColor = Color(0xFFFFF3E0),
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatDashCard(
                        label = "Medicines",
                        value = "$totalMedicineCount",
                        color = Color(0xFF0051D5),
                        bgColor = Color(0xFFE3EFFF),
                        modifier = Modifier.weight(1f)
                    )
                }

                // ── Prescriptions Awaiting Verification ───────────────────
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    AdminSectionHeader(title = "Prescriptions Awaiting Verification")
                    if (pendingPrescriptions.isNotEmpty()) {
                        Text(
                            text = "View All",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0051D5),
                            modifier = Modifier.clickable { onNavigatePrescriptions() }
                        )
                    }
                }
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        if (pendingPrescriptions.isEmpty()) {
                            Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                                Text(text = "No prescriptions awaiting review", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        } else {
                            pendingPrescriptions.take(3).forEachIndexed { index, prescription ->
                                PrescriptionDashRow(
                                    customerName = prescription.userName.ifBlank { "Unknown User" },
                                    medicineName = prescription.medicineName.ifBlank { "General Prescription" },
                                    onClick = { onPrescriptionClick(prescription) }
                                )
                                if (index < pendingPrescriptions.take(3).lastIndex) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            }
                        }
                    }
                }

                // ── Quick Actions ─────────────────────────────────────────
                AdminSectionHeader(title = "Quick Actions")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ElevatedButton(
                        onClick = onAddMedicine,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Color(0xFF00501F),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Add Medicine", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    ElevatedButton(
                        onClick = onAddCategory,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Color(0xFF0051D5),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Add Category", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        } // end Scaffold
    }
}

// ── Sidebar Drawer ────────────────────────────────────────────────────────────
@Composable
fun AdminSideDrawer(
    adminName: String = "Admin",
    adminEmail: String = "",
    adminPhotoUrl: String = "",
    activeItem: String = "Dashboard",
    onClose: () -> Unit = {},
    onNavigateDashboard: () -> Unit = {},
    onNavigateMedicines: () -> Unit = {},
    onNavigateCategories: () -> Unit = {},
    onNavigatePrescriptions: () -> Unit = {},
    onNavigateOrders: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val navItems = listOf(
        Triple("Dashboard", Icons.Default.Dashboard, activeItem == "Dashboard"),
        Triple("Medicines", Icons.Default.Medication, activeItem == "Medicines"),
        Triple("Categories", Icons.Default.Category, activeItem == "Categories"),
        Triple("Prescriptions", Icons.Default.Description, activeItem == "Prescriptions"),
        Triple("Orders", Icons.Default.ShoppingCart, activeItem == "Orders")
    )

    Column(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
            .padding(top = 48.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarCircle(
                photoUrl = adminPhotoUrl,
                fallbackText = adminName.firstOrNull()?.uppercaseChar()?.toString() ?: "A",
                size = 40.dp,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = adminName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = adminEmail,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp).clickable { onClose() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(8.dp))

        navItems.forEach { (label, icon, isActive) ->
            DrawerNavItem(
                label = label,
                icon = icon,
                isActive = isActive,
                onClick = {
                    when (label) {
                        "Dashboard" -> onNavigateDashboard()
                        "Medicines" -> onNavigateMedicines()
                        "Categories" -> onNavigateCategories()
                        "Prescriptions" -> onNavigatePrescriptions()
                        "Orders" -> onNavigateOrders()
                        else -> onClose()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLogout() }
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Logout",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(text = "Logout", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun DrawerNavItem(label: String, icon: ImageVector, isActive: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isActive) MaterialTheme.colorScheme.tertiaryContainer else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isActive) Color(0xFF006B2C) else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isActive) Color(0xFF006B2C) else MaterialTheme.colorScheme.onSurface
        )
    }
}

// ── Helper composables ────────────────────────────────────────────────────────

@Composable
fun AdminStatDashCard(label: String, value: String, color: Color, bgColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = label, fontSize = 11.sp, color = color.copy(alpha = 0.8f), fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun AdminSectionHeader(title: String) {
    Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
}

@Composable
fun PrescriptionDashRow(customerName: String, medicineName: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = customerName.first().uppercase(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0051D5))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = customerName, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            Text(text = medicineName, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(50.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(text = "Review", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF006B2C))
        }
    }
}

@Composable
fun AvatarCircle(
    photoUrl: String,
    fallbackText: String,
    size: Dp,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF006B2C),
    textColor: Color = Color.White,
    fontSize: TextUnit = 14.sp
) {
    Box(
        modifier = modifier.size(size).background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (photoUrl.isNotBlank()) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize().clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(text = fallbackText, fontSize = fontSize, fontWeight = FontWeight.Bold, color = textColor)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminDashboardPreview() {
    AdminDashboardScreen(totalMedicineCount = 3)
}
