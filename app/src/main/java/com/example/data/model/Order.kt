package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    READY,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}

enum class OrderType {
    DELIVERY,
    PICKUP,
    DINE_IN
}

enum class PaymentMethod {
    ONLINE_UPI,
    CREDIT_CARD,
    COD
}

enum class PaymentStatus {
    PAID,
    PENDING,
    REFUNDED
}

@JsonClass(generateAdapter = true)
data class OrderItem(
    val itemId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1,
    val isVeg: Boolean = false,
    val addOns: List<String> = emptyList(),
    val spiceLevel: String = "Medium"
)

@Entity(tableName = "orders")
@JsonClass(generateAdapter = true)
data class Order(
    @PrimaryKey
    val id: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val deliveryAddress: String = "",
    val orderType: OrderType = OrderType.DELIVERY,
    val status: OrderStatus = OrderStatus.PENDING,
    val items: List<OrderItem> = emptyList(),
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val packagingFee: Double = 0.0,
    val discount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val paymentMethod: PaymentMethod = PaymentMethod.ONLINE_UPI,
    val paymentStatus: PaymentStatus = PaymentStatus.PAID,
    val assignedDriverId: String? = null,
    val assignedDriverName: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val estimatedDeliveryMinutes: Int = 30,
    val instructions: String? = null,
    val cancellationReason: String? = null
)
