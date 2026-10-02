package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.OrderType
import com.example.data.model.PaymentMethod
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.ZaykaAmber
import com.example.ui.theme.ZaykaBlue
import com.example.ui.theme.ZaykaCrimson
import com.example.ui.theme.ZaykaEmerald
import com.example.ui.theme.ZaykaOrange
import com.example.ui.theme.ZaykaOrangeDark
import com.example.ui.theme.ZaykaSurface
import com.example.ui.theme.ZaykaSurfaceBorder
import com.example.ui.theme.ZaykaSurfaceElevated
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OrderCard(
    order: Order,
    onStatusChange: (OrderStatus) -> Unit,
    onAssignDriverClick: () -> Unit,
    onViewDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val timeAgo = remember(order.createdAt) {
        val diffMins = ((System.currentTimeMillis() - order.createdAt) / (60 * 1000)).toInt()
        if (diffMins <= 0) "Just now" else "$diffMins min ago"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .testTag("order_card_${order.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ZaykaSurface),
        border = BorderStroke(
            1.dp,
            if (order.status == OrderStatus.PENDING) ZaykaCrimson.copy(alpha = 0.6f) else ZaykaSurfaceBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Order ID, Type chip, Time Ago, Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "#${order.id}",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = ZaykaSurfaceElevated
                    ) {
                        Text(
                            text = order.orderType.name,
                            color = ZaykaAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = timeAgo,
                            color = TextTertiary,
                            fontSize = 11.sp
                        )
                    }
                    OrderStatusBadge(status = order.status)
                }
            }

            // Customer Details & Location
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = order.customerName,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Address",
                            tint = ZaykaOrange,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = order.deliveryAddress,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            maxLines = if (expanded) 3 else 1
                        )
                    }
                }

                // Call Action Button
                IconButton(
                    onClick = {
                        val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${order.customerPhone}")
                        }
                        context.startActivity(dialIntent)
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ZaykaSurfaceElevated)
                        .testTag("call_customer_${order.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call Customer",
                        tint = ZaykaEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            HorizontalDivider(color = ZaykaSurfaceBorder, thickness = 0.8.dp)

            // Items Preview
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                order.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            VegNonVegBadge(isVeg = item.isVeg)
                            Text(
                                text = "${item.quantity}x ${item.name}",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = "₹${"%.0f".format(item.price * item.quantity)}",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (expanded && item.addOns.isNotEmpty()) {
                        Text(
                            text = "  + ${item.addOns.joinToString(", ")} (${item.spiceLevel})",
                            color = TextTertiary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 24.dp)
                        )
                    }
                }
            }

            // Expanded instructions & driver info
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!order.instructions.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ZaykaSurfaceElevated,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "📝 Note: ${order.instructions}",
                                color = ZaykaAmber,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    if (order.assignedDriverName != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ZaykaBlue.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, ZaykaBlue.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeliveryDining,
                                    contentDescription = null,
                                    tint = ZaykaBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Rider: ${order.assignedDriverName}",
                                    color = ZaykaBlue,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Price Breakdown
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ZaykaSurfaceElevated)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal", color = TextSecondary, fontSize = 12.sp)
                            Text("₹${"%.1f".format(order.subtotal)}", color = TextPrimary, fontSize = 12.sp)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Taxes & GST (5%)", color = TextSecondary, fontSize = 12.sp)
                            Text("₹${"%.1f".format(order.tax)}", color = TextPrimary, fontSize = 12.sp)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Delivery & Packaging", color = TextSecondary, fontSize = 12.sp)
                            Text("₹${"%.1f".format(order.deliveryFee + order.packagingFee)}", color = TextPrimary, fontSize = 12.sp)
                        }
                        if (order.discount > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Discount", color = ZaykaEmerald, fontSize = 12.sp)
                                Text("-₹${"%.1f".format(order.discount)}", color = ZaykaEmerald, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Bottom Bar: Total Amount, Payment Method, and State Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Amount",
                        color = TextTertiary,
                        fontSize = 11.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "₹${"%.0f".format(order.totalAmount)}",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (order.paymentMethod == PaymentMethod.COD) ZaykaAmber.copy(alpha = 0.2f) else ZaykaEmerald.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (order.paymentMethod == PaymentMethod.COD) "COD" else "PAID UPI",
                                color = if (order.paymentMethod == PaymentMethod.COD) ZaykaAmber else ZaykaEmerald,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Dynamic Action Button based on current order step
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    when (order.status) {
                        OrderStatus.PENDING -> {
                            OutlinedButton(
                                onClick = { onStatusChange(OrderStatus.CANCELLED) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ZaykaCrimson),
                                border = BorderStroke(1.dp, ZaykaCrimson.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("reject_order_${order.id}")
                            ) {
                                Text("Reject", fontSize = 12.sp)
                            }
                            Button(
                                onClick = { onStatusChange(OrderStatus.CONFIRMED) },
                                colors = ButtonDefaults.buttonColors(containerColor = ZaykaOrange),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("accept_order_${order.id}")
                            ) {
                                Text("Accept Order", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        OrderStatus.CONFIRMED -> {
                            Button(
                                onClick = { onStatusChange(OrderStatus.PREPARING) },
                                colors = ButtonDefaults.buttonColors(containerColor = ZaykaOrange),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("start_prep_${order.id}")
                            ) {
                                Icon(Icons.Default.OutdoorGrill, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Start Cooking", fontSize = 12.sp)
                            }
                        }
                        OrderStatus.PREPARING -> {
                            Button(
                                onClick = { onStatusChange(OrderStatus.READY) },
                                colors = ButtonDefaults.buttonColors(containerColor = ZaykaEmerald),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("mark_ready_${order.id}")
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Mark Ready", fontSize = 12.sp)
                            }
                        }
                        OrderStatus.READY -> {
                            Button(
                                onClick = onAssignDriverClick,
                                colors = ButtonDefaults.buttonColors(containerColor = ZaykaBlue),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("assign_driver_${order.id}")
                            ) {
                                Icon(Icons.Default.DeliveryDining, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Dispatch Rider", fontSize = 12.sp)
                            }
                        }
                        OrderStatus.OUT_FOR_DELIVERY -> {
                            Button(
                                onClick = { onStatusChange(OrderStatus.DELIVERED) },
                                colors = ButtonDefaults.buttonColors(containerColor = ZaykaEmerald),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("complete_delivery_${order.id}")
                            ) {
                                Text("Complete Delivery", fontSize = 12.sp)
                            }
                        }
                        OrderStatus.DELIVERED -> {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ZaykaEmerald.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Delivered ✓",
                                    color = ZaykaEmerald,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                        OrderStatus.CANCELLED -> {
                            Text(
                                text = "Cancelled",
                                color = TextTertiary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
