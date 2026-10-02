package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.DriverStatus
import com.example.data.model.OrderItem
import com.example.data.model.OrderStatus
import com.example.data.model.OrderType
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentStatus
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @TypeConverter
    fun fromOrderItemList(items: List<OrderItem>?): String {
        if (items == null) return "[]"
        val type = Types.newParameterizedType(List::class.java, OrderItem::class.java)
        val adapter = moshi.adapter<List<OrderItem>>(type)
        return adapter.toJson(items)
    }

    @TypeConverter
    fun toOrderItemList(data: String?): List<OrderItem> {
        if (data.isNullOrEmpty()) return emptyList()
        val type = Types.newParameterizedType(List::class.java, OrderItem::class.java)
        val adapter = moshi.adapter<List<OrderItem>>(type)
        return adapter.fromJson(data) ?: emptyList()
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        if (list == null) return "[]"
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter = moshi.adapter<List<String>>(type)
        return adapter.toJson(list)
    }

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        if (data.isNullOrEmpty()) return emptyList()
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter = moshi.adapter<List<String>>(type)
        return adapter.fromJson(data) ?: emptyList()
    }

    @TypeConverter
    fun fromOrderStatus(status: OrderStatus?): String = status?.name ?: OrderStatus.PENDING.name

    @TypeConverter
    fun toOrderStatus(value: String?): OrderStatus = try {
        OrderStatus.valueOf(value ?: OrderStatus.PENDING.name)
    } catch (e: Exception) {
        OrderStatus.PENDING
    }

    @TypeConverter
    fun fromOrderType(type: OrderType?): String = type?.name ?: OrderType.DELIVERY.name

    @TypeConverter
    fun toOrderType(value: String?): OrderType = try {
        OrderType.valueOf(value ?: OrderType.DELIVERY.name)
    } catch (e: Exception) {
        OrderType.DELIVERY
    }

    @TypeConverter
    fun fromPaymentMethod(method: PaymentMethod?): String = method?.name ?: PaymentMethod.ONLINE_UPI.name

    @TypeConverter
    fun toPaymentMethod(value: String?): PaymentMethod = try {
        PaymentMethod.valueOf(value ?: PaymentMethod.ONLINE_UPI.name)
    } catch (e: Exception) {
        PaymentMethod.ONLINE_UPI
    }

    @TypeConverter
    fun fromPaymentStatus(status: PaymentStatus?): String = status?.name ?: PaymentStatus.PAID.name

    @TypeConverter
    fun toPaymentStatus(value: String?): PaymentStatus = try {
        PaymentStatus.valueOf(value ?: PaymentStatus.PAID.name)
    } catch (e: Exception) {
        PaymentStatus.PAID
    }

    @TypeConverter
    fun fromDriverStatus(status: DriverStatus?): String = status?.name ?: DriverStatus.AVAILABLE.name

    @TypeConverter
    fun toDriverStatus(value: String?): DriverStatus = try {
        DriverStatus.valueOf(value ?: DriverStatus.AVAILABLE.name)
    } catch (e: Exception) {
        DriverStatus.AVAILABLE
    }
}
