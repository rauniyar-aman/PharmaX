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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import com.example.pharmax.model.OrderModel
import com.example.pharmax.model.PrescriptionModel
import com.example.pharmax.repo.KhaltiInitiateResult
import com.example.pharmax.repo.KhaltiRepo
import com.example.pharmax.ui.theme.PharmaXTheme
import com.example.pharmax.viewmodel.OrderViewModel
import com.example.pharmax.viewmodel.PrescriptionViewModel
import com.example.pharmax.viewmodel.UserViewModel
import com.khalti.checkout.Khalti
import com.khalti.checkout.data.Environment
import com.khalti.checkout.data.KhaltiPayConfig
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BuyMedicineActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val medicineId = intent.getStringExtra("medicineId") ?: ""
        val medicineName = intent.getStringExtra("medicineName") ?: ""
        val price = intent.getDoubleExtra("price", 0.0)
        val requiresPrescription = intent.getBooleanExtra("requiresPrescription", false)
        val medicineType = intent.getStringExtra("medicineType") ?: "OTC"

        setContent {
            PharmaXTheme {
                BuyMedicineBody(
                    medicineId = medicineId,
                    medicineName = medicineName,
                    price = price,
                    isRx = requiresPrescription || medicineType == "Rx",
                    medicineType = medicineType,
                    onBack = { finish() },
                    onUploadPrescription = {
                        val i = Intent(this, UploadPrescriptionActivity::class.java)
                        i.putExtra("medicineId", medicineId)
                        i.putExtra("medicineName", medicineName)
                        startActivity(i)
                    },
                    onOrderPlaced = {
                        val i = Intent(this, MyOrdersActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(i)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun BuyMedicineBody(
    medicineId: String,
    medicineName: String,
    price: Double,
    isRx: Boolean,
    medicineType: String,
    onBack: () -> Unit,
    onUploadPrescription: () -> Unit,
    onOrderPlaced: () -> Unit
) {
    val context = LocalContext.current
    val userVm: UserViewModel = viewModel()
    val prescriptionVm: PrescriptionViewModel = viewModel()
    val orderVm: OrderViewModel = viewModel()
    val khaltiRepo = remember { KhaltiRepo() }

    val user by userVm.user.collectAsState()
    val prescriptions by prescriptionVm.prescriptions.collectAsState()
    val isPlacingOrder by orderVm.loading.collectAsState()

    var quantity by remember { mutableStateOf(1) }
    var selectedPrescriptionId by remember { mutableStateOf("") }
    var isInitiatingPayment by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { userVm.loadCurrentUser() }
    LaunchedEffect(user) {
        user?.uid?.let { uid -> if (uid.isNotBlank()) prescriptionVm.loadUserPrescriptions(uid) }
    }

    val selectablePrescriptions = prescriptions.filter { it.status != "Rejected" }

    fun placeOrder(transactionId: String) {
        val order = OrderModel(
            userId = user?.uid ?: "",
            userName = user?.fullName ?: "",
            userPhone = user?.phone ?: "",
            medicineId = medicineId,
            medicineName = medicineName,
            medicineType = medicineType,
            quantity = quantity,
            unitPrice = price,
            totalAmount = price * quantity,
            prescriptionId = selectedPrescriptionId,
            paymentStatus = "Paid",
            transactionId = transactionId
        )
        orderVm.placeOrder(order) {
            Toast.makeText(context, "Order placed successfully!", Toast.LENGTH_LONG).show()
            onOrderPlaced()
        }
    }

    BuyMedicineScreen(
        medicineName = medicineName,
        price = price,
        isRx = isRx,
        quantity = quantity,
        onQuantityChange = { quantity = it },
        selectablePrescriptions = selectablePrescriptions,
        selectedPrescriptionId = selectedPrescriptionId,
        onSelectPrescription = { selectedPrescriptionId = it },
        isProcessing = isInitiatingPayment || isPlacingOrder,
        onUploadPrescription = onUploadPrescription,
        onPay = {
            if (isRx && selectedPrescriptionId.isBlank()) {
                Toast.makeText(context, "Please select a prescription for this medicine", Toast.LENGTH_SHORT).show()
                return@BuyMedicineScreen
            }
            isInitiatingPayment = true
            khaltiRepo.initiatePayment(
                amountNpr = price * quantity,
                purchaseOrderId = "PX-${System.currentTimeMillis()}",
                purchaseOrderName = medicineName,
                customerName = user?.fullName ?: "",
                customerPhone = user?.phone ?: ""
            ) { success: Boolean, initResult: KhaltiInitiateResult?, message: String ->
                isInitiatingPayment = false
                if (success && initResult != null) {
                    val khaltiPayConfig = KhaltiPayConfig(
                        publicKey = com.example.pharmax.BuildConfig.KHALTI_PUBLIC_KEY,
                        paymentUrl = initResult.paymentUrl,
                        pidx = initResult.pidx,
                        environment = Environment.TEST
                    )
                    Khalti.init(
                        context = context,
                        config = khaltiPayConfig,
                        onPaymentResult = { paymentResult, khalti ->
                            if (paymentResult.status.equals("Completed", ignoreCase = true)) {
                                placeOrder(paymentResult.payload?.transactionId ?: initResult.pidx)
                            } else {
                                Toast.makeText(context, "Payment ${paymentResult.status}", Toast.LENGTH_LONG).show()
                            }
                            khalti.close()
                        },
                        onMessage = { payload, khalti ->
                            Toast.makeText(context, payload.message, Toast.LENGTH_LONG).show()
                            khalti.close()
                        }
                    ).open()
                } else {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            }
        },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyMedicineScreen(
    medicineName: String = "",
    price: Double = 0.0,
    isRx: Boolean = false,
    quantity: Int = 1,
    onQuantityChange: (Int) -> Unit = {},
    selectablePrescriptions: List<PrescriptionModel> = emptyList(),
    selectedPrescriptionId: String = "",
    onSelectPrescription: (String) -> Unit = {},
    isProcessing: Boolean = false,
    onUploadPrescription: () -> Unit = {},
    onPay: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = if (selectedPrescriptionId.isBlank()) {
        "None"
    } else {
        val p = selectablePrescriptions.find { it.prescriptionId == selectedPrescriptionId }
        p?.let { prescriptionLabel(it) } ?: "None"
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).windowInsetsPadding(WindowInsets.systemBars)) {

        // ── Top bar ───────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF006B2C),
                modifier = Modifier.size(24.dp).clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "Buy Medicine", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006B2C))
        }

        Column(
            modifier = Modifier.weight(1f).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Medicine card ────────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = medicineName, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .background(if (isRx) Color(0xFFFFEDED) else Color(0xFFE8F5E9), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = if (isRx) "Rx" else "OTC", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isRx) MaterialTheme.colorScheme.error else Color(0xFF006B2C))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "NPR ${price.toInt()} / unit", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // ── Quantity ─────────────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Quantity", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                    QuantityStepperButton(symbol = "−", enabled = quantity > 1) { onQuantityChange(quantity - 1) }
                    Text(text = "$quantity", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface)
                    QuantityStepperButton(symbol = "+", enabled = true) { onQuantityChange(quantity + 1) }
                }
            }

            // ── Prescription selection ──────────────────────────────────
            Text(
                text = if (isRx) "SELECT PRESCRIPTION *" else "ATTACH PRESCRIPTION (OPTIONAL)",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )

            if (isRx && selectablePrescriptions.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD))
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(text = "You need to upload a prescription", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF856404))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "This medicine requires a prescription. You can order as soon as you upload one — it doesn't need to be approved yet, but our pharmacist will still need to verify it before your order ships.",
                            fontSize = 13.sp,
                            color = Color(0xFF856404)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        ElevatedButton(
                            onClick = onUploadPrescription,
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.elevatedButtonColors(containerColor = Color(0xFF00501F), contentColor = Color.White)
                        ) {
                            Text(text = "Upload Prescription", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = selectedLabel,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        singleLine = true,
                        shape = RoundedCornerShape(50.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedBorderColor = Color(0xFF006B2C)
                        )
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        if (!isRx) {
                            DropdownMenuItem(text = { Text("None") }, onClick = { onSelectPrescription(""); expanded = false })
                        }
                        selectablePrescriptions.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(prescriptionLabel(p)) },
                                onClick = { onSelectPrescription(p.prescriptionId); expanded = false }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── Total + Pay ──────────────────────────────────────────────
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Total", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "NPR ${(price * quantity).toInt()}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0051D5))
            }

            ElevatedButton(
                onClick = onPay,
                enabled = !isProcessing && !(isRx && selectablePrescriptions.isEmpty()),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.elevatedButtonColors(containerColor = Color(0xFF5C2D91), contentColor = Color.White),
                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 6.dp)
            ) {
                if (isProcessing) CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                else Text(text = "Pay with Khalti", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun QuantityStepperButton(symbol: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(if (enabled) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = symbol, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (enabled) Color(0xFF006B2C) else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun prescriptionLabel(p: PrescriptionModel): String {
    val dateText = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(p.uploadedAt))
    val label = p.name.ifBlank { p.medicineName.ifBlank { "General Prescription" } }
    val statusSuffix = if (p.status == "Approved") "" else " (${p.status} Approval)"
    return "$label — $dateText$statusSuffix"
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BuyMedicinePreview() {
    BuyMedicineScreen(medicineName = "Amoxicillin 500mg", price = 180.0, isRx = true)
}
