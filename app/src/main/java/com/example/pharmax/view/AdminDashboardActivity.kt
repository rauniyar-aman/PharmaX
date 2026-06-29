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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pharmax.model.MedicineModel
import com.example.pharmax.viewmodel.MedicineViewModel
import kotlinx.coroutines.launch

class AdminDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdminDashboardBody()
        }
    }
}

// Stateful — handles ViewModel + Firebase
@Composable
fun AdminDashboardBody() {
    val context = LocalContext.current
    val vm: MedicineViewModel = viewModel()

    val message by vm.message.collectAsState()
    val medicines by vm.medicines.collectAsState()
    val isLoading by vm.loading.collectAsState()

    LaunchedEffect(Unit) { vm.loadMedicines() }

    LaunchedEffect(message) {
        if (!message.isNullOrBlank()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            vm.clearMessage()
        }
    }

    val lowStockMedicines = medicines.filter { it.stock in 0..10 }

    AdminDashboardScreen(
        lowStockMedicines = lowStockMedicines,
        lowStockCount = lowStockMedicines.size,
        onAddMedicine = { context.startActivity(Intent(context, AddMedicineActivity::class.java)) },
        onAddCategory = { context.startActivity(Intent(context, AddCategoryActivity::class.java)) },
        onNavigateMedicines = { context.startActivity(Intent(context, AdminMedicineManagement::class.java)) },
        onNavigateCategories = { context.startActivity(Intent(context, AdminCategoryManagement::class.java)) }
    )
}

// Pure UI — safe to preview
@Composable
fun AdminDashboardScreen(
    lowStockMedicines: List<MedicineModel> = emptyList(),
    lowStockCount: Int = 0,
    onAddMedicine: () -> Unit = {},
    onAddCategory: () -> Unit = {},
    onNavigateMedicines: () -> Unit = {},
    onNavigateCategories: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AdminSideDrawer(
                onClose = { scope.launch { drawerState.close() } },
                onNavigateMedicines = { scope.launch { drawerState.close() }; onNavigateMedicines() },
                onNavigateCategories = { scope.launch { drawerState.close() }; onNavigateCategories() }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F9FF))
        ) {

            // ── Top bar ───────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color(0xFF0E1D2A),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { scope.launch { drawerState.open() } }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "PharmaX Admin",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF006B2C),
                    modifier = Modifier.weight(1f)
                )
                Box {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF0E1D2A), modifier = Modifier.size(24.dp))
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
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                // ── 2×2 Stat grid ─────────────────────────────────────────
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdminStatDashCard(
                        label = "Total Orders",
                        value = "128",
                        color = Color(0xFF006B2C),
                        bgColor = Color(0xFFE8F5E9),
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatDashCard(
                        label = "Revenue Today",
                        value = "NPR 45,200",
                        color = Color(0xFF0051D5),
                        bgColor = Color(0xFFE3EFFF),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdminStatDashCard(
                        label = "Pending Prescriptions",
                        value = "14",
                        color = Color(0xFFE65100),
                        bgColor = Color(0xFFFFF3E0),
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatDashCard(
                        label = "Low Stock Alerts",
                        value = "$lowStockCount",
                        color = Color(0xFFBA1A1A),
                        bgColor = Color(0xFFFFEDED),
                        modifier = Modifier.weight(1f)
                    )
                }

                // ── Recent Orders ─────────────────────────────────────────
                AdminSectionHeader(title = "Recent Orders")
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        listOf(
                            Triple("#ORD-1042", "Ramesh Sharma", "NPR 850") to Pair("Delivered", Color(0xFF006B2C)),
                            Triple("#ORD-1041", "Sita Rana", "NPR 320") to Pair("Pending", Color(0xFFE65100)),
                            Triple("#ORD-1040", "Anil KC", "NPR 1,200") to Pair("Processing", Color(0xFF0051D5)),
                            Triple("#ORD-1039", "Priya Thapa", "NPR 460") to Pair("Cancelled", Color(0xFFBA1A1A))
                        ).forEachIndexed { index, (order, status) ->
                            RecentOrderRow(
                                orderId = order.first,
                                customer = order.second,
                                amount = order.third,
                                status = status.first,
                                statusColor = status.second,
                                time = "${(index + 1) * 12} mins ago"
                            )
                            if (index < 3) HorizontalDivider(color = Color(0xFFF1F4F8))
                        }
                    }
                }

                // ── Prescriptions Awaiting Verification ───────────────────
                AdminSectionHeader(title = "Prescriptions Awaiting Verification")
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        listOf(
                            "Bikash Gurung" to "2 mins ago",
                            "Sunita Poudel" to "15 mins ago",
                            "Manoj Shrestha" to "1 hr ago"
                        ).forEachIndexed { index, (name, time) ->
                            PrescriptionRow(customerName = name, uploadTime = time)
                            if (index < 2) HorizontalDivider(color = Color(0xFFF1F4F8))
                        }
                    }
                }

                // ── Low Stock Medicines ───────────────────────────────────
                AdminSectionHeader(title = "Low Stock Medicines")
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        if (lowStockMedicines.isEmpty()) {
                            Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                                Text(text = "No low stock alerts", fontSize = 14.sp, color = Color(0xFF6F7A6E))
                            }
                        } else {
                            lowStockMedicines.take(5).forEachIndexed { index, medicine ->
                                LowStockRow(name = medicine.name, stock = medicine.stock)
                                if (index < lowStockMedicines.take(5).lastIndex) HorizontalDivider(color = Color(0xFFF1F4F8))
                            }
                        }
                    }
                }

                // ── Quick Actions ─────────────────────────────────────────
                AdminSectionHeader(title = "Quick Actions")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ElevatedButton(
                        onClick = onAddMedicine,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Color(0xFF00501F),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Add Medicine", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    ElevatedButton(
                        onClick = onAddCategory,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Color(0xFF0051D5),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Add Category", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ── Sidebar Drawer ────────────────────────────────────────────────────────────
@Composable
fun AdminSideDrawer(
    onClose: () -> Unit = {},
    onNavigateMedicines: () -> Unit = {},
    onNavigateCategories: () -> Unit = {}
) {
    val navItems = listOf(
        Triple("Dashboard", Icons.Default.Dashboard, true),
        Triple("Medicines", Icons.Default.Medication, false),
        Triple("Categories", Icons.Default.Category, false),
        Triple("Inventory", Icons.Default.Inventory, false),
        Triple("Prescriptions", Icons.Default.Description, false),
        Triple("Orders", Icons.Default.ReceiptLong, false),
        Triple("Customers", Icons.Default.Group, false),
        Triple("Delivery", Icons.Default.LocalShipping, false),
        Triple("Reports", Icons.Default.BarChart, false),
        Triple("Settings", Icons.Default.Settings, false)
    )

    Column(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
            .background(Color.White)
            .padding(top = 48.dp)
    ) {
        // Drawer header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(Color(0xFF006B2C), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "A", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Admin", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0E1D2A))
                Text(text = "admin@pharmax.com", fontSize = 12.sp, color = Color(0xFF6F7A6E))
            }
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color(0xFF6F7A6E),
                modifier = Modifier.size(20.dp).clickable { onClose() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color(0xFFF1F4F8))
        Spacer(modifier = Modifier.height(8.dp))

        navItems.forEach { (label, icon, isActive) ->
            DrawerNavItem(
                label = label,
                icon = icon,
                isActive = isActive,
                onClick = {
                    when (label) {
                        "Medicines" -> onNavigateMedicines()
                        "Categories" -> onNavigateCategories()
                        else -> onClose()
                    }
                }
            )
        }
    }
}

@Composable
fun DrawerNavItem(label: String, icon: ImageVector, isActive: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isActive) Color(0xFFE8F5E9) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isActive) Color(0xFF006B2C) else Color(0xFF6F7A6E),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isActive) Color(0xFF006B2C) else Color(0xFF0E1D2A)
        )
    }
}

// ── Helper composables ────────────────────────────────────────────────────────

@Composable
fun AdminStatDashCard(label: String, value: String, color: Color, bgColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = label, fontSize = 11.sp, color = color.copy(alpha = 0.8f), fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun AdminSectionHeader(title: String) {
    Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0E1D2A))
}

@Composable
fun RecentOrderRow(orderId: String, customer: String, amount: String, status: String, statusColor: Color, time: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = orderId, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0E1D2A))
            Text(text = customer, fontSize = 12.sp, color = Color(0xFF6F7A6E))
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = amount, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0E1D2A))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).background(statusColor, CircleShape))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = status, fontSize = 11.sp, color = statusColor, fontWeight = FontWeight.Medium)
            }
            Text(text = time, fontSize = 10.sp, color = Color(0xFF6F7A6E))
        }
    }
}

@Composable
fun PrescriptionRow(customerName: String, uploadTime: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp).background(Color(0xFFE3EFFF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = customerName.first().uppercase(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0051D5))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = customerName, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF0E1D2A))
            Text(text = "Uploaded $uploadTime", fontSize = 12.sp, color = Color(0xFF6F7A6E))
        }
        Box(
            modifier = Modifier
                .background(Color(0xFFE8F5E9), RoundedCornerShape(50.dp))
                .clickable { }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(text = "Review", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF006B2C))
        }
    }
}

@Composable
fun LowStockRow(name: String, stock: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "💊", fontSize = 18.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF0E1D2A), modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .background(Color(0xFFFFEDED), RoundedCornerShape(50.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(text = "Stock: $stock", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFBA1A1A))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminDashboardPreview() {
    val sampleLowStock = listOf(
        MedicineModel(name = "Amoxicillin Syrup", stock = 8),
        MedicineModel(name = "Metformin 500mg", stock = 3),
        MedicineModel(name = "Insulin Injection", stock = 0)
    )
    AdminDashboardScreen(lowStockMedicines = sampleLowStock, lowStockCount = 3)
}
