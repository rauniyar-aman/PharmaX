package com.example.pharmax.view

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.pharmax.ui.theme.PharmaXTheme
import com.example.pharmax.viewmodel.PrescriptionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminPrescriptionDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val prescriptionId = intent.getStringExtra("prescriptionId") ?: ""
        val userId = intent.getStringExtra("userId") ?: ""
        val userName = intent.getStringExtra("userName") ?: ""
        val userPhone = intent.getStringExtra("userPhone") ?: ""
        val name = intent.getStringExtra("name") ?: ""
        val medicineName = intent.getStringExtra("medicineName") ?: ""
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""
        val notes = intent.getStringExtra("notes") ?: ""
        val status = intent.getStringExtra("status") ?: "Pending"
        val adminComment = intent.getStringExtra("adminComment") ?: ""
        val uploadedAt = intent.getLongExtra("uploadedAt", 0L)

        setContent {
            PharmaXTheme {
                AdminPrescriptionDetailBody(
                    prescriptionId = prescriptionId,
                    userId = userId,
                    userName = userName,
                    userPhone = userPhone,
                    name = name,
                    medicineName = medicineName,
                    imageUrl = imageUrl,
                    notes = notes,
                    initialStatus = status,
                    initialComment = adminComment,
                    uploadedAt = uploadedAt,
                    onBack = { finish() }
                )
            }
        }
    }
}

@Composable
fun AdminPrescriptionDetailBody(
    prescriptionId: String,
    userId: String,
    userName: String,
    userPhone: String,
    name: String,
    medicineName: String,
    imageUrl: String,
    notes: String,
    initialStatus: String,
    initialComment: String,
    uploadedAt: Long,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val vm: PrescriptionViewModel = viewModel()

    val isLoading by vm.loading.collectAsState()
    val message by vm.message.collectAsState()

    LaunchedEffect(message) {
        if (!message.isNullOrBlank()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            vm.clearMessage()
        }
    }

    AdminPrescriptionDetailScreen(
        userName = userName,
        userPhone = userPhone,
        name = name,
        medicineName = medicineName,
        imageUrl = imageUrl,
        notes = notes,
        status = initialStatus,
        adminComment = initialComment,
        uploadedAt = uploadedAt,
        isLoading = isLoading,
        onApprove = { comment -> vm.updateStatus(prescriptionId, "Approved", comment, userId, medicineName) { onBack() } },
        onReject = { comment -> vm.updateStatus(prescriptionId, "Rejected", comment, userId, medicineName) { onBack() } },
        onBack = onBack
    )
}

@Composable
fun AdminPrescriptionDetailScreen(
    userName: String = "",
    userPhone: String = "",
    name: String = "",
    medicineName: String = "",
    imageUrl: String = "",
    notes: String = "",
    status: String = "Pending",
    adminComment: String = "",
    uploadedAt: Long = 0L,
    isLoading: Boolean = false,
    onApprove: (String) -> Unit = {},
    onReject: (String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    var comment by remember { mutableStateOf(adminComment) }
    val dateText = remember(uploadedAt) {
        if (uploadedAt > 0) SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(uploadedAt)) else ""
    }
    val statusColor = when (status) {
        "Approved" -> Color(0xFF006B2C)
        "Rejected" -> Color(0xFFBA1A1A)
        else -> Color(0xFFE65100)
    }
    val statusBg = when (status) {
        "Approved" -> Color(0xFFE8F5E9)
        "Rejected" -> Color(0xFFFFEDED)
        else -> Color(0xFFFFF3E0)
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
            Text(text = "Review Prescription", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006B2C), modifier = Modifier.weight(1f))
            Box(modifier = Modifier.background(statusBg, RoundedCornerShape(50.dp)).padding(horizontal = 10.dp, vertical = 5.dp)) {
                Text(text = status, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = statusColor)
            }
        }

        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ── Patient info card ────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRowItem(label = "Patient Name", value = userName.ifBlank { "—" })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 8.dp))
                    DetailRowItem(label = "Phone", value = userPhone.ifBlank { "—" })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 8.dp))
                    DetailRowItem(label = "Prescription Name", value = name.ifBlank { "—" })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 8.dp))
                    DetailRowItem(label = "Medicine", value = medicineName.ifBlank { "General Prescription" })
                    if (dateText.isNotBlank()) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 8.dp))
                        DetailRowItem(label = "Submitted", value = dateText)
                    }
                }
            }

            // ── Prescription image ──────────────────────────────────────
            Text(text = "PRESCRIPTION IMAGE", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
            Box(
                modifier = Modifier.fillMaxWidth().height(280.dp).background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Prescription",
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(text = "No image", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (notes.isNotBlank()) {
                Text(text = "Patient Notes", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = notes, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // ── Admin comment ────────────────────────────────────────────
            Text(text = "Comment (visible to patient)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                modifier = Modifier.fillMaxWidth().height(90.dp),
                placeholder = { Text("Add a note, e.g. reason for rejection...") },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color(0xFF006B2C),
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                )
            )

            if (status == "Pending") {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    ElevatedButton(
                        onClick = { onReject(comment) },
                        enabled = !isLoading,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.elevatedButtonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = Color.White)
                    ) {
                        Text(text = "Reject", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    ElevatedButton(
                        onClick = { onApprove(comment) },
                        enabled = !isLoading,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.elevatedButtonColors(containerColor = Color(0xFF006B2C), contentColor = Color.White)
                    ) {
                        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        else Text(text = "Approve", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun DetailRowItem(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminPrescriptionDetailPreview() {
    AdminPrescriptionDetailScreen(
        userName = "Bikash Gurung",
        userPhone = "+9779812345678",
        medicineName = "Amoxicillin 500mg",
        notes = "Doctor prescribed 3 times daily for 7 days",
        status = "Pending"
    )
}
