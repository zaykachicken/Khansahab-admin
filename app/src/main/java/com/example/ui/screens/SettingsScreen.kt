package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RestaurantSettings
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.ZaykaAmber
import com.example.ui.theme.ZaykaCharcoalBg
import com.example.ui.theme.ZaykaCrimson
import com.example.ui.theme.ZaykaEmerald
import com.example.ui.theme.ZaykaOrange
import com.example.ui.theme.ZaykaSurface
import com.example.ui.theme.ZaykaSurfaceBorder
import com.example.ui.theme.ZaykaSurfaceElevated
import com.example.ui.viewmodel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
    viewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    val settingsState by viewModel.settings.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val lastSyncTime by viewModel.lastSyncTime.collectAsState()
    val context = LocalContext.current

    var restaurantName by remember(settingsState) { mutableStateOf(settingsState?.restaurantName ?: "ZaykaChicken Cafe & Restaurant") }
    var contactPhone by remember(settingsState) { mutableStateOf(settingsState?.contactPhone ?: "+91 98765 43210") }
    var storeAddress by remember(settingsState) { mutableStateOf(settingsState?.storeAddress ?: "Plot 42, Gourmet Boulevard, Zayka Square") }
    var deliveryRadius by remember(settingsState) { mutableStateOf(settingsState?.deliveryRadiusKm?.toString() ?: "10.0") }
    var minOrderValue by remember(settingsState) { mutableStateOf(settingsState?.minOrderValue?.toString() ?: "149") }
    var baseDeliveryFee by remember(settingsState) { mutableStateOf(settingsState?.baseDeliveryFee?.toString() ?: "40") }
    var packagingCharge by remember(settingsState) { mutableStateOf(settingsState?.packagingCharge?.toString() ?: "25") }
    var gstPercentage by remember(settingsState) { mutableStateOf(settingsState?.gstPercentage?.toString() ?: "5.0") }
    var prepBufferTime by remember(settingsState) { mutableStateOf(settingsState?.prepTimeBufferMinutes?.toString() ?: "20") }
    var autoAccept by remember(settingsState) { mutableStateOf(settingsState?.autoAcceptOrders ?: false) }
    var soundAlerts by remember(settingsState) { mutableStateOf(settingsState?.soundAlertsEnabled ?: true) }

    val formattedSyncTime = remember(lastSyncTime) {
        SimpleDateFormat("hh:mm:ss a, dd MMM", Locale.getDefault()).format(Date(lastSyncTime))
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ZaykaCharcoalBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Zayka User App & Firebase Cloud Live Sync Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ZaykaSurface),
                border = BorderStroke(1.dp, ZaykaEmerald.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(ZaykaEmerald.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = ZaykaEmerald,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Zayka User App Cloud Sync",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Active Project: zaykacaferesto (Firebase)",
                                    color = ZaykaEmerald,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = ZaykaEmerald.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(ZaykaEmerald)
                                )
                                Text("LIVE", color = ZaykaEmerald, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ZaykaSurfaceElevated,
                        border = BorderStroke(1.dp, ZaykaSurfaceBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("User App Client:", color = TextSecondary, fontSize = 11.sp)
                                Text("com.zayka.delivery", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Real-Time Listeners:", color = TextSecondary, fontSize = 11.sp)
                                Text("Orders, Menu, Riders, Store Status", color = ZaykaOrange, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Last Synced:", color = TextSecondary, fontSize = 11.sp)
                                Text(formattedSyncTime, color = TextTertiary, fontSize = 11.sp)
                            }
                        }
                    }

                    // Cloud Quick Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.syncMenuToCloud { count ->
                                    Toast.makeText(context, "Synced $count Menu items with User App!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, ZaykaOrange.copy(alpha = 0.5f)),
                            modifier = Modifier.weight(1f).testTag("sync_menu_btn")
                        ) {
                            Icon(Icons.Default.RestaurantMenu, contentDescription = null, tint = ZaykaOrange, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sync Menu", color = ZaykaOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.syncCouponsToCloud { count ->
                                    Toast.makeText(context, "Synced $count Promo coupons with User App!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, ZaykaAmber.copy(alpha = 0.5f)),
                            modifier = Modifier.weight(1f).testTag("sync_coupons_btn")
                        ) {
                            Icon(Icons.Default.LocalOffer, contentDescription = null, tint = ZaykaAmber, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sync Deals", color = ZaykaAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.syncDriversToCloud { count ->
                                    Toast.makeText(context, "Synced $count Riders with User App!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, ZaykaEmerald.copy(alpha = 0.5f)),
                            modifier = Modifier.weight(1f).testTag("sync_riders_btn")
                        ) {
                            Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = ZaykaEmerald, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sync Riders", color = ZaykaEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Full Master Sync Button
                    Button(
                        onClick = {
                            viewModel.syncAllToCloud { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                        },
                        enabled = !isSyncing,
                        colors = ButtonDefaults.buttonColors(containerColor = ZaykaEmerald),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("full_sync_cloud_btn")
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Syncing with Zayka User App...", color = Color.White, fontSize = 13.sp)
                        } else {
                            Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("⚡ Push Full Database to User App Cloud", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Restaurant Profile Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ZaykaSurface),
                border = BorderStroke(1.dp, ZaykaSurfaceBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = ZaykaOrange)
                        Text(
                            text = "Restaurant Profile & Brand",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedTextField(
                        value = restaurantName,
                        onValueChange = { restaurantName = it },
                        label = { Text("Restaurant Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ZaykaOrange,
                            unfocusedBorderColor = ZaykaSurfaceBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = contactPhone,
                        onValueChange = { contactPhone = it },
                        label = { Text("Contact Phone") },
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
                        value = storeAddress,
                        onValueChange = { storeAddress = it },
                        label = { Text("Store Address") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = ZaykaOrange,
                            unfocusedBorderColor = ZaykaSurfaceBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Delivery & Service Charges Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ZaykaSurface),
                border = BorderStroke(1.dp, ZaykaSurfaceBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = ZaykaAmber)
                        Text(
                            text = "Delivery & Operational Charges",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = deliveryRadius,
                            onValueChange = { deliveryRadius = it },
                            label = { Text("Radius (Km)") },
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
                            value = minOrderValue,
                            onValueChange = { minOrderValue = it },
                            label = { Text("Min Order (₹)") },
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = baseDeliveryFee,
                            onValueChange = { baseDeliveryFee = it },
                            label = { Text("Delivery Fee (₹)") },
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
                            value = packagingCharge,
                            onValueChange = { packagingCharge = it },
                            label = { Text("Packaging (₹)") },
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = gstPercentage,
                            onValueChange = { gstPercentage = it },
                            label = { Text("GST Rate (%)") },
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
                            value = prepBufferTime,
                            onValueChange = { prepBufferTime = it },
                            label = { Text("Avg Prep (Mins)") },
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
                }
            }
        }

        // Toggles & Preferences Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ZaykaSurface),
                border = BorderStroke(1.dp, ZaykaSurfaceBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Auto-Accept Incoming Orders", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Skip manual confirmation and send directly to kitchen", color = TextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = autoAccept,
                            onCheckedChange = { autoAccept = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = ZaykaEmerald
                            ),
                            modifier = Modifier.scale(0.8f)
                        )
                    }

                    HorizontalDivider(color = ZaykaSurfaceBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Sound & Vibration Chime on New Orders", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Play audible ping and haptic buzz during busy service", color = TextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = soundAlerts,
                            onCheckedChange = { soundAlerts = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = ZaykaOrange
                            ),
                            modifier = Modifier.scale(0.8f)
                        )
                    }
                }
            }
        }

        // Save Button
        item {
            Button(
                onClick = {
                    val updated = (settingsState ?: RestaurantSettings()).copy(
                        restaurantName = restaurantName,
                        contactPhone = contactPhone,
                        storeAddress = storeAddress,
                        deliveryRadiusKm = deliveryRadius.toDoubleOrNull() ?: 10.0,
                        minOrderValue = minOrderValue.toDoubleOrNull() ?: 149.0,
                        baseDeliveryFee = baseDeliveryFee.toDoubleOrNull() ?: 40.0,
                        packagingCharge = packagingCharge.toDoubleOrNull() ?: 25.0,
                        gstPercentage = gstPercentage.toDoubleOrNull() ?: 5.0,
                        prepTimeBufferMinutes = prepBufferTime.toIntOrNull() ?: 20,
                        autoAcceptOrders = autoAccept,
                        soundAlertsEnabled = soundAlerts
                    )
                    viewModel.saveSettings(updated)
                    Toast.makeText(context, "Settings saved and synced with Firestore!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ZaykaOrange),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_settings_btn")
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Configuration", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
