package com.example.pharmax.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
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
import com.example.pharmax.model.PrescriptionModel
import com.example.pharmax.ui.theme.PharmaXTheme
import com.example.pharmax.viewmodel.ImageViewModel
import com.example.pharmax.viewmodel.PrescriptionViewModel
import com.example.pharmax.viewmodel.UserViewModel

class UploadPrescriptionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val medicineId = intent.getStringExtra("medicineId") ?: ""
        val medicineName = intent.getStringExtra("medicineName") ?: ""
        val prescriptionId = intent.getStringExtra("prescriptionId") ?: ""
        val existingName = intent.getStringExtra("existingName") ?: ""
        val existingImageUrl = intent.getStringExtra("existingImageUrl") ?: ""
        val existingNotes = intent.getStringExtra("existingNotes") ?: ""
        setContent {
            PharmaXTheme {
                UploadPrescriptionBody(
                    medicineId = medicineId,
                    medicineName = medicineName,
                    prescriptionId = prescriptionId,
                    initialName = existingName,
                    initialImageUrl = existingImageUrl,
                    initialNotes = existingNotes,
                    onBack = { finish() },
                    onCreated = { saved ->
                        val i = Intent(this, UserPrescriptionDetailActivity::class.java)
                        i.putExtra("prescriptionId", saved.prescriptionId)
                        i.putExtra("name", saved.name)
                        i.putExtra("medicineName", saved.medicineName)
                        i.putExtra("imageUrl", saved.imageUrl)
                        i.putExtra("notes", saved.notes)
                        i.putExtra("status", saved.status)
                        i.putExtra("adminComment", saved.adminComment)
                        i.putExtra("uploadedAt", saved.uploadedAt)
                        startActivity(i)
                        finish()
                    },
                    onUpdated = { updatedName, updatedImageUrl, updatedNotes ->
                        val result = Intent()
                        result.putExtra("name", updatedName)
                        result.putExtra("imageUrl", updatedImageUrl)
                        result.putExtra("notes", updatedNotes)
                        setResult(RESULT_OK, result)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun UploadPrescriptionBody(
    medicineId: String = "",
    medicineName: String = "",
    prescriptionId: String = "",
    initialName: String = "",
    initialImageUrl: String = "",
    initialNotes: String = "",
    onBack: () -> Unit = {},
    onCreated: (PrescriptionModel) -> Unit = {},
    onUpdated: (String, String, String) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current
    val userVm: UserViewModel = viewModel()
    val imageVm: ImageViewModel = viewModel()
    val prescriptionVm: PrescriptionViewModel = viewModel()

    val user by userVm.user.collectAsState()
    val isUploading by imageVm.isUploading.collectAsState()
    val imageMessage by imageVm.message.collectAsState()
    val isSaving by prescriptionVm.loading.collectAsState()
    val message by prescriptionVm.message.collectAsState()

    var imageUrl by remember { mutableStateOf(initialImageUrl) }
    val isEditMode = prescriptionId.isNotBlank()

    LaunchedEffect(Unit) { userVm.loadCurrentUser() }

    LaunchedEffect(imageMessage) {
        if (!imageMessage.isNullOrBlank()) {
            Toast.makeText(context, imageMessage, Toast.LENGTH_LONG).show()
            imageVm.clearMessage()
        }
    }

    LaunchedEffect(message) {
        if (!message.isNullOrBlank()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            prescriptionVm.clearMessage()
        }
    }

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { imageVm.uploadImage(it, context) { url -> imageUrl = url } }
    }

    UploadPrescriptionScreen(
        medicineName = medicineName,
        imageUrl = imageUrl,
        initialName = initialName.ifBlank { medicineName },
        initialNotes = initialNotes,
        isEditMode = isEditMode,
        isUploadingImage = isUploading,
        isSaving = isSaving,
        onPickImage = { imageLauncher.launch("image/*") },
        onSubmit = { name, notes ->
            if (isEditMode) {
                prescriptionVm.updatePrescription(prescriptionId, name, imageUrl, notes) { onUpdated(name, imageUrl, notes) }
            } else {
                val model = PrescriptionModel(
                    userId = user?.uid ?: "",
                    userName = user?.fullName ?: "",
                    userPhone = user?.phone ?: "",
                    medicineId = medicineId,
                    medicineName = medicineName,
                    name = name,
                    imageUrl = imageUrl,
                    notes = notes
                )
                prescriptionVm.addPrescription(model) { saved -> onCreated(saved) }
            }
        },
        onBack = onBack
    )
}

@Composable
fun UploadPrescriptionScreen(
    medicineName: String = "",
    imageUrl: String = "",
    initialName: String = "",
    initialNotes: String = "",
    isEditMode: Boolean = false,
    isUploadingImage: Boolean = false,
    isSaving: Boolean = false,
    onPickImage: () -> Unit = {},
    onSubmit: (String, String) -> Unit = { _, _ -> },
    onBack: () -> Unit = {}
) {
    var name by remember { mutableStateOf(initialName) }
    var notes by remember { mutableStateOf(initialNotes) }

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
            Text(text = if (isEditMode) "Edit Prescription" else "Upload Prescription", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006B2C))
        }

        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            if (medicineName.isNotBlank()) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💊", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "For medicine", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = medicineName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            Text(text = "Prescription Name*", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Dr. Sharma's prescription") },
                singleLine = true,
                shape = RoundedCornerShape(50.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color(0xFF006B2C),
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                )
            )
            Text(
                text = "Give it a name you'll recognize later when picking a prescription for an order.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(text = "PRESCRIPTION IMAGE", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp))
                    .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                    .clickable { onPickImage() },
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Prescription Image",
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📄", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Upload Prescription Photo", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = "Tap to choose from gallery", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                if (isUploadingImage) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                    }
                }
            }

            Text(text = "Notes (optional)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                placeholder = { Text("Any extra details for the pharmacist...") },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color(0xFF006B2C),
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            ElevatedButton(
                onClick = { onSubmit(name, notes) },
                enabled = name.isNotBlank() && imageUrl.isNotBlank() && !isSaving && !isUploadingImage,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.elevatedButtonColors(containerColor = Color(0xFF00501F), contentColor = Color.White),
                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 6.dp)
            ) {
                if (isSaving) CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                else Text(text = if (isEditMode) "Save Changes" else "Submit Prescription", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UploadPrescriptionPreview() {
    UploadPrescriptionScreen(medicineName = "Amoxicillin 500mg")
}
