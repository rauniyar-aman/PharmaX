package com.example.pharmax.view

import android.content.Intent
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pharmax.model.MedicineModel
import com.example.pharmax.ui.theme.PharmaXTheme

class UserMedicineDetailActivity : ComponentActivity() {
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
                UserMedicineDetailBody(
                    medicine = medicine,
                    onBack = { finish() },
                    onUploadPrescription = {
                        val intent = Intent(this, UploadPrescriptionActivity::class.java)
                        intent.putExtra("medicineId", medicine.medicineId)
                        intent.putExtra("medicineName", medicine.name)
                        startActivity(intent)
                    },
                    onBuyNow = {
                        val intent = Intent(this, BuyMedicineActivity::class.java)
                        intent.putExtra("medicineId", medicine.medicineId)
                        intent.putExtra("medicineName", medicine.name)
                        intent.putExtra("price", medicine.price)
                        intent.putExtra("requiresPrescription", medicine.requiresPrescription)
                        intent.putExtra("medicineType", medicine.type)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun UserMedicineDetailBody(
    medicine: MedicineModel = MedicineModel(),
    onBack: () -> Unit = {},
    onUploadPrescription: () -> Unit = {},
    onBuyNow: () -> Unit = {}
) {
    UserMedicineDetailScreen(medicine = medicine, onBack = onBack, onUploadPrescription = onUploadPrescription, onBuyNow = onBuyNow)
}

@Composable
fun UserMedicineDetailScreen(
    medicine: MedicineModel = MedicineModel(),
    onBack: () -> Unit = {},
    onUploadPrescription: () -> Unit = {},
    onBuyNow: () -> Unit = {}
) {
    Scaffold(
        bottomBar = { DashboardBottomNav(activeTab = "Medicines") }
    ) { innerPadding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
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
                Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(24.dp))
                Box(modifier = Modifier.size(8.dp).background(Color.Red, CircleShape).align(Alignment.TopEnd))
            }
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
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (medicine.imageUrl.isNotBlank()) {
                            AsyncImage(
                                model = medicine.imageUrl,
                                contentDescription = medicine.name,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(text = "💊", fontSize = 48.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(text = medicine.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = medicine.brand, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (medicine.category.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFE3EFFF), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(text = medicine.category.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    if (medicine.requiresPrescription) Color(0xFFFFEDED) else Color(0xFFE8F5E9),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (medicine.requiresPrescription) "Prescription Required" else "Over The Counter",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (medicine.requiresPrescription) MaterialTheme.colorScheme.error else Color(0xFF006B2C)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Price", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "NPR ${medicine.price.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0051D5))
                        }
                        Box(modifier = Modifier.width(1.dp).height(40.dp).background(MaterialTheme.colorScheme.outlineVariant))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Quantity", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "${medicine.quantity}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006B2C))
                        }
                    }
                }
            }

            // ── Info card ─────────────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    if (medicine.dosage.isNotBlank()) {
                        DetailRow(label = "Dosage / Strength", value = medicine.dosage)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 10.dp))
                    }

                    if (medicine.description.isNotBlank()) {
                        Text(text = "About this medicine", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = medicine.description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 22.sp)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 10.dp))
                    }

                    if (medicine.howToUse.isNotBlank()) {
                        Text(text = "How to use", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = medicine.howToUse, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 22.sp)
                    }
                }
            }

            // ── Prescription warning ──────────────────────────────────────
            if (medicine.requiresPrescription) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(text = "⚕️ Prescription Required", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF856404))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "This medicine requires a valid prescription from a licensed doctor. Please upload your prescription to proceed.",
                            fontSize = 13.sp,
                            color = Color(0xFF856404),
                            lineHeight = 20.sp
                        )
                    }
                }

                ElevatedButton(
                    onClick = onUploadPrescription,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.elevatedButtonColors(containerColor = Color(0xFF006B2C), contentColor = Color.White),
                    elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 4.dp)
                ) {
                    Text(text = "Upload Prescription", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            ElevatedButton(
                onClick = onBuyNow,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.elevatedButtonColors(containerColor = Color(0xFF5C2D91), contentColor = Color.White),
                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 6.dp)
            ) {
                Text(text = "Buy Now", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
    } // end Scaffold
}

@Composable
private fun DetailRow(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UserMedicineDetailPreview() {
    UserMedicineDetailScreen(
        medicine = MedicineModel(
            name = "Amoxicillin 500mg",
            brand = "Amoxil",
            category = "Antibiotics",
            description = "Amoxicillin is an antibiotic used to treat a number of bacterial infections.",
            price = 180.0,
            quantity = 32,
            dosage = "500mg — 3 times daily",
            requiresPrescription = true,
            type = "Rx",
            howToUse = "Take with or without food. Complete the full course even if you feel better."
        )
    )
}
