package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.ui.components.AssignDriverDialog
import com.example.ui.components.OrderCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.ZaykaAmber
import com.example.ui.theme.ZaykaBlue
import com.example.ui.theme.ZaykaCharcoalBg
import com.example.ui.theme.ZaykaCrimson
import com.example.ui.theme.ZaykaEmerald
import com.example.ui.theme.ZaykaOrange
import com.example.ui.theme.ZaykaSurface
import com.example.ui.theme.ZaykaSurfaceBorder
import com.example.ui.theme.ZaykaSurfaceElevated
import com.example.ui.viewmodel.AdminViewModel

@Composable
fun OrdersQueueScreen(
    viewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.filteredOrders.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val drivers by viewModel.allDrivers.collectAsState()
    val selectedStatus by viewModel.selectedStatusTab.collectAsState()
    val searchQuery by viewModel.orderSearchQuery.collectAsState()

    var selectedOrderForDriver by remember { mutableStateOf<Order?>(null) }

    val statusTabs = listOf(
        null to "All (${allOrders.size})",
        OrderStatus.PENDING to "Pending (${allOrders.count { it.status == OrderStatus.PENDING }})",
        OrderStatus.CONFIRMED to "Confirmed (${allOrders.count { it.status == OrderStatus.CONFIRMED }})",
        OrderStatus.PREPARING to "Kitchen (${allOrders.count { it.status == OrderStatus.PREPARING }})",
        OrderStatus.READY to "Ready (${allOrders.count { it.status == OrderStatus.READY }})",
        OrderStatus.OUT_FOR_DELIVERY to "Dispatched (${allOrders.count { it.status == OrderStatus.OUT_FOR_DELIVERY }})",
        OrderStatus.DELIVERED to "Delivered (${allOrders.count { it.status == OrderStatus.DELIVERED }})",
        OrderStatus.CANCELLED to "Cancelled (${allOrders.count { it.status == OrderStatus.CANCELLED }})"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ZaykaCharcoalBg)
    ) {
        // Search & Filter Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ZaykaSurface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setOrderSearchQuery(it) },
                placeholder = { Text("Search by Order #, Name, Phone or Dish...", color = TextTertiary, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = ZaykaOrange, modifier = Modifier.size(20.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setOrderSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = ZaykaOrange,
                    unfocusedBorderColor = ZaykaSurfaceBorder,
                    focusedContainerColor = ZaykaSurfaceElevated,
                    unfocusedContainerColor = ZaykaSurfaceElevated
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("order_search_field")
            )

            // Horizontal Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(statusTabs) { (status, label) ->
                    val isSelected = selectedStatus == status
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setStatusFilter(status) },
                        label = {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ZaykaOrange,
                            selectedLabelColor = Color.White,
                            containerColor = ZaykaSurfaceElevated,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) ZaykaOrange else ZaykaSurfaceBorder
                        ),
                        modifier = Modifier.testTag("status_filter_${status?.name ?: "ALL"}")
                    )
                }
            }
        }

        // Orders List
        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = ZaykaSurface,
                    border = BorderStroke(1.dp, ZaykaSurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "🔍 No Orders Found",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "No orders match the current filter or search criteria.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { order ->
                    OrderCard(
                        order = order,
                        onStatusChange = { newStatus ->
                            viewModel.updateOrderStatus(order.id, newStatus)
                        },
                        onAssignDriverClick = {
                            selectedOrderForDriver = order
                        },
                        onViewDetail = { }
                    )
                }
            }
        }
    }

    // Driver Assignment Dialog
    selectedOrderForDriver?.let { order ->
        AssignDriverDialog(
            order = order,
            drivers = drivers,
            onDismiss = { selectedOrderForDriver = null },
            onDriverSelected = { driver ->
                viewModel.assignDriver(order.id, driver.id, driver.name)
                selectedOrderForDriver = null
            }
        )
    }
}
