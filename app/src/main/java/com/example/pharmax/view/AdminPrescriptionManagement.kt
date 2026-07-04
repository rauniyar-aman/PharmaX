package com.example.pharmax.view

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.pharmax.model.PrescriptionModel
import com.example.pharmax.ui.theme.PharmaXTheme
import com.example.pharmax.viewmodel.ADMIN_NOTIFICATION_BUCKET
import com.example.pharmax.viewmodel.NotificationViewModel
import com.example.pharmax.viewmodel.PrescriptionViewModel
import com.example.pharmax.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminPrescriptionManagement : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PharmaXTheme {
                AdminPrescriptionManagementBody()
            }
        }
    }
}

@Composable
fun AdminPrescriptionManagementBody() {
    val context = LocalContext.current
    val vm: PrescriptionViewModel = viewModel()
    val userVm: UserViewModel = viewModel()
    val notificationVm: NotificationViewModel = viewModel()

    val prescriptions by vm.prescriptions.collectAsState()
    val isLoading by vm.loading.collectAsState()
    val adminUser by userVm.user.collectAsState()
    val isLoggedOut by userVm.isLoggedOut.collectAsState()
    val unreadCount by notificationVm.unreadCount.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadAllPrescriptions()
        userVm.loadCurrentUser()
        notificationVm.loadNotifications(ADMIN_NOTIFICATION_BUCKET)
    }

    LaunchedEffect(isLoggedOut) {
        if (isLoggedOut) {
            val intent = Intent(context, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }

    AdminPrescriptionManagementScreen(
        prescriptions = prescriptions,
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
        onNavigateCategories = { context.startActivity(Intent(context, AdminCategoryManagement::class.java)) },
        onNavigateProfile = { context.startActivity(Intent(context, AdminProfileActivity::class.java)) },
        onNavigateOrders = { context.startActivity(Intent(context, AdminOrderManagement::class.java)) },
        onLogout = { userVm.logOut() },
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
        }
    )
}

@Composable
fun AdminPrescriptionManagementScreen(
    prescriptions: List<PrescriptionModel> = emptyList(),
    isLoading: Boolean = false,
    adminName: String = "Admin",
    adminEmail: String = "",
    adminPhotoUrl: String = "",
    unreadNotificationCount: Int = 0,
    onNotificationsClick: () -> Unit = {},
    onNavigateDashboard: () -> Unit = {},
    onNavigateMedicines: () -> Unit = {},
    onNavigateCategories: () -> Unit = {},
    onNavigateProfile: () -> Unit = {},
    onNavigateOrders: () -> Unit = {},
    onLogout: () -> Unit = {},
    onPrescriptionClick: (PrescriptionModel) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Pending", "Approved", "Rejected")
    val filtered = if (selectedFilter == "All") prescriptions else prescriptions.filter { it.status == selectedFilter }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AdminSideDrawer(
                adminName = adminName,
                adminEmail = adminEmail,
                adminPhotoUrl = adminPhotoUrl,
                activeItem = "Prescriptions",
                onClose = { scope.launch { drawerState.close() } },
                onNavigateDashboard = { scope.launch { drawerState.close() }; onNavigateDashboard() },
                onNavigateMedicines = { scope.launch { drawerState.close() }; onNavigateMedicines() },
                onNavigateCategories = { scope.launch { drawerState.close() }; onNavigateCategories() },
                onNavigatePrescriptions = { scope.launch { drawerState.close() } },
                onNavigateOrders = { scope.launch { drawerState.close() }; onNavigateOrders() },
                onLogout = { scope.launch { drawerState.close() }; onLogout() }
            )
        }
    ) {
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).windowInsetsPadding(WindowInsets.systemBars)) {

        // ── Top bar ───────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp).clickable { scope.launch { drawerState.open() } }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "Prescriptions", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006B2C), modifier = Modifier.weight(1f))
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

        // ── Filter chips ─────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp).horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = if (selectedFilter == filter) Color(0xFF00501F) else MaterialTheme.colorScheme.surface,
                    modifier = Modifier.height(36.dp).clickable { selectedFilter = filter }
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = filter,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selectedFilter == filter) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF006B2C))
                }
            }
            filtered.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📄", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "No prescriptions found", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filtered) { prescription ->
                        AdminPrescriptionCard(prescription, onClick = { onPrescriptionClick(prescription) })
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
    }
}

@Composable
private fun AdminPrescriptionCard(prescription: PrescriptionModel, onClick: () -> Unit) {
    val statusColor = when (prescription.status) {
        "Approved" -> Color(0xFF006B2C)
        "Rejected" -> Color(0xFFBA1A1A)
        else -> Color(0xFFE65100)
    }
    val statusBg = when (prescription.status) {
        "Approved" -> Color(0xFFE8F5E9)
        "Rejected" -> Color(0xFFFFEDED)
        else -> Color(0xFFFFF3E0)
    }
    val dateText = remember(prescription.uploadedAt) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(prescription.uploadedAt))
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(44.dp).background(MaterialTheme.colorScheme.secondaryContainer, androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = prescription.userName.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0051D5)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = prescription.userName.ifBlank { "Unknown User" }, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    text = prescription.name.ifBlank { prescription.medicineName.ifBlank { "General Prescription" } },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(text = dateText, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Box(modifier = Modifier.background(statusBg, RoundedCornerShape(50.dp)).padding(horizontal = 10.dp, vertical = 5.dp)) {
                Text(text = prescription.status, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = statusColor)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminPrescriptionManagementPreview() {
    AdminPrescriptionManagementScreen(
        prescriptions = listOf(
            PrescriptionModel(userName = "Bikash Gurung", medicineName = "Amoxicillin 500mg", status = "Pending"),
            PrescriptionModel(userName = "Sunita Poudel", medicineName = "Tramadol 50mg", status = "Approved"),
        )
    )
}
