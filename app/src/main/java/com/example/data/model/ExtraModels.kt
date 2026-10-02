package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "coupons")
@JsonClass(generateAdapter = true)
data class Coupon(
    @PrimaryKey
    val id: String = "",
    val code: String = "",
    val title: String = "",
    val description: String = "",
    val discountType: String = "PERCENTAGE", // "PERCENTAGE" or "FLAT"
    val discountValue: Double = 20.0,
    val minOrderAmount: Double = 299.0,
    val maxDiscountAmount: Double = 100.0,
    val isActive: Boolean = true,
    val usageCount: Int = 142
)

@Entity(tableName = "restaurant_settings")
@JsonClass(generateAdapter = true)
data class RestaurantSettings(
    @PrimaryKey
    val id: Int = 1,
    val restaurantName: String = "ZaykaChicken Cafe & Restaurant",
    val tagline: String = "Authentic Charcoal Tandoor & Royal Dum Biryani",
    val isStoreOpen: Boolean = true,
    val isBusyMode: Boolean = false,
    val deliveryRadiusKm: Double = 8.5,
    val minOrderValue: Double = 149.0,
    val baseDeliveryFee: Double = 35.0,
    val packagingCharge: Double = 25.0,
    val gstPercentage: Double = 5.0,
    val prepTimeBufferMinutes: Int = 25,
    val contactPhone: String = "+91 98765 43210",
    val storeAddress: String = "Plot 42, Gourmet Boulevard, Zayka Square, City Center",
    val autoAcceptOrders: Boolean = false,
    val soundAlertsEnabled: Boolean = true
)

enum class StaffRole {
    OWNER,
    KITCHEN_MANAGER,
    DISPATCHER
}

@JsonClass(generateAdapter = true)
data class StaffUser(
    val uid: String = "",
    val name: String = "Zayka Master Admin",
    val email: String = "admin@zaykachicken.com",
    val role: StaffRole = StaffRole.OWNER,
    val photoUrl: String? = null
)
