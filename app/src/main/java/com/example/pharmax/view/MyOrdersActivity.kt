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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.pharmax.model.OrderModel
import com.example.pharmax.ui.theme.PharmaXTheme
import com.example.pharmax.viewmodel.OrderViewModel
import com.example.pharmax.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyOrdersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { PharmaXTheme { MyOrdersBody(onBack = { finish() }) } }
    }
}

@Composable
fun MyOrdersBody(onBack: () -> Unit = {}) {
    val userVm: UserViewModel = viewModel()
    val orderVm: OrderViewModel = viewModel()

    val user by userVm.user.collectAsState()
    val orders by orderVm.orders.collectAsState()
    val isLoading by orderVm.loading.collectAsState()

    LaunchedEffect(Unit) { userVm.loadCurrentUser() }
    LaunchedEffect(user) {
        user?.uid?.let { uid -> if (uid.isNotBlank()) orderVm.loadUserOrders(uid) }
    }

    MyOrdersScreen(orders = orders, isLoading = isLoading, onBack = onBack)
}

@Composable
fun MyOrdersScreen(
    orders: List<OrderModel> = emptyList(),
    isLoading: Boolean = false,
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
            Text(text = "My Orders", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006B2C))
        }

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF006B2C))
                }
            }
            orders.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🛒", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "No orders yet", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(orders) { order -> MyOrderCard(order) }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun MyOrderCard(order: OrderModel) {
    val statusColor = when (order.paymentStatus) {
        "Paid" -> Color(0xFF006B2C)
        "Failed" -> MaterialTheme.colorScheme.error
        else -> Color(0xFFE65100)
    }
    val statusBg = when (order.paymentStatus) {
        "Paid" -> Color(0xFFE8F5E9)
        "Failed" -> Color(0xFFFFEDED)
        else -> Color(0xFFFFF3E0)
    }
    val dateText = remember(order.orderedAt) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(order.orderedAt))
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = order.medicineName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Box(modifier = Modifier.background(statusBg, RoundedCornerShape(50.dp)).padding(horizontal = 10.dp, vertical = 5.dp)) {
                    Text(text = order.paymentStatus, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = statusColor)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Qty: ${order.quantity} · NPR ${order.totalAmount.toInt()}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = dateText, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyOrdersPreview() {
    MyOrdersScreen(
        orders = listOf(
            OrderModel(medicineName = "Amoxicillin 500mg", quantity = 2, totalAmount = 360.0, paymentStatus = "Paid"),
            OrderModel(medicineName = "Paracetamol 500mg", quantity = 1, totalAmount = 120.0, paymentStatus = "Failed")
        )
    )
}
