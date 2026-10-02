package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.data.model.DriverStatus
import com.example.ui.components.AddDriverDialog
import com.example.ui.components.DriverCard
import com.example.ui.components.RadarMapView
import com.example.ui.components.StatCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
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
fun DriverFleetScreen(
    viewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    val drivers by viewModel.allDrivers.collectAsState()
    val orders by viewModel.allOrders.collectAsState()
    var showAddDriverDialog by remember { mutableStateOf(false) }

    val availableCount = drivers.count { it.status == DriverStatus.AVAILABLE }
    val onDeliveryCount = drivers.count { it.status == DriverStatus.ON_DELIVERY }
    val offlineCount = drivers.count { it.status == DriverStatus.OFFLINE }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ZaykaCharcoalBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Radar Map Simulation
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Live Fleet Radar & Dispatch",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                RadarMapView(drivers = drivers, activeOrders = orders)
            }
        }

        // Fleet KPI Summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Available",
                    value = "$availableCount",
                    icon = Icons.Default.NearMe,
                    iconColor = ZaykaEmerald,
                    changeText = "Ready for pickup",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "On Delivery",
                    value = "$onDeliveryCount",
                    icon = Icons.Default.ElectricBike,
                    iconColor = ZaykaBlue,
                    changeText = "Active on route",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Total Fleet",
                    value = "${drivers.size}",
                    icon = Icons.Default.Person,
                    iconColor = ZaykaAmber,
                    changeText = "$offlineCount offline",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Add Driver Action Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Registered Delivery Riders (${drivers.size})",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = { showAddDriverDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ZaykaOrange),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_rider_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.size(4.dp))
                    Text("Add Rider", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Drivers List
        items(drivers) { driver ->
            DriverCard(
                driver = driver,
                onStatusChange = { newStatus ->
                    viewModel.updateDriverStatus(driver.id, newStatus)
                },
                onDeleteDriver = {
                    viewModel.deleteDriver(driver.id)
                }
            )
        }
    }

    if (showAddDriverDialog) {
        AddDriverDialog(
            onDismiss = { showAddDriverDialog = false },
            onSave = { newDriver ->
                viewModel.saveDriver(newDriver)
                showAddDriverDialog = false
            }
        )
    }
}
