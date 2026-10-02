package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.ZaykaAmber
import com.example.ui.theme.ZaykaBlue
import com.example.ui.theme.ZaykaEmerald
import com.example.ui.theme.ZaykaOrange
import com.example.ui.theme.ZaykaOrangeDark
import com.example.ui.theme.ZaykaPurple
import com.example.ui.theme.ZaykaSurface
import com.example.ui.theme.ZaykaSurfaceBorder
import com.example.ui.theme.ZaykaSurfaceElevated

@Composable
fun HourlySalesBarChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ZaykaSurface),
        border = BorderStroke(1.dp, ZaykaSurfaceBorder)
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
                Column {
                    Text(
                        text = "Peak Order Hours & Revenue",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Highest rush observed 7:00 PM - 10:00 PM",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ZaykaOrange.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Hourly",
                        color = ZaykaOrange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            val maxVal = data.maxOfOrNull { it.second } ?: 1.0

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(top = 10.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height - 30f
                    val barSpacing = canvasWidth / data.size
                    val barWidth = barSpacing * 0.55f

                    data.forEachIndexed { index, pair ->
                        val barHeight = (pair.second / maxVal).toFloat() * canvasHeight
                        val left = (index * barSpacing) + (barSpacing - barWidth) / 2f
                        val top = canvasHeight - barHeight

                        // Highlight peak dinner rush bars with orange-amber gradient
                        val isPeak = pair.first.contains("7") || pair.first.contains("8") || pair.first.contains("9")
                        val barBrush = if (isPeak) {
                            Brush.verticalGradient(listOf(ZaykaAmber, ZaykaOrange))
                        } else {
                            Brush.verticalGradient(listOf(ZaykaSurfaceElevated.copy(alpha = 0.8f), ZaykaSurfaceBorder))
                        }

                        drawRoundRect(
                            brush = barBrush,
                            topLeft = Offset(left, top),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(6f, 6f)
                        )
                    }
                }

                // X-Axis Labels Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    data.forEach { pair ->
                        Text(
                            text = pair.first,
                            color = if (pair.first.contains("8") || pair.first.contains("7")) ZaykaOrange else TextTertiary,
                            fontSize = 10.sp,
                            fontWeight = if (pair.first.contains("8")) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategorySalesBreakdown(
    categories: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ZaykaSurface),
        border = BorderStroke(1.dp, ZaykaSurfaceBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Sales by Menu Category",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            val totalRevenue = categories.sumOf { it.second }.coerceAtLeast(1.0)
            val colors = listOf(ZaykaOrange, ZaykaAmber, ZaykaEmerald, ZaykaBlue, ZaykaPurple, Color(0xFFFF5252))

            categories.forEachIndexed { index, item ->
                val percent = (item.second / totalRevenue * 100).toInt()
                val color = colors[index % colors.size]

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
                            Text(text = item.first, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = "₹${"%.0f".format(item.second)}", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "$percent%", color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(ZaykaSurfaceElevated)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(item.second.toFloat() / totalRevenue.toFloat())
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(3.dp))
                                .background(color)
                        )
                    }
                }
            }
        }
    }
}
