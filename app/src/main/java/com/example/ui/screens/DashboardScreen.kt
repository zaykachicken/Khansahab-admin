package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.DriverStatus
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.ui.components.OrderCard
import com.example.ui.components.StatCard
import com.example.ui.theme.PrimaryColor
import com.example.ui.theme.SurfaceBorderColor
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AdminViewModel

@Composable
fun DashboardScreen(
    viewModel: AdminViewModel,
    onNavigateToOrders: () -> Unit,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.allOrders.collectAsState()
    val drivers by viewModel.allDrivers.collectAsState()
    val analytics by viewModel.analytics.collectAsState()

    val pendingOrders = orders.filter { it.status == OrderStatus.PENDING }
    val activeOrders = orders.filter {
        it.status == OrderStatus.PENDING ||
                it.status == OrderStatus.CONFIRMED ||
                it.status == OrderStatus.PREPARING ||
                it.status == OrderStatus.READY ||
                it.status == OrderStatus.OUT_FOR_DELIVERY
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Hero Restaurant Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(18.dp),
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_zayka_banner),
                        contentDescription = "Zayka Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Color(0xCC000000), Color.Transparent))))
                    
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
                        Text(text = "Zayka Operations", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(text = "${pendingOrders.size} orders pending", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                }
            }
        }

        // Stats
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard(title = "Revenue", value = "₹${"%.0f".format(analytics.todayRevenue)}", icon = Icons.Default.CurrencyRupee, iconColor = MaterialTheme.colorScheme.primary, changeText = "+10%", isPositive = true, modifier = Modifier.weight(1f))
                    StatCard(title = "Pending", value = "${pendingOrders.size}", icon = Icons.Default.OutdoorGrill, iconColor = MaterialTheme.colorScheme.secondary, changeText = "Urgent", isPositive = pendingOrders.isEmpty(), modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard(title = "Fleet", value = "${drivers.count { it.status == DriverStatus.AVAILABLE }}", icon = Icons.Default.ElectricBike, iconColor = MaterialTheme.colorScheme.primary, changeText = "Ready", isPositive = true, modifier = Modifier.weight(1f))
                    StatCard(title = "Avg Time", value = "${analytics.avgDeliveryTimeMins}m", icon = Icons.Default.Timer, iconColor = MaterialTheme.colorScheme.secondary, changeText = "Target <30m", isPositive = true, modifier = Modifier.weight(1f))
                }
            }
        }

        // Orders Queue
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Incoming Orders", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = "View All →", color = PrimaryColor, fontWeight = FontWeight.Bold, modifier = Modifier.testTag("view_all_orders_link"))
            }
        }

        if (activeOrders.isEmpty()) {
            item {
                Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, SurfaceBorderColor), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                        Text(text = "All Caught Up!", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            items(activeOrders.take(5)) { order ->
                OrderCard(order = order, onStatusChange = { viewModel.updateOrderStatus(order.id, it) }, onAssignDriverClick = {}, onViewDetail = {})
            }
        }
    }
}
