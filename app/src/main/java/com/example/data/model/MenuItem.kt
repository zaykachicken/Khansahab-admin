package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "menu_items")
@JsonClass(generateAdapter = true)
data class MenuItem(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val category: String = "Biryani Specials",
    val description: String = "",
    val price: Double = 0.0,
    val discountedPrice: Double? = null,
    val imageUrl: String = "",
    val isVeg: Boolean = false,
    val isAvailable: Boolean = true,
    val preparationTimeMinutes: Int = 20,
    val spiceOptions: List<String> = listOf("Mild", "Medium", "Spicy"),
    val availableAddOns: List<String> = emptyList(),
    val totalOrdersCount: Int = 0,
    val rating: Float = 4.8f
)
