package com.example.pharmax.view

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pharmax.model.MedicineModel
import com.example.pharmax.ui.theme.PharmaXTheme
import com.example.pharmax.viewmodel.CategoryViewModel
import com.example.pharmax.viewmodel.ImageViewModel
import com.example.pharmax.viewmodel.MedicineViewModel

class AddMedicineActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val editMedicine = intent?.getStringExtra("medicineId")?.let { id ->
            MedicineModel(
                medicineId = id,
                name = intent.getStringExtra("name") ?: "",
                brand = intent.getStringExtra("brand") ?: "",
                category = intent.getStringExtra("category") ?: "",
                description = intent.getStringExtra("description") ?: "",
                price = intent.getDoubleExtra("price", 0.0),
                stock = intent.getIntExtra("stock", 0),
                dosage = intent.getStringExtra("dosage") ?: "",
                requiresPrescription = intent.getBooleanExtra("requiresPrescription", false),
                type = intent.getStringExtra("type") ?: "OTC",
                howToUse = intent.getStringExtra("howToUse") ?: "",
                imageUrl = intent.getStringExtra("imageUrl") ?: ""
            )
        }

        setContent {
            PharmaXTheme {
                AddMedicineBody(editMedicine = editMedicine)
            }
        }
    }
}

// Stateful — handles ViewModel + Firebase
@Composable
fun AddMedicineBody(editMedicine: MedicineModel? = null) {
    val context = LocalContext.current
    val vm: MedicineViewModel = viewModel()
    val categoryVm: CategoryViewModel = viewModel()
    val imageVm: ImageViewModel = viewModel()

    val message by vm.message.collectAsState()
    val isLoading by vm.loading.collectAsState()
    val categories by categoryVm.categories.collectAsState()
    val isUploading by imageVm.isUploading.collectAsState()
    val imageMessage by imageVm.message.collectAsState()
    var imageUrl by remember { mutableStateOf(editMedicine?.imageUrl ?: "") }

    LaunchedEffect(Unit) { categoryVm.loadCategories() }

    LaunchedEffect(message) {
        if (!message.isNullOrBlank()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            vm.clearMessage()
        }
    }

    LaunchedEffect(imageMessage) {
        if (!imageMessage.isNullOrBlank()) {
            Toast.makeText(context, imageMessage, Toast.LENGTH_LONG).show()
            imageVm.clearMessage()
        }
    }

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageVm.uploadImage(it, context) { url -> imageUrl = url }
        }
    }

    AddMedicineScreen(
        editMedicine = editMedicine,
        isLoading = isLoading,
        isUploadingImage = isUploading,
        imageUrl = imageUrl,
        categoryNames = categories.filter { it.isActive }.map { it.name },
        onPickImage = { imageLauncher.launch("image/*") },
        onSave = { model ->
            val finalModel = model.copy(imageUrl = imageUrl)
            if (editMedicine != null) {
                vm.updateMedicine(finalModel) { (context as? ComponentActivity)?.finish() }
            } else {
                vm.addMedicine(finalModel) { (context as? ComponentActivity)?.finish() }
            }
        },
        onCancel = { (context as? ComponentActivity)?.finish() }
    )
}

// Pure UI — safe to preview
@Composable
fun AddMedicineScreen(
    editMedicine: MedicineModel? = null,
    isLoading: Boolean = false,
    isUploadingImage: Boolean = false,
    imageUrl: String = "",
    categoryNames: List<String> = listOf("Pain Relief", "Antibiotics", "Vitamins", "Supplements", "Skincare", "Diabetes"),
    onPickImage: () -> Unit = {},
    onSave: (MedicineModel) -> Unit = {},
    onCancel: () -> Unit = {}
) {
    val isEditMode = editMedicine != null
    val categories = categoryNames

    var name by remember { mutableStateOf(editMedicine?.name ?: "") }
    var brand by remember { mutableStateOf(editMedicine?.brand ?: "") }
    var category by remember { mutableStateOf(editMedicine?.category ?: "") }
    var description by remember { mutableStateOf(editMedicine?.description ?: "") }
    var price by remember { mutableStateOf(if (editMedicine != null && editMedicine.price > 0) editMedicine.price.toString() else "") }
    var stock by remember { mutableStateOf(if (editMedicine != null) editMedicine.stock.toString() else "") }
    var dosage by remember { mutableStateOf(editMedicine?.dosage ?: "") }
    var requiresPrescription by remember { mutableStateOf(editMedicine?.requiresPrescription ?: false) }
    var medicineType by remember { mutableStateOf(editMedicine?.type ?: "OTC") }
    var howToUse by remember { mutableStateOf(editMedicine?.howToUse ?: "") }
    var ingredientInput by remember { mutableStateOf("") }
    val ingredients = remember { mutableStateListOf<String>().also { it.addAll(editMedicine?.ingredients ?: emptyList()) } }
    var categoryExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                modifier = Modifier.size(24.dp).clickable { onCancel() }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (isEditMode) "Edit Medicine" else "Add Medicine",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF006B2C),
                modifier = Modifier.weight(1f)
            )
            Box {
                Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(24.dp))
                Box(modifier = Modifier.size(8.dp).background(Color.Red, CircleShape).align(Alignment.TopEnd))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier.size(36.dp).background(Color(0xFF006B2C), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "A", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Image upload ──────────────────────────────────────────────
            Text(text = "MEDICINE IMAGE", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp))
                    .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                    .clickable { onPickImage() },
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Medicine Photo",
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🖼", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Upload Medicine Photo", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
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

            // ── Medicine Name ─────────────────────────────────────────────
            FormLabel(text = "Medicine Name*")
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter medicine name") },
                singleLine = true,
                shape = RoundedCornerShape(50.dp),
                colors = medicineFieldColors()
            )

            // ── Brand Name ────────────────────────────────────────────────
            FormLabel(text = "Brand Name*")
            OutlinedTextField(
                value = brand,
                onValueChange = { brand = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter manufacturer or brand") },
                singleLine = true,
                shape = RoundedCornerShape(50.dp),
                colors = medicineFieldColors()
            )

            // ── Category dropdown ─────────────────────────────────────────
            FormLabel(text = "Category")
            Box {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().clickable { categoryExpanded = true },
                    placeholder = { Text("Select Category") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.clickable { categoryExpanded = true }
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(50.dp),
                    colors = medicineFieldColors()
                )
                DropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = { category = cat; categoryExpanded = false }
                        )
                    }
                }
            }

            // ── Description ───────────────────────────────────────────────
            FormLabel(text = "Description")
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                placeholder = { Text("Provide a detailed clinical description of the medicine...") },
                shape = RoundedCornerShape(12.dp),
                colors = medicineFieldColors()
            )

            // ── Price + Stock ─────────────────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    FormLabel(text = "Price (NPR)*")
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("0.00") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(50.dp),
                        colors = medicineFieldColors()
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    FormLabel(text = "Stock Qty*")
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("0") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(50.dp),
                        colors = medicineFieldColors()
                    )
                }
            }

            // ── Dosage / Strength ─────────────────────────────────────────
            FormLabel(text = "Dosage / Strength")
            OutlinedTextField(
                value = dosage,
                onValueChange = { dosage = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. 500mg, 10ml") },
                singleLine = true,
                shape = RoundedCornerShape(50.dp),
                colors = medicineFieldColors()
            )

            // ── Requires Prescription toggle ──────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Requires Prescription", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "Rx-only medicines require doctor approval", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = requiresPrescription,
                    onCheckedChange = { requiresPrescription = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF0051D5))
                )
            }

            // ── Medicine Type: Rx / OTC ───────────────────────────────────
            FormLabel(text = "Medicine Type")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("OTC", "Rx").forEach { t ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                if (medicineType == t) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surface,
                                RoundedCornerShape(50.dp)
                            )
                            .border(
                                1.dp,
                                if (medicineType == t) Color(0xFF006B2C) else MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(50.dp)
                            )
                            .clickable { medicineType = t }
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .border(2.dp, if (medicineType == t) Color(0xFF006B2C) else MaterialTheme.colorScheme.outline, CircleShape)
                                .background(Color.Transparent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (medicineType == t) {
                                Box(modifier = Modifier.size(8.dp).background(Color(0xFF006B2C), CircleShape))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = t, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            // ── Ingredients tag input ─────────────────────────────────────
            FormLabel(text = "Ingredients")
            OutlinedTextField(
                value = ingredientInput,
                onValueChange = { ingredientInput = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Type ingredient name...") },
                singleLine = true,
                shape = RoundedCornerShape(50.dp),
                colors = medicineFieldColors(),
                trailingIcon = {
                    if (ingredientInput.isNotBlank()) {
                        Text(
                            text = "Add",
                            fontSize = 13.sp,
                            color = Color(0xFF006B2C),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(end = 12.dp).clickable {
                                ingredients.add(ingredientInput.trim())
                                ingredientInput = ""
                            }
                        )
                    }
                }
            )
            if (ingredients.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ingredients.toList().forEach { ingredient ->
                        Row(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(50.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = ingredient, fontSize = 12.sp, color = Color(0xFF006B2C), fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = Color(0xFF006B2C),
                                modifier = Modifier.size(14.dp).clickable { ingredients.remove(ingredient) }
                            )
                        }
                    }
                }
            }

            // ── How to Use ────────────────────────────────────────────────
            FormLabel(text = "How to Use")
            OutlinedTextField(
                value = howToUse,
                onValueChange = { howToUse = it },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                placeholder = { Text("Dosage instructions and usage guidelines...") },
                shape = RoundedCornerShape(12.dp),
                colors = medicineFieldColors()
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // ── Sticky bottom buttons ─────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Text(text = "Cancel", fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }

            ElevatedButton(
                onClick = {
                    onSave(
                        MedicineModel(
                            medicineId = editMedicine?.medicineId ?: "",
                            name = name,
                            brand = brand,
                            category = category,
                            description = description,
                            price = price.toDoubleOrNull() ?: 0.0,
                            stock = stock.toIntOrNull() ?: 0,
                            dosage = dosage,
                            requiresPrescription = requiresPrescription,
                            type = medicineType,
                            ingredients = ingredients.toList(),
                            howToUse = howToUse
                        )
                    )
                },
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = Color(0xFF00501F),
                    contentColor = Color.White
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(text = "Save Medicine", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
}

@Composable
private fun medicineFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    focusedIndicatorColor = Color(0xFF006B2C),
    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddMedicinePreview() {
    AddMedicineScreen()
}
