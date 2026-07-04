package com.example.pharmax.view

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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pharmax.model.NotificationModel
import com.example.pharmax.ui.theme.PharmaXTheme
import com.example.pharmax.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationCenterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val recipientId = intent.getStringExtra("recipientId") ?: ""
        setContent {
            PharmaXTheme {
                NotificationCenterBody(recipientId = recipientId, onBack = { finish() })
            }
        }
    }
}

@Composable
fun NotificationCenterBody(recipientId: String, onBack: () -> Unit) {
    val vm: NotificationViewModel = viewModel()
    val notifications by vm.notifications.collectAsState()
    val isLoading by vm.loading.collectAsState()

    LaunchedEffect(recipientId) {
        if (recipientId.isNotBlank()) vm.loadNotifications(recipientId)
    }

    NotificationCenterScreen(
        notifications = notifications,
        isLoading = isLoading,
        onNotificationClick = { if (!it.isRead) vm.markAsRead(it.notificationId) },
        onMarkAllRead = { vm.markAllAsRead(recipientId) },
        onBack = onBack
    )
}

@Composable
fun NotificationCenterScreen(
    notifications: List<NotificationModel> = emptyList(),
    isLoading: Boolean = false,
    onNotificationClick: (NotificationModel) -> Unit = {},
    onMarkAllRead: () -> Unit = {},
    onBack: () -> Unit = {}
) {
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
            Text(text = "Notifications", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006B2C), modifier = Modifier.weight(1f))
            if (notifications.any { !it.isRead }) {
                Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = "Mark all as read",
                    tint = Color(0xFF006B2C),
                    modifier = Modifier.size(22.dp).clickable { onMarkAllRead() }
                )
            }
        }

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF006B2C))
                }
            }
            notifications.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🔔", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "No notifications yet", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(notifications) { notification ->
                        NotificationCard(notification, onClick = { onNotificationClick(notification) })
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(notification: NotificationModel, onClick: () -> Unit) {
    val dateText = remember(notification.createdAt) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(notification.createdAt))
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) MaterialTheme.colorScheme.surface else Color(0xFFE8F5E9)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            if (!notification.isRead) {
                Box(modifier = Modifier.padding(top = 6.dp).size(8.dp).background(Color(0xFF006B2C), CircleShape))
                Spacer(modifier = Modifier.width(10.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = notification.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = notification.message, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = dateText, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotificationCenterPreview() {
    NotificationCenterScreen(
        notifications = listOf(
            NotificationModel(title = "Prescription Approved", message = "Your prescription for Amoxicillin 500mg has been Approved.", isRead = false),
            NotificationModel(title = "Order placed", message = "Your order for 2x Paracetamol 500mg was placed successfully.", isRead = true)
        )
    )
}
