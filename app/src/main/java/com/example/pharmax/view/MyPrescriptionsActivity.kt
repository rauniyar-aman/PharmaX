package com.example.pharmax.view

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pharmax.model.PrescriptionModel
import com.example.pharmax.ui.theme.PharmaXTheme
import com.example.pharmax.viewmodel.PrescriptionViewModel
import com.example.pharmax.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyPrescriptionsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { PharmaXTheme { MyPrescriptionsBody() } }
    }
}

@Composable
fun MyPrescriptionsBody() {
    val context = LocalContext.current
    val userVm: UserViewModel = viewModel()
    val prescriptionVm: PrescriptionViewModel = viewModel()

    val user by userVm.user.collectAsState()
    val prescriptions by prescriptionVm.prescriptions.collectAsState()
    val isLoading by prescriptionVm.loading.collectAsState()

    LaunchedEffect(Unit) { userVm.loadCurrentUser() }

    LaunchedEffect(user) {
        user?.uid?.let { uid -> if (uid.isNotBlank()) prescriptionVm.loadUserPrescriptions(uid) }
    }

    MyPrescriptionsScreen(
        prescriptions = prescriptions,
        isLoading = isLoading,
        onUploadNew = { context.startActivity(Intent(context, UploadPrescriptionActivity::class.java)) }
    )
}

@Composable
fun MyPrescriptionsScreen(
    prescriptions: List<PrescriptionModel> = emptyList(),
    isLoading: Boolean = false,
    onUploadNew: () -> Unit = {}
) {
    Scaffold(
        bottomBar = { DashboardBottomNav(activeTab = "Prescriptions") }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(innerPadding)
        ) {

            // ── Top bar ───────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "My Prescriptions", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006B2C), modifier = Modifier.weight(1f))
                ElevatedButton(
                    onClick = onUploadNew,
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.elevatedButtonColors(containerColor = Color(0xFF00501F), contentColor = Color.White),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Upload", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF006B2C))
                    }
                }
                prescriptions.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "📄", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "No prescriptions submitted yet", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(prescriptions) { prescription ->
                            MyPrescriptionCard(prescription)
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun MyPrescriptionCard(prescription: PrescriptionModel) {
    val statusColor = when (prescription.status) {
        "Approved" -> Color(0xFF006B2C)
        "Rejected" -> MaterialTheme.colorScheme.error
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(50.dp).background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📄", fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = prescription.medicineName.ifBlank { "General Prescription" },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = dateText, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (prescription.status == "Rejected" && prescription.adminComment.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Note: ${prescription.adminComment}", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                }
            }
            Box(
                modifier = Modifier.background(statusBg, RoundedCornerShape(50.dp)).padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(text = prescription.status, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = statusColor)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyPrescriptionsPreview() {
    MyPrescriptionsScreen(
        prescriptions = listOf(
            PrescriptionModel(medicineName = "Amoxicillin 500mg", status = "Pending"),
            PrescriptionModel(medicineName = "Tramadol 50mg", status = "Approved"),
            PrescriptionModel(medicineName = "Insulin", status = "Rejected", adminComment = "Image unclear, please re-upload")
        )
    )
}
