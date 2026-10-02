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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Coupon
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
import java.util.UUID

@Composable
fun OffersScreen(
    viewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    val coupons by viewModel.allCoupons.collectAsState()
    var showAddCouponDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ZaykaCharcoalBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Offers & Discount Coupons",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Syncs with Zayka Customer App",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = { showAddCouponDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ZaykaOrange),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("create_coupon_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Create Promo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(coupons) { coupon ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ZaykaSurface),
                border = BorderStroke(1.dp, if (coupon.isActive) ZaykaOrange.copy(alpha = 0.3f) else ZaykaSurfaceBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ZaykaOrange.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, ZaykaOrange.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = coupon.code,
                                    color = ZaykaOrange,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = if (coupon.discountType == "PERCENTAGE") "${coupon.discountValue.toInt()}% OFF" else "FLAT ₹${coupon.discountValue.toInt()} OFF",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Switch(
                            checked = coupon.isActive,
                            onCheckedChange = { isActive ->
                                viewModel.toggleCoupon(coupon.id, isActive)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = ZaykaEmerald,
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = ZaykaCharcoalBg
                            ),
                            modifier = Modifier.scale(0.75f)
                        )
                    }

                    Text(
                        text = coupon.description,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    HorizontalDivider(color = ZaykaSurfaceBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Min Order: ₹${coupon.minOrderAmount.toInt()} • Max Disc: ₹${coupon.maxDiscountAmount.toInt()}",
                            color = TextTertiary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "Redeemed ${coupon.usageCount} times",
                            color = ZaykaAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    if (showAddCouponDialog) {
        AddCouponDialog(
            onDismiss = { showAddCouponDialog = false },
            onSave = { newCoupon ->
                viewModel.saveCoupon(newCoupon)
                showAddCouponDialog = false
            }
        )
    }
}

@Composable
fun AddCouponDialog(
    onDismiss: () -> Unit,
    onSave: (Coupon) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var discountValText by remember { mutableStateOf("20") }
    var minOrderText by remember { mutableStateOf("399") }
    var isPercentage by remember { mutableStateOf(true) }

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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Create Promo Code",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it.uppercase() },
                    label = { Text("Coupon Code (e.g. ZAYKA50)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ZaykaOrange,
                        unfocusedBorderColor = ZaykaSurfaceBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("coupon_code_input")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Offer Description") },
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
                        value = discountValText,
                        onValueChange = { discountValText = it },
                        label = { Text(if (isPercentage) "Discount (%)" else "Discount (₹)") },
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
                        value = minOrderText,
                        onValueChange = { minOrderText = it },
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
                            val newCoupon = Coupon(
                                id = "C-${UUID.randomUUID().toString().take(4).uppercase()}",
                                code = code.ifBlank { "OFFER20" },
                                title = "Special Discount",
                                description = description.ifBlank { "Special discount on ZaykaChicken menu" },
                                discountType = if (isPercentage) "PERCENTAGE" else "FLAT",
                                discountValue = discountValText.toDoubleOrNull() ?: 20.0,
                                minOrderAmount = minOrderText.toDoubleOrNull() ?: 299.0,
                                maxDiscountAmount = 150.0,
                                isActive = true
                            )
                            onSave(newCoupon)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ZaykaOrange),
                        modifier = Modifier.testTag("save_coupon_btn")
                    ) {
                        Text("Create")
                    }
                }
            }
        }
    }
}
