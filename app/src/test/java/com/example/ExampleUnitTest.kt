package com.example

import com.example.data.model.OrderStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testOrderStatusEnumValues() {
        assertEquals("PENDING", OrderStatus.PENDING.name)
        assertEquals("DELIVERED", OrderStatus.DELIVERED.name)
        assertTrue(OrderStatus.values().contains(OrderStatus.PREPARING))
    }
}
