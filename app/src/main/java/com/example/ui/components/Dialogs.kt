package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Coupon
import com.example.data.model.Driver
import com.example.data.model.DriverStatus
import com.example.data.model.MenuItem
import com.example.data.model.Order
import com.example.data.model.StaffRole
import com.example.data.model.StaffUser
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.VegGreen
import com.example.ui.theme.ZaykaAmber
import com.example.ui.theme.ZaykaBlue
import com.example.ui.theme.ZaykaCharcoalBg
import com.example.ui.theme.ZaykaCrimson
import com.example.ui.theme.ZaykaEmerald
import com.example.ui.theme.ZaykaOrange
import com.example.ui.theme.ZaykaSurface
import com.example.ui.theme.ZaykaSurfaceBorder
import com.example.ui.theme.ZaykaSurfaceElevated
import java.util.UUID

@Composable
fun AssignDriverDialog(
    order: Order,
    drivers: List<Driver>,
    onDismiss: () -> Unit,
    onDriverSelected: (Driver) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ZaykaSurface),
            border = BorderStroke(1.dp, ZaykaSurfaceBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Assign Rider",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Order #${order.id} • ${order.customerName}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                HorizontalDivider(color = ZaykaSurfaceBorder)

                val availableDrivers = drivers.filter { it.status == DriverStatus.AVAILABLE }

                if (availableDrivers.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ZaykaAmber.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "⚠️ All drivers are currently busy or offline. You can still assign any driver from the fleet.",
                            color = ZaykaAmber,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val displayList = if (availableDrivers.isNotEmpty()) availableDrivers else drivers
                    items(displayList) { driver ->
                        Surface(
                            onClick = { onDriverSelected(driver) },
                            shape = RoundedCornerShape(12.dp),
                            color = ZaykaSurfaceElevated,
                            border = BorderStroke(
                                1.dp,
                                if (driver.status == DriverStatus.AVAILABLE) ZaykaEmerald.copy(alpha = 0.4f) else ZaykaSurfaceBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("select_driver_${driver.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(ZaykaSurface),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.ElectricBike,
                                            contentDescription = null,
                                            tint = ZaykaOrange,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column {
                                        Text(driver.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text("${driver.vehicleNumber} • ⭐ ${driver.rating}", color = TextSecondary, fontSize = 11.sp)
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    DriverStatusBadge(status = driver.status)
                                    Button(
                                        onClick = { onDriverSelected(driver) },
                                        colors = ButtonDefaults.buttonColors(containerColor = ZaykaBlue),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Assign", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditMenuItemDialog(
    itemToEdit: MenuItem? = null,
    onDismiss: () -> Unit,
    onSave: (MenuItem) -> Unit
) {
    var name by remember { mutableStateOf(itemToEdit?.name ?: "") }
    var category by remember { mutableStateOf(itemToEdit?.category ?: "Biryani Specials") }
    var description by remember { mutableStateOf(itemToEdit?.description ?: "") }
    var priceText by remember { mutableStateOf(itemToEdit?.price?.toString() ?: "299") }
    var discountPriceText by remember { mutableStateOf(itemToEdit?.discountedPrice?.toString() ?: "") }
    var prepTimeText by remember { mutableStateOf(itemToEdit?.preparationTimeMinutes?.toString() ?: "20") }
    var isVeg by remember { mutableStateOf(itemToEdit?.isVeg ?: false) }
    var isAvailable by remember { mutableStateOf(itemToEdit?.isAvailable ?: true) }

    val categories = listOf(
        "Biryani Specials",
        "Tandoori & Charcoal Grills",
        "Rich Curries & Gravies",
        "Starters & Wings",
        "Family Combos & Platters",
        "Breads & Rice",
        "Beverages & Desserts"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ZaykaSurface),
            border = BorderStroke(1.dp, ZaykaSurfaceBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (itemToEdit == null) "Add New Dish" else "Edit Dish",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Dish Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ZaykaOrange,
                        unfocusedBorderColor = ZaykaSurfaceBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dish_name_input")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Ingredients") },
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ZaykaOrange,
                        unfocusedBorderColor = ZaykaSurfaceBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Price (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ZaykaOrange,
                            unfocusedBorderColor = ZaykaSurfaceBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = discountPriceText,
                        onValueChange = { discountPriceText = it },
                        label = { Text("Discounted (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ZaykaOrange,
                            unfocusedBorderColor = ZaykaSurfaceBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Switch(
                            checked = isVeg,
                            onCheckedChange = { isVeg = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = VegGreen,
                                uncheckedTrackColor = ZaykaCharcoalBg
                            )
                        )
                        Text(
                            text = if (isVeg) "Pure Veg" else "Non-Veg",
                            color = if (isVeg) VegGreen else ZaykaCrimson,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Switch(
                            checked = isAvailable,
                            onCheckedChange = { isAvailable = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = ZaykaEmerald
                            )
                        )
                        Text(
                            text = if (isAvailable) "In Stock" else "86'd (Sold Out)",
                            color = if (isAvailable) ZaykaEmerald else ZaykaCrimson,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        border = BorderStroke(1.dp, ZaykaSurfaceBorder)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val price = priceText.toDoubleOrNull() ?: 299.0
                            val disc = discountPriceText.toDoubleOrNull()
                            val prep = prepTimeText.toIntOrNull() ?: 20
                            val updatedItem = (itemToEdit ?: MenuItem(
                                id = "M-${UUID.randomUUID().toString().take(5).uppercase()}"
                            )).copy(
                                name = name.ifBlank { "Zayka Specialty Dish" },
                                category = category,
                                description = description,
                                price = price,
                                discountedPrice = disc,
                                preparationTimeMinutes = prep,
                                isVeg = isVeg,
                                isAvailable = isAvailable
                            )
                            onSave(updatedItem)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ZaykaOrange),
                        modifier = Modifier.testTag("save_dish_btn")
                    ) {
                        Text("Save Dish")
                    }
                }
            }
        }
    }
}

@Composable
fun AddDriverDialog(
    onDismiss: () -> Unit,
    onSave: (Driver) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+91 98") }
    var vehicleNumber by remember { mutableStateOf("DL ") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ZaykaSurface),
            border = BorderStroke(1.dp, ZaykaSurfaceBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Add Delivery Rider",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Rider Full Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ZaykaOrange,
                        unfocusedBorderColor = ZaykaSurfaceBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("rider_name_input")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Contact Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ZaykaOrange,
                        unfocusedBorderColor = ZaykaSurfaceBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = vehicleNumber,
                    onValueChange = { vehicleNumber = it },
                    label = { Text("Vehicle Registration No.") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ZaykaOrange,
                        unfocusedBorderColor = ZaykaSurfaceBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        border = BorderStroke(1.dp, ZaykaSurfaceBorder)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val newDriver = Driver(
                                id = "DRV-${UUID.randomUUID().toString().take(4).uppercase()}",
                                name = name.ifBlank { "Fleet Rider" },
                                phone = phone,
                                vehicleNumber = vehicleNumber.ifBlank { "DL 4S 0000" },
                                status = DriverStatus.AVAILABLE
                            )
                            onSave(newDriver)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ZaykaOrange),
                        modifier = Modifier.testTag("save_rider_btn")
                    ) {
                        Text("Add Rider")
                    }
                }
            }
        }
    }
}

@Composable
fun StaffRoleDialog(
    currentUser: StaffUser?,
    onDismiss: () -> Unit,
    onRoleSelected: (StaffRole) -> Unit,
    onGoogleSignIn: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ZaykaSurface),
            border = BorderStroke(1.dp, ZaykaSurfaceBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Staff & Role Access",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Active: ${currentUser?.name ?: "Zayka Staff"}",
                            color = ZaykaOrange,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = TextSecondary)
                    }
                }

                HorizontalDivider(color = ZaykaSurfaceBorder)

                Text(
                    text = "Switch Active Workspace Role:",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                StaffRole.values().forEach { role ->
                    val isSelected = currentUser?.role == role
                    Surface(
                        onClick = {
                            onRoleSelected(role)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) ZaykaOrange.copy(alpha = 0.15f) else ZaykaSurfaceElevated,
                        border = BorderStroke(1.dp, if (isSelected) ZaykaOrange else ZaykaSurfaceBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("role_option_${role.name}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = when (role) {
                                        StaffRole.OWNER -> "👑 Owner / General Manager"
                                        StaffRole.KITCHEN_MANAGER -> "👨‍🍳 Kitchen Head / Chef"
                                        StaffRole.DISPATCHER -> "🛵 Delivery & Dispatch Desk"
                                    },
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = when (role) {
                                        StaffRole.OWNER -> "Full access to analytics, menu, settings & fleet"
                                        StaffRole.KITCHEN_MANAGER -> "Live orders cooking queue & stock management"
                                        StaffRole.DISPATCHER -> "Rider assignment, radar tracking & customer ETA"
                                    },
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = ZaykaOrange)
                            }
                        }
                    }
                }

                HorizontalDivider(color = ZaykaSurfaceBorder)

                Button(
                    onClick = {
                        onGoogleSignIn()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZaykaSurfaceElevated),
                    border = BorderStroke(1.dp, ZaykaSurfaceBorder),
                    modifier = Modifier.fillMaxWidth().testTag("google_signin_btn")
                ) {
                    Text("🔑 Sign in with Google Account", color = TextPrimary, fontSize = 13.sp)
                }
            }
        }
    }
}
