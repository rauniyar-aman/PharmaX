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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pharmax.model.CategoryModel
import com.example.pharmax.ui.theme.PharmaXTheme
import com.example.pharmax.viewmodel.CategoryViewModel
import com.example.pharmax.viewmodel.NotificationViewModel
import com.example.pharmax.viewmodel.UserViewModel
import java.util.Calendar

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PharmaXTheme {
                DashboardBody()
            }
        }
    }
}

@Composable
fun DashboardBody() {
    val context = LocalContext.current
    val vm: UserViewModel = viewModel()
    val categoryVm: CategoryViewModel = viewModel()
    val notificationVm: NotificationViewModel = viewModel()

    val message by vm.message.collectAsState()
    val user by vm.user.collectAsState()
    val isLoggedOut by vm.isLoggedOut.collectAsState()
    val categories by categoryVm.categories.collectAsState()
    val unreadCount by notificationVm.unreadCount.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadCurrentUser()
        categoryVm.loadCategories()
    }

    LaunchedEffect(user) {
        user?.uid?.let { uid -> if (uid.isNotBlank()) notificationVm.loadNotifications(uid) }
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

    DashboardScreen(
        firstName = user?.fullName?.split(" ")?.firstOrNull() ?: "User",
        categories = categories.filter { it.isActive },
        unreadNotificationCount = unreadCount,
        onNotificationsClick = {
            val i = Intent(context, NotificationCenterActivity::class.java)
            i.putExtra("recipientId", user?.uid ?: "")
            context.startActivity(i)
        }
    )
}

@Composable
fun DashboardScreen(
    firstName: String = "User",
    categories: List<CategoryModel> = emptyList(),
    unreadNotificationCount: Int = 0,
    onNotificationsClick: () -> Unit = {}
) {
    val context = LocalContext.current

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else      -> "Good evening"
    }

    Scaffold(
        bottomBar = { DashboardBottomNav(activeTab = "Home") }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Header ────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "$greeting, $firstName", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006B2C))
                    Text(text = "Your health is our priority today.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Box {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(26.dp).clickable { onNotificationsClick() }
                    )
                    if (unreadNotificationCount > 0) {
                        Box(modifier = Modifier.size(8.dp).background(Color.Red, CircleShape).align(Alignment.TopEnd))
                    }
                }
            }

            // ── Search bar ───────────────────────────────────────────────
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
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

            Spacer(modifier = Modifier.height(16.dp))

            // ── Quick action chips ────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickChip(label = "All Medicines", icon = Icons.Default.Medication) {
                    context.startActivity(Intent(context, BrowseAllMedicinesActivity::class.java))
                }
                QuickChip(label = "By Category", icon = Icons.Default.Favorite) {
                    context.startActivity(Intent(context, BrowseCategoriesActivity::class.java))
                }
                QuickChip(label = "Upload Prescription", icon = Icons.Default.Add) {
                    context.startActivity(Intent(context, UploadPrescriptionActivity::class.java))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Shop by Category ─────────────────────────────────────────
            SectionHeader(title = "Shop by Category")
            Spacer(modifier = Modifier.height(12.dp))
            if (categories.isEmpty()) {
                Text(
                    text = "No categories available yet",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    categories.take(6).forEach { category ->
                        CategoryChip(
                            label = category.name,
                            icon = category.icon,
                            onClick = {
                                val intent = Intent(context, MedicineListActivity::class.java)
                                intent.putExtra("categoryId", category.categoryId)
                                intent.putExtra("categoryName", category.name)
                                intent.putExtra("categoryIcon", category.icon)
                                context.startActivity(intent)
                            }
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = Color(0xFF00501F),
                        modifier = Modifier.height(36.dp).clickable {
                            context.startActivity(Intent(context, BrowseCategoriesActivity::class.java))
                        }
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(text = "See All →", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(text = title, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
fun QuickChip(label: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        modifier = Modifier.height(38.dp).clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF006B2C), modifier = Modifier.size(16.dp))
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun CategoryChip(label: String, icon: String, onClick: () -> Unit = {}) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = Color(0xFFE8F5E9),
        modifier = Modifier.height(36.dp).clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = icon, fontSize = 14.sp)
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun DashboardBottomNav(activeTab: String = "Home") {
    val context = LocalContext.current
    val userVm: UserViewModel = viewModel()
    val user by userVm.user.collectAsState()

    LaunchedEffect(Unit) { userVm.loadCurrentUser() }

    Row(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(icon = Icons.Default.Home, label = "Home", isActive = activeTab == "Home") {
            if (activeTab != "Home") {
                val intent = Intent(context, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                context.startActivity(intent)
            }
        }
        BottomNavItem(icon = Icons.Default.Medication, label = "Medicines", isActive = activeTab == "Medicines") {
            if (activeTab != "Medicines") {
                context.startActivity(Intent(context, BrowseAllMedicinesActivity::class.java))
            }
        }
        BottomNavItem(icon = Icons.Default.Description, label = "Prescriptions", isActive = activeTab == "Prescriptions") {
            if (activeTab != "Prescriptions") {
                context.startActivity(Intent(context, MyPrescriptionsActivity::class.java))
            }
        }
        BottomNavItem(icon = Icons.Default.Person, label = "Profile", isActive = activeTab == "Profile", photoUrl = user?.profileImageUrl ?: "") {
            if (activeTab != "Profile") {
                context.startActivity(Intent(context, ProfileActivity::class.java))
            }
        }
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String, isActive: Boolean, photoUrl: String = "", onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        if (isActive) {
            Box(
                modifier = Modifier.size(36.dp).background(Color(0xFF00501F), RoundedCornerShape(50.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (photoUrl.isNotBlank()) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = label,
                        modifier = Modifier.size(26.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(imageVector = icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        } else {
            if (photoUrl.isNotBlank()) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = label,
                    modifier = Modifier.size(24.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(imageVector = icon, contentDescription = label, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
            }
        }
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (isActive) Color(0xFF00501F) else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardPreview() {
    DashboardScreen(firstName = "John")
}
