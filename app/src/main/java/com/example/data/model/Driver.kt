package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

enum class DriverStatus {
    AVAILABLE,
    ON_DELIVERY,
    BUSY,
    OFFLINE
}

@Entity(tableName = "drivers")
@JsonClass(generateAdapter = true)
data class Driver(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val vehicleNumber: String = "",
    val status: DriverStatus = DriverStatus.AVAILABLE,
    val activeOrderId: String? = null,
    val completedTodayCount: Int = 0,
    val rating: Float = 4.9f,
    val currentLatitude: Double = 28.6139,
    val currentLongitude: Double = 77.2090,
    val batteryPercent: Int = 92
)
