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
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pharmax.R
import com.example.pharmax.ui.theme.AppThemeState
import com.example.pharmax.ui.theme.PharmaXTheme
import com.example.pharmax.viewmodel.ImageViewModel
import com.example.pharmax.viewmodel.UserViewModel

class AdminProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { PharmaXTheme { AdminProfileBody(onBack = { finish() }) } }
    }
}

@Composable
fun AdminProfileBody(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val vm: UserViewModel = viewModel()
    val imageVm: ImageViewModel = viewModel()

    val user by vm.user.collectAsState()
    val isLoggedOut by vm.isLoggedOut.collectAsState()
    val isSaving by vm.loading.collectAsState()
    val isUploading by imageVm.isUploading.collectAsState()
    val message by vm.message.collectAsState()
    val imageMessage by imageVm.message.collectAsState()

    LaunchedEffect(Unit) { vm.loadCurrentUser() }

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

    LaunchedEffect(isLoggedOut) {
        if (isLoggedOut) {
            val intent = Intent(context, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageVm.uploadImage(it, context) { imageUrl ->
                vm.updateProfileImage(imageUrl)
            }
        }
    }

    AdminProfileScreen(
        fullName = user?.fullName ?: "",
        email = user?.email ?: "",
        phone = user?.phone ?: "",
        profileImageUrl = user?.profileImageUrl ?: "",
        isSaving = isSaving,
        isUploading = isUploading,
        onBack = onBack,
        onPickImage = { imageLauncher.launch("image/*") },
        onRemoveImage = { vm.removeProfileImage() },
        onSaveProfile = { name, phone -> vm.updateProfile(name, phone) },
        onChangePassword = { current, new, confirm -> vm.changePassword(current, new, confirm) },
        onDarkModeToggle = { isDark -> vm.updateDarkMode(isDark) },
        onLogout = { vm.logOut() }
    )
}

@Composable
fun AdminProfileScreen(
    fullName: String = "",
    email: String = "",
    phone: String = "",
    profileImageUrl: String = "",
    isSaving: Boolean = false,
    isUploading: Boolean = false,
    onBack: () -> Unit = {},
    onPickImage: () -> Unit = {},
    onRemoveImage: () -> Unit = {},
    onSaveProfile: (String, String) -> Unit = { _, _ -> },
    onChangePassword: (String, String, String) -> Unit = { _, _, _ -> },
    onDarkModeToggle: (Boolean) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showPhotoOptions by remember { mutableStateOf(false) }

    if (showEditDialog) {
        AdminEditProfileDialog(
            currentName = fullName,
            currentPhone = phone.removePrefix("+977"),
            isLoading = isSaving,
            onSave = { name, phone ->
                onSaveProfile(name, phone)
                showEditDialog = false
            },
            onDismiss = { showEditDialog = false }
        )
    }

    if (showPasswordDialog) {
        AdminChangePasswordDialog(
            isLoading = isSaving,
            onSave = { current, new, confirm ->
                onChangePassword(current, new, confirm)
                showPasswordDialog = false
            },
            onDismiss = { showPasswordDialog = false }
        )
    }

    if (showPhotoOptions) {
        AdminPhotoOptionsDialog(
            hasImage = profileImageUrl.isNotBlank(),
            onChangePhoto = {
                showPhotoOptions = false
                onPickImage()
            },
            onRemovePhoto = {
                showPhotoOptions = false
                onRemoveImage()
            },
            onDismiss = { showPhotoOptions = false }
        )
    }

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
            Text(text = "Admin Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006B2C))
        }

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

            // ── Avatar header ─────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(Color(0xFF006B2C), CircleShape)
                            .clickable { showPhotoOptions = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileImageUrl.isNotBlank()) {
                            AsyncImage(
                                model = profileImageUrl,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.size(90.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else if (fullName.isNotBlank()) {
                            Text(text = fullName.first().uppercaseChar().toString(), fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        } else {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                        }
                        if (isUploading) {
                            Box(
                                modifier = Modifier.size(90.dp).background(Color.Black.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(28.dp), color = Color.White, strokeWidth = 2.dp)
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color(0xFF0051D5), CircleShape)
                            .clickable { showPhotoOptions = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Change Photo", tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(text = fullName.ifBlank { "Admin" }, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = email, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Personal Information ──────────────────────────────────────
            AdminProfileSectionTitle("Personal Information")
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    AdminProfileInfoRow(label = "Full Name", value = fullName.ifBlank { "—" })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    AdminProfileInfoRow(label = "Email", value = email.ifBlank { "—" })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    AdminProfileInfoRow(label = "Phone", value = phone.ifBlank { "—" })
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Edit Profile button ───────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                AdminProfileActionRow(
                    icon = Icons.Default.Edit,
                    label = "Edit Profile",
                    iconTint = Color(0xFF0051D5),
                    onClick = { showEditDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Security ──────────────────────────────────────────────────
            AdminProfileSectionTitle("Security")
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                AdminProfileActionRow(
                    icon = Icons.Default.Person,
                    label = "Change Password",
                    iconTint = Color(0xFF006B2C),
                    onClick = { showPasswordDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Appearance ────────────────────────────────────────────────
            AdminProfileSectionTitle("Appearance")
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Dark Mode", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                    Switch(
                        checked = AppThemeState.isDarkMode.value,
                        onCheckedChange = { onDarkModeToggle(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF006B2C),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Logout ────────────────────────────────────────────────────
            ElevatedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.elevatedButtonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = Color.White),
                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 4.dp)
            ) {
                Text(text = "Logout", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ── Edit Profile Dialog ───────────────────────────────────────────────────────
@Composable
private fun AdminEditProfileDialog(
    currentName: String,
    currentPhone: String,
    isLoading: Boolean,
    onSave: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var phone by remember { mutableStateOf(currentPhone) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit Profile", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = Color(0xFF006B2C),
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) phone = it },
                    label = { Text("Phone") },
                    prefix = { Text("+977 ", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = Color(0xFF006B2C),
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name, phone) },
                enabled = !isLoading
            ) {
                Text("Save", color = Color(0xFF006B2C), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}

// ── Photo Options Dialog ──────────────────────────────────────────────────────
@Composable
private fun AdminPhotoOptionsDialog(
    hasImage: Boolean,
    onChangePhoto: () -> Unit,
    onRemovePhoto: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Profile Photo", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
        text = {
            Column {
                AdminPhotoOptionRow(label = "Change Photo", color = Color(0xFF006B2C), onClick = onChangePhoto)
                if (hasImage) {
                    AdminPhotoOptionRow(label = "Remove Photo", color = MaterialTheme.colorScheme.error, onClick = onRemovePhoto)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun AdminPhotoOptionRow(label: String, color: Color, onClick: () -> Unit) {
    Text(
        text = label,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        color = color,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    )
}

// ── Change Password Dialog ────────────────────────────────────────────────────
@Composable
private fun AdminChangePasswordDialog(
    isLoading: Boolean,
    onSave: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var currentVisible by remember { mutableStateOf(false) }
    var newVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Change Password", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AdminPasswordField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = "Current Password",
                    visible = currentVisible,
                    onToggleVisibility = { currentVisible = !currentVisible }
                )
                AdminPasswordField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = "New Password",
                    visible = newVisible,
                    onToggleVisibility = { newVisible = !newVisible }
                )
                AdminPasswordField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirm New Password",
                    visible = confirmVisible,
                    onToggleVisibility = { confirmVisible = !confirmVisible }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(currentPassword, newPassword, confirmPassword) },
                enabled = !isLoading
            ) {
                Text("Change", color = Color(0xFF006B2C), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun AdminPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    onToggleVisibility: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(
                        id = if (visible) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24
                    ),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color(0xFF006B2C),
            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

// ── Helper composables ────────────────────────────────────────────────────────
@Composable
private fun AdminProfileSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 20.dp, bottom = 6.dp)
    )
}

@Composable
private fun AdminProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun AdminProfileActionRow(icon: ImageVector, label: String, iconTint: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp).background(iconTint.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminProfilePreview() {
    AdminProfileScreen(
        fullName = "Aman Rauniyar",
        email = "admin@pharmax.com",
        phone = "+9779812345678"
    )
}
