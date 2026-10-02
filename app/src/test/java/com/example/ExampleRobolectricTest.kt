package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Order
import com.example.data.model.OrderItem
import com.example.data.model.OrderStatus
import com.example.data.model.OrderType
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Zayka Admin", appName)
    }

    @Test
    fun `test order calculation and status integrity`() {
        val order = Order(
            id = "ZK-1001",
            customerName = "Aryan Malhotra",
            customerPhone = "+91 98711 22334",
            deliveryAddress = "Flat 402, Pinnacle Heights",
            orderType = OrderType.DELIVERY,
            status = OrderStatus.PENDING,
            items = listOf(
                OrderItem(itemId = "M01", name = "Zayka Royal Chicken Dum Biryani", price = 299.0, quantity = 2, isVeg = false),
                OrderItem(itemId = "M04", name = "Zayka Fiery Chicken Wings", price = 239.0, quantity = 1, isVeg = false)
            ),
            subtotal = 837.0,
            tax = 41.85,
            deliveryFee = 40.0,
            packagingFee = 25.0,
            discount = 50.0,
            totalAmount = 893.85,
            paymentMethod = PaymentMethod.ONLINE_UPI,
            paymentStatus = PaymentStatus.PAID
        )

        assertNotNull(order)
        assertEquals("ZK-1001", order.id)
        assertEquals(2, order.items.size)
        assertEquals(OrderStatus.PENDING, order.status)
        assertEquals(893.85, order.totalAmount, 0.01)
    }
}
