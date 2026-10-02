package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.ElectricBike
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DriverStatus
import com.example.data.model.OrderStatus
import com.example.data.model.StaffRole
import com.example.data.model.StaffUser
import com.example.ui.theme.NonVegRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VegGreen
import com.example.ui.theme.ZaykaAmber
import com.example.ui.theme.ZaykaBlue
import com.example.ui.theme.ZaykaCharcoalBg
import com.example.ui.theme.ZaykaCrimson
import com.example.ui.theme.ZaykaEmerald
import com.example.ui.theme.ZaykaOrange
import com.example.ui.theme.ZaykaOrangeDark
import com.example.ui.theme.ZaykaSurface
import com.example.ui.theme.ZaykaSurfaceBorder
import com.example.ui.theme.ZaykaSurfaceElevated

@Composable
fun ZaykaTopHeader(
    isStoreOpen: Boolean,
    isBusyMode: Boolean,
    pendingOrdersCount: Int,
    currentUser: StaffUser?,
    onToggleStoreStatus: (Boolean) -> Unit,
    onToggleBusyMode: (Boolean) -> Unit,
    onSimulateOrder: () -> Unit,
    onOpenRoleDialog: () -> Unit
) {
    Surface(
        color = ZaykaSurface,
        border = BorderStroke(1.dp, ZaykaSurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand Identity
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(ZaykaOrange, ZaykaOrangeDark)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = "Zayka Brand Logo",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "ZAYKA",
                                color = ZaykaOrange,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "ADMIN",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Text(
                            text = "Chicken Cafe & Restaurant",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Staff Role & Simulate Order Action
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Simulate Incoming Order button for live demonstration
                    Surface(
                        onClick = onSimulateOrder,
                        shape = RoundedCornerShape(20.dp),
                        color = ZaykaOrange.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, ZaykaOrange.copy(alpha = 0.4f)),
                        modifier = Modifier.testTag("simulate_order_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("⚡ +Order", color = ZaykaOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Staff User Chip
                    Surface(
                        onClick = onOpenRoleDialog,
                        shape = RoundedCornerShape(20.dp),
                        color = ZaykaSurfaceElevated,
                        border = BorderStroke(1.dp, ZaykaSurfaceBorder),
                        modifier = Modifier.testTag("staff_role_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isStoreOpen) ZaykaEmerald else ZaykaCrimson)
                            )
                            Text(
                                text = currentUser?.role?.name?.replace("_", " ") ?: "OWNER",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Operational Bar: Store Open/Closed Toggle & Busy Kitchen Mode
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ZaykaSurfaceElevated)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = "Store Status",
                        tint = if (isStoreOpen) ZaykaEmerald else ZaykaCrimson,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (isStoreOpen) "Store is ONLINE" else "Store is CLOSED",
                        color = if (isStoreOpen) ZaykaEmerald else ZaykaCrimson,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Switch(
                        checked = isStoreOpen,
                        onCheckedChange = onToggleStoreStatus,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = ZaykaEmerald,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = ZaykaCharcoalBg
                        ),
                        modifier = Modifier
                            .scale(0.75f)
                            .testTag("store_open_toggle")
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (isBusyMode) "Rush Mode (+15m)" else "Normal Prep",
                        color = if (isBusyMode) ZaykaAmber else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Switch(
                        checked = isBusyMode,
                        onCheckedChange = onToggleBusyMode,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = ZaykaAmber,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = ZaykaCharcoalBg
                        ),
                        modifier = Modifier
                            .scale(0.75f)
                            .testTag("busy_mode_toggle")
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    changeText: String? = null,
    isPositive: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ZaykaSurface),
        border = BorderStroke(1.dp, ZaykaSurfaceBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = value,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )

            if (changeText != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = if (isPositive) ZaykaEmerald else ZaykaCrimson,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = changeText,
                        color = if (isPositive) ZaykaEmerald else ZaykaCrimson,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun OrderStatusBadge(status: OrderStatus) {
    val (bgColor, textColor, label) = when (status) {
        OrderStatus.PENDING -> Triple(ZaykaCrimson.copy(alpha = 0.18f), ZaykaCrimson, "PENDING")
        OrderStatus.CONFIRMED -> Triple(ZaykaAmber.copy(alpha = 0.18f), ZaykaAmber, "CONFIRMED")
        OrderStatus.PREPARING -> Triple(ZaykaOrange.copy(alpha = 0.18f), ZaykaOrange, "PREPARING")
        OrderStatus.READY -> Triple(ZaykaEmerald.copy(alpha = 0.18f), ZaykaEmerald, "READY")
        OrderStatus.OUT_FOR_DELIVERY -> Triple(ZaykaBlue.copy(alpha = 0.18f), ZaykaBlue, "DISPATCHED")
        OrderStatus.DELIVERED -> Triple(Color(0xFF2E7D32).copy(alpha = 0.2f), Color(0xFF81C784), "DELIVERED")
        OrderStatus.CANCELLED -> Triple(Color.DarkGray.copy(alpha = 0.3f), Color.LightGray, "CANCELLED")
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.4f))
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun DriverStatusBadge(status: DriverStatus) {
    val (bgColor, textColor, label) = when (status) {
        DriverStatus.AVAILABLE -> Triple(ZaykaEmerald.copy(alpha = 0.18f), ZaykaEmerald, "Available")
        DriverStatus.ON_DELIVERY -> Triple(ZaykaBlue.copy(alpha = 0.18f), ZaykaBlue, "On Delivery")
        DriverStatus.BUSY -> Triple(ZaykaAmber.copy(alpha = 0.18f), ZaykaAmber, "Busy")
        DriverStatus.OFFLINE -> Triple(Color.Gray.copy(alpha = 0.2f), Color.LightGray, "Offline")
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bgColor,
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Text(
                text = label,
                color = textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun VegNonVegBadge(isVeg: Boolean) {
    val color = if (isVeg) VegGreen else NonVegRed
    Box(
        modifier = Modifier
            .size(16.dp)
            .border(1.5.dp, color, RoundedCornerShape(3.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
    }
}
