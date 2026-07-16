package com.example.pharmax.view

import android.os.Bundle
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import android.content.Intent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.pharmax.viewmodel.NotificationViewModel
import com.example.pharmax.viewmodel.UserViewModel

class AdminMedicineDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val medicine = MedicineModel(
            medicineId = intent.getStringExtra("medicineId") ?: "",
            name = intent.getStringExtra("name") ?: "",
            brand = intent.getStringExtra("brand") ?: "",
            category = intent.getStringExtra("category") ?: "",
            description = intent.getStringExtra("description") ?: "",
            price = intent.getDoubleExtra("price", 0.0),
            quantity = intent.getIntExtra("quantity", 0),
            dosage = intent.getStringExtra("dosage") ?: "",
            requiresPrescription = intent.getBooleanExtra("requiresPrescription", false),
            type = intent.getStringExtra("type") ?: "OTC",
            howToUse = intent.getStringExtra("howToUse") ?: "",
            imageUrl = intent.getStringExtra("imageUrl") ?: ""
        )

        setContent {
            PharmaXTheme {
                AdminMedicineDetailScreen(medicine = medicine, onBack = { finish() })
            }
        }
    }
}

@Composable
fun AdminMedicineDetailScreen(medicine: MedicineModel = MedicineModel(), onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val userVm: UserViewModel = viewModel()
    val notificationVm: NotificationViewModel = viewModel()
    val adminUser by userVm.user.collectAsState()
    val unreadCount by notificationVm.unreadCount.collectAsState()

    LaunchedEffect(Unit) {
        userVm.loadCurrentUser()
        notificationVm.loadNotifications(ADMIN_NOTIFICATION_BUCKET)
    }

    RequireAdminAccess(role = adminUser?.role)

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
                modifier = Modifier.size(24.dp).clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Medicine Details",
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
                    modifier = Modifier.size(24.dp).clickable {
                        val i = Intent(context, NotificationCenterActivity::class.java)
                        i.putExtra("recipientId", ADMIN_NOTIFICATION_BUCKET)
                        context.startActivity(i)
                    }
                )
                if (unreadCount > 0) {
                    Box(modifier = Modifier.size(8.dp).background(Color.Red, CircleShape).align(Alignment.TopEnd))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            AvatarCircle(
                photoUrl = adminUser?.profileImageUrl ?: "",
                fallbackText = adminUser?.fullName?.firstOrNull()?.uppercaseChar()?.toString() ?: "A",
                size = 36.dp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ── Hero card ─────────────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (medicine.imageUrl.isNotBlank()) {
                            AsyncImage(
                                model = medicine.imageUrl,
                                contentDescription = medicine.name,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(text = "💊", fontSize = 48.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(text = medicine.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = medicine.brand, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (medicine.category.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(text = medicine.category.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    if (medicine.type == "Rx") MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.tertiaryContainer,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = medicine.type,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (medicine.type == "Rx") MaterialTheme.colorScheme.error else Color(0xFF006B2C)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Price", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "NPR ${medicine.price.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0051D5))
                        }
                        Box(modifier = Modifier.width(1.dp).height(40.dp).background(MaterialTheme.colorScheme.outlineVariant))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Quantity", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "${medicine.quantity}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            // ── Info rows card ────────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    if (medicine.dosage.isNotBlank()) {
                        MedicineDetailRow(label = "Dosage / Strength", value = medicine.dosage)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 10.dp))
                    }

                    MedicineDetailRow(
                        label = "Requires Prescription",
                        value = if (medicine.requiresPrescription) "Yes" else "No",
                        valueColor = if (medicine.requiresPrescription) Color(0xFF0051D5) else Color(0xFF006B2C)
                    )

                    if (medicine.description.isNotBlank()) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 10.dp))
                        Text(text = "Description", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = medicine.description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 20.sp)
                    }

                    if (medicine.howToUse.isNotBlank()) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 10.dp))
                        Text(text = "How to Use", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = medicine.howToUse, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 20.sp)
                    }
                }
            }

            // ── Ingredients ───────────────────────────────────────────────
            if (medicine.ingredients.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Ingredients", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            medicine.ingredients.forEach { ingredient ->
                                Box(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(50.dp))
                                        .border(1.dp, Color(0xFF006B2C), RoundedCornerShape(50.dp))
                                        .padding(horizontal = 12.dp, vertical = 5.dp)
                                ) {
                                    Text(text = ingredient, fontSize = 12.sp, color = Color(0xFF006B2C), fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MedicineDetailRow(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminMedicineDetailPreview() {
    AdminMedicineDetailScreen(
        medicine = MedicineModel(
            name = "Paracetamol 500mg",
            brand = "Panadol Professional",
            category = "Pain Relief",
            description = "A common painkiller used to treat aches and pain.",
            price = 120.0,
            quantity = 42,
            dosage = "500mg",
            requiresPrescription = false,
            type = "OTC",
            ingredients = listOf("Paracetamol", "Starch", "Povidone"),
            howToUse = "Take 1-2 tablets every 4-6 hours."
        )
    )
}
