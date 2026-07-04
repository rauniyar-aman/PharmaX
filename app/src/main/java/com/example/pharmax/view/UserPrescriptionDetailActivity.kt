package com.example.pharmax.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import coil.compose.AsyncImage
import com.example.pharmax.ui.theme.PharmaXTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserPrescriptionDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val prescriptionId = intent.getStringExtra("prescriptionId") ?: ""
        val name = intent.getStringExtra("name") ?: ""
        val medicineName = intent.getStringExtra("medicineName") ?: ""
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""
        val notes = intent.getStringExtra("notes") ?: ""
        val status = intent.getStringExtra("status") ?: "Pending"
        val adminComment = intent.getStringExtra("adminComment") ?: ""
        val uploadedAt = intent.getLongExtra("uploadedAt", 0L)

        setContent {
            PharmaXTheme {
                UserPrescriptionDetailBody(
                    prescriptionId = prescriptionId,
                    initialName = name,
                    medicineName = medicineName,
                    initialImageUrl = imageUrl,
                    initialNotes = notes,
                    status = status,
                    adminComment = adminComment,
                    uploadedAt = uploadedAt,
                    onBack = { finish() }
                )
            }
        }
    }
}

@Composable
fun UserPrescriptionDetailBody(
    prescriptionId: String,
    initialName: String,
    medicineName: String,
    initialImageUrl: String,
    initialNotes: String,
    status: String,
    adminComment: String,
    uploadedAt: Long,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(initialName) }
    var imageUrl by remember { mutableStateOf(initialImageUrl) }
    var notes by remember { mutableStateOf(initialNotes) }

    val editLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == ComponentActivity.RESULT_OK) {
            result.data?.let { data ->
                name = data.getStringExtra("name") ?: name
                imageUrl = data.getStringExtra("imageUrl") ?: imageUrl
                notes = data.getStringExtra("notes") ?: notes
            }
        }
    }

    UserPrescriptionDetailScreen(
        name = name,
        medicineName = medicineName,
        imageUrl = imageUrl,
        notes = notes,
        status = status,
        adminComment = adminComment,
        uploadedAt = uploadedAt,
        onEdit = {
            val i = Intent(context, UploadPrescriptionActivity::class.java)
            i.putExtra("prescriptionId", prescriptionId)
            i.putExtra("medicineName", medicineName)
            i.putExtra("existingName", name)
            i.putExtra("existingImageUrl", imageUrl)
            i.putExtra("existingNotes", notes)
            editLauncher.launch(i)
        },
        onBack = onBack
    )
}

@Composable
fun UserPrescriptionDetailScreen(
    name: String = "",
    medicineName: String = "",
    imageUrl: String = "",
    notes: String = "",
    status: String = "Pending",
    adminComment: String = "",
    uploadedAt: Long = 0L,
    onEdit: () -> Unit = {},
    onBack: () -> Unit = {}
) {
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
            Text(text = "Prescription Details", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006B2C), modifier = Modifier.weight(1f))
            Box(modifier = Modifier.background(statusBg, RoundedCornerShape(50.dp)).padding(horizontal = 10.dp, vertical = 5.dp)) {
                Text(text = status, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = statusColor)
            }
        }

        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ── Info card ─────────────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRowText(label = "Name", value = name.ifBlank { "—" })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 8.dp))
                    DetailRowText(label = "Medicine", value = medicineName.ifBlank { "General Prescription" })
                    if (dateText.isNotBlank()) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 8.dp))
                        DetailRowText(label = "Submitted", value = dateText)
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
                Text(text = "Your Notes", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = notes, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (status != "Pending" && adminComment.isNotBlank()) {
                Text(text = "Pharmacist Comment", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = adminComment, fontSize = 14.sp, color = statusColor)
            }

            if (status == "Pending") {
                ElevatedButton(
                    onClick = onEdit,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.elevatedButtonColors(containerColor = Color(0xFF00501F), contentColor = Color.White)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Edit Prescription", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Text(
                    text = "This prescription has already been reviewed and can no longer be edited.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun DetailRowText(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UserPrescriptionDetailPreview() {
    UserPrescriptionDetailScreen(
        name = "Dr. Sharma's prescription",
        medicineName = "Amoxicillin 500mg",
        notes = "Doctor prescribed 3 times daily for 7 days",
        status = "Pending"
    )
}
