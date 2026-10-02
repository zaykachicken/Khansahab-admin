package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.ElectricBike
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Driver
import com.example.data.model.DriverStatus
import com.example.data.model.Order
import com.example.data.model.OrderStatus
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
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarMapView(
    drivers: List<Driver>,
    activeOrders: List<Order>,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarTransition")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarSweep"
    )

    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "HubPulse"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ZaykaCharcoalBg),
        border = BorderStroke(1.dp, ZaykaSurfaceBorder)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val maxRadius = (size.height / 2f) - 20f

                // Draw Radar Grid Circles
                drawCircle(
                    color = ZaykaSurfaceBorder,
                    radius = maxRadius * 0.33f,
                    center = center,
                    style = Stroke(width = 1f)
                )
                drawCircle(
                    color = ZaykaSurfaceBorder,
                    radius = maxRadius * 0.66f,
                    center = center,
                    style = Stroke(width = 1f)
                )
                drawCircle(
                    color = ZaykaSurfaceBorder,
                    radius = maxRadius,
                    center = center,
                    style = Stroke(width = 1.5f)
                )

                // Grid Crosshairs
                drawLine(
                    color = ZaykaSurfaceBorder.copy(alpha = 0.5f),
                    start = Offset(center.x - maxRadius, center.y),
                    end = Offset(center.x + maxRadius, center.y),
                    strokeWidth = 1f
                )
                drawLine(
                    color = ZaykaSurfaceBorder.copy(alpha = 0.5f),
                    start = Offset(center.x, center.y - maxRadius),
                    end = Offset(center.x, center.y + maxRadius),
                    strokeWidth = 1f
                )

                // Expanding Hub Pulse Ring
                drawCircle(
                    color = ZaykaOrange.copy(alpha = (1f - pulseRadius) * 0.5f),
                    radius = maxRadius * pulseRadius,
                    center = center,
                    style = Stroke(width = 2f)
                )

                // Central Restaurant Hub
                drawCircle(
                    color = ZaykaOrange,
                    radius = 12f,
                    center = center
                )
                drawCircle(
                    color = Color.White,
                    radius = 5f,
                    center = center
                )

                // Radar Sweeper Beam
                val rad = Math.toRadians(sweepAngle.toDouble())
                val beamEnd = Offset(
                    (center.x + maxRadius * cos(rad)).toFloat(),
                    (center.y + maxRadius * sin(rad)).toFloat()
                )
                drawLine(
                    brush = Brush.linearGradient(
                        listOf(ZaykaOrange.copy(alpha = 0.8f), Color.Transparent),
                        start = center,
                        end = beamEnd
                    ),
                    start = center,
                    end = beamEnd,
                    strokeWidth = 3f
                )

                // Plot Active Drivers around hub
                val activeDrivers = drivers.filter { it.status != DriverStatus.OFFLINE }
                activeDrivers.forEachIndexed { index, driver ->
                    val angleOffset = (index * 72.0) + (sweepAngle * 0.05)
                    val distFactor = 0.4f + ((index % 3) * 0.22f)
                    val r = Math.toRadians(angleOffset)
                    val driverPos = Offset(
                        (center.x + (maxRadius * distFactor) * cos(r)).toFloat(),
                        (center.y + (maxRadius * distFactor) * sin(r)).toFloat()
                    )

                    val dotColor = if (driver.status == DriverStatus.ON_DELIVERY) ZaykaBlue else ZaykaEmerald

                    // Draw connecting route line if on delivery
                    if (driver.status == DriverStatus.ON_DELIVERY) {
                        drawLine(
                            color = ZaykaBlue.copy(alpha = 0.4f),
                            start = center,
                            end = driverPos,
                            strokeWidth = 2f
                        )
                    }

                    drawCircle(
                        color = dotColor.copy(alpha = 0.3f),
                        radius = 14f,
                        center = driverPos
                    )
                    drawCircle(
                        color = dotColor,
                        radius = 7f,
                        center = driverPos
                    )
                }

                // Plot Active Customer Delivery Drop Pins
                val outOrders = activeOrders.filter { it.status == OrderStatus.OUT_FOR_DELIVERY }
                outOrders.forEachIndexed { i, _ ->
                    val dropAngle = Math.toRadians((i * 120.0) + 45.0)
                    val dropPos = Offset(
                        (center.x + (maxRadius * 0.85f) * cos(dropAngle)).toFloat(),
                        (center.y + (maxRadius * 0.85f) * sin(dropAngle)).toFloat()
                    )
                    drawCircle(
                        color = ZaykaAmber.copy(alpha = 0.3f),
                        radius = 12f,
                        center = dropPos
                    )
                    drawCircle(
                        color = ZaykaAmber,
                        radius = 6f,
                        center = dropPos
                    )
                }
            }

            // Legend Overlay
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp),
                shape = RoundedCornerShape(8.dp),
                color = ZaykaSurface.copy(alpha = 0.88f),
                border = BorderStroke(1.dp, ZaykaSurfaceBorder)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "LIVE FLEET RADAR",
                        color = ZaykaOrange,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(ZaykaOrange))
                        Text("Hub", color = TextPrimary, fontSize = 9.sp)

                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(ZaykaEmerald))
                        Text("Available", color = TextPrimary, fontSize = 9.sp)

                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(ZaykaBlue))
                        Text("On Route", color = TextPrimary, fontSize = 9.sp)

                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(ZaykaAmber))
                        Text("Drop Pin", color = TextPrimary, fontSize = 9.sp)
                    }
                }
            }

            // Active count indicator on top right
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp),
                shape = RoundedCornerShape(8.dp),
                color = ZaykaSurface.copy(alpha = 0.88f),
                border = BorderStroke(1.dp, ZaykaSurfaceBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NearMe,
                        contentDescription = null,
                        tint = ZaykaEmerald,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "${drivers.count { it.status == DriverStatus.ON_DELIVERY }} In Transit",
                        color = TextPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
