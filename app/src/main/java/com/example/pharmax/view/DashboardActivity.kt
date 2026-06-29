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
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pharmax.viewmodel.UserViewModel
import java.util.Calendar

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashboardBody()
        }
    }
}

@Composable
fun DashboardBody() {
    val context = LocalContext.current
    val vm: UserViewModel = viewModel()

    val message by vm.message.collectAsState()
    val user by vm.user.collectAsState()
    val isLoggedOut by vm.isLoggedOut.collectAsState()

    LaunchedEffect(Unit) { vm.loadCurrentUser() }

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

    DashboardScreen(firstName = user?.fullName?.split(" ")?.firstOrNull() ?: "User")
}

@Composable
fun DashboardScreen(firstName: String = "User") {
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
                .background(Color(0xFFF7F9FF))
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
                    Text(text = "Your health is our priority today.", fontSize = 13.sp, color = Color(0xFF3F493F))
                }
                BadgedBox(badge = {
                    Badge(containerColor = Color.Red) {
                        Text(text = "3", fontSize = 10.sp, color = Color.White)
                    }
                }) {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications", tint = Color(0xFF0E1D2A), modifier = Modifier.size(26.dp))
                }
            }

            // ── Search bar ───────────────────────────────────────────────
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                placeholder = { Text("Search medicines, brands...", color = Color(0xFF6F7A6E), fontSize = 14.sp) },
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
                    Toast.makeText(context, "Upload Prescription coming soon", Toast.LENGTH_SHORT).show()
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Shop by Category ─────────────────────────────────────────
            SectionHeader(title = "Shop by Category")
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CategoryChip(label = "Pain Relief",  bgColor = Color(0xFFFFE0E0), iconTint = Color(0xFFD32F2F))
                CategoryChip(label = "Vitamins",     bgColor = Color(0xFFFFF9C4), iconTint = Color(0xFFF9A825))
                CategoryChip(label = "Antibiotics",  bgColor = Color(0xFFE8F5E9), iconTint = Color(0xFF388E3C))
                CategoryChip(label = "Skincare",     bgColor = Color(0xFFFCE4EC), iconTint = Color(0xFFC2185B))
                CategoryChip(label = "Diabetes",     bgColor = Color(0xFFE3F2FD), iconTint = Color(0xFF1565C0))
                CategoryChip(label = "Heart Health", bgColor = Color(0xFFF3E5F5), iconTint = Color(0xFF7B1FA2))
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

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(text = title, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0E1D2A), modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
fun QuickChip(label: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier.height(38.dp).clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF006B2C), modifier = Modifier.size(16.dp))
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF0E1D2A))
        }
    }
}

@Composable
fun CategoryChip(label: String, bgColor: Color, iconTint: Color) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = bgColor,
        modifier = Modifier.height(36.dp).clickable { }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = iconTint, modifier = Modifier.size(14.dp))
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF0E1D2A))
        }
    }
}

@Composable
fun DashboardBottomNav(activeTab: String = "Home") {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.White).padding(vertical = 8.dp),
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
                context.startActivity(Intent(context, BrowseCategoriesActivity::class.java))
            }
        }
        BottomNavItem(icon = Icons.Default.Description, label = "Prescriptions", isActive = activeTab == "Prescriptions") {
            Toast.makeText(context, "Prescriptions coming soon", Toast.LENGTH_SHORT).show()
        }
        BottomNavItem(icon = Icons.Default.Person, label = "Profile", isActive = activeTab == "Profile") {
            Toast.makeText(context, "Profile coming soon", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String, isActive: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        if (isActive) {
            Box(
                modifier = Modifier.size(36.dp).background(Color(0xFF00501F), RoundedCornerShape(50.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        } else {
            Icon(imageVector = icon, contentDescription = label, tint = Color(0xFF6F7A6E), modifier = Modifier.size(24.dp))
        }
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (isActive) Color(0xFF00501F) else Color(0xFF6F7A6E),
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardPreview() {
    DashboardScreen(firstName = "John")
}
