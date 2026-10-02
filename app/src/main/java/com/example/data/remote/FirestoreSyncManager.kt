package com.example.data.remote

import android.util.Log
import com.example.data.model.Coupon
import com.example.data.model.Driver
import com.example.data.model.DriverStatus
import com.example.data.model.MenuItem
import com.example.data.model.Order
import com.example.data.model.OrderItem
import com.example.data.model.OrderStatus
import com.example.data.model.OrderType
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentStatus
import com.example.data.model.RestaurantSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreSyncManager {
    private val TAG = "FirestoreSyncManager"
    private var ordersListener: ListenerRegistration? = null
    private var settingsListener: ListenerRegistration? = null
    private var driversListener: ListenerRegistration? = null

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "Firestore initialization fallback: ${e.message}")
            null
        }
    }

    val isConnected: Boolean
        get() = firestore != null

    suspend fun ensureAuthenticated() = withContext(Dispatchers.IO) {
        try {
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser == null) {
                auth.signInAnonymously().await()
                Log.d(TAG, "Firebase Auth anonymous sign-in success: ${auth.currentUser?.uid}")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Auth sign-in status: ${e.message}")
        }
    }

    // ==================== REAL-TIME LISTENERS (USER APP -> ADMIN APP) ====================

    fun startListeningToOrders(
        onOrderReceived: (Order) -> Unit,
        onError: (Exception) -> Unit = {}
    ) {
        val db = firestore ?: return
        try {
            ordersListener?.remove()
            ordersListener = db.collection("orders")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        if (error.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                            Log.w(TAG, "Firestore permission notice: 'orders' collection access requires Firebase rules permission or authentication.")
                        } else {
                            Log.w(TAG, "Orders snapshot listener status: ${error.message}")
                        }
                        onError(error)
                        return@addSnapshotListener
                    }

                    snapshot?.documentChanges?.forEach { change ->
                        try {
                            val order = parseOrderFromSnapshot(change.document)
                            if (order != null) {
                                onOrderReceived(order)
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Error parsing incoming order: ${e.message}")
                        }
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to start orders listener: ${e.message}")
        }
    }

    fun startListeningToSettings(
        onSettingsReceived: (RestaurantSettings) -> Unit
    ) {
        val db = firestore ?: return
        try {
            settingsListener?.remove()
            settingsListener = db.collection("restaurant_settings")
                .document("general_settings")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        if (error.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                            Log.w(TAG, "Firestore permission notice: 'restaurant_settings' access restricted.")
                        }
                        return@addSnapshotListener
                    }
                    if (snapshot == null || !snapshot.exists()) return@addSnapshotListener
                    try {
                        val settings = parseSettingsFromSnapshot(snapshot)
                        if (settings != null) {
                            onSettingsReceived(settings)
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Error parsing settings snapshot: ${e.message}")
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to start settings listener: ${e.message}")
        }
    }

    fun stopAllListeners() {
        ordersListener?.remove()
        ordersListener = null
        settingsListener?.remove()
        settingsListener = null
        driversListener?.remove()
        driversListener = null
    }

    // ==================== TWO-WAY SYNC METHODS (ADMIN APP -> USER APP) ====================

    suspend fun syncOrderToFirestore(order: Order): Boolean = withContext(Dispatchers.IO) {
        try {
            val orderMap = orderToMap(order)
            firestore?.collection("orders")?.document(order.id)?.set(orderMap, SetOptions.merge())?.await()
            true
        } catch (e: Exception) {
            Log.w(TAG, "Sync order notice for #${order.id}: ${e.message}")
            false
        }
    }

    suspend fun syncMenuItemToFirestore(item: MenuItem): Boolean = withContext(Dispatchers.IO) {
        try {
            val itemMap = menuItemToMap(item)
            firestore?.collection("menu_items")?.document(item.id)?.set(itemMap, SetOptions.merge())?.await()
            true
        } catch (e: Exception) {
            Log.w(TAG, "Sync menu item notice for ${item.name}: ${e.message}")
            false
        }
    }

    suspend fun syncDriverToFirestore(driver: Driver): Boolean = withContext(Dispatchers.IO) {
        try {
            val driverMap = driverToMap(driver)
            firestore?.collection("drivers")?.document(driver.id)?.set(driverMap, SetOptions.merge())?.await()
            true
        } catch (e: Exception) {
            Log.w(TAG, "Sync driver notice for ${driver.name}: ${e.message}")
            false
        }
    }

    suspend fun syncCouponToFirestore(coupon: Coupon): Boolean = withContext(Dispatchers.IO) {
        try {
            val couponMap = couponToMap(coupon)
            firestore?.collection("coupons")?.document(coupon.id)?.set(couponMap, SetOptions.merge())?.await()
            true
        } catch (e: Exception) {
            Log.w(TAG, "Sync coupon notice for ${coupon.code}: ${e.message}")
            false
        }
    }

    suspend fun syncSettingsToFirestore(settings: RestaurantSettings): Boolean = withContext(Dispatchers.IO) {
        try {
            val settingsMap = settingsToMap(settings)
            firestore?.collection("restaurant_settings")?.document("general_settings")?.set(settingsMap, SetOptions.merge())?.await()
            true
        } catch (e: Exception) {
            Log.w(TAG, "Sync settings notice: ${e.message}")
            false
        }
    }

    // ==================== BATCH FULL SYNC (PUSH ALL LOCAL TO CLOUD) ====================

    suspend fun syncAllMenuBatch(items: List<MenuItem>): Int = withContext(Dispatchers.IO) {
        var count = 0
        try {
            val batch = firestore?.batch() ?: return@withContext 0
            items.forEach { item ->
                val docRef = firestore?.collection("menu_items")?.document(item.id)
                if (docRef != null) {
                    batch.set(docRef, menuItemToMap(item), SetOptions.merge())
                    count++
                }
            }
            batch.commit().await()
        } catch (e: Exception) {
            Log.w(TAG, "Batch menu sync notice: ${e.message}")
        }
        count
    }

    suspend fun syncAllCouponsBatch(coupons: List<Coupon>): Int = withContext(Dispatchers.IO) {
        var count = 0
        try {
            val batch = firestore?.batch() ?: return@withContext 0
            coupons.forEach { coupon ->
                val docRef = firestore?.collection("coupons")?.document(coupon.id)
                if (docRef != null) {
                    batch.set(docRef, couponToMap(coupon), SetOptions.merge())
                    count++
                }
            }
            batch.commit().await()
        } catch (e: Exception) {
            Log.w(TAG, "Batch coupon sync notice: ${e.message}")
        }
        count
    }

    suspend fun syncAllDriversBatch(drivers: List<Driver>): Int = withContext(Dispatchers.IO) {
        var count = 0
        try {
            val batch = firestore?.batch() ?: return@withContext 0
            drivers.forEach { driver ->
                val docRef = firestore?.collection("drivers")?.document(driver.id)
                if (docRef != null) {
                    batch.set(docRef, driverToMap(driver), SetOptions.merge())
                    count++
                }
            }
            batch.commit().await()
        } catch (e: Exception) {
            Log.w(TAG, "Batch driver sync notice: ${e.message}")
        }
        count
    }

    // ==================== SAFE MAP PARSERS & SERIALIZERS ====================

    private fun orderToMap(order: Order): Map<String, Any?> {
        return mapOf(
            "id" to order.id,
            "customerName" to order.customerName,
            "customerPhone" to order.customerPhone,
            "deliveryAddress" to order.deliveryAddress,
            "orderType" to order.orderType.name,
            "status" to order.status.name,
            "items" to order.items.map { item ->
                mapOf(
                    "itemId" to item.itemId,
                    "name" to item.name,
                    "price" to item.price,
                    "quantity" to item.quantity,
                    "isVeg" to item.isVeg,
                    "addOns" to item.addOns,
                    "spiceLevel" to item.spiceLevel
                )
            },
            "subtotal" to order.subtotal,
            "tax" to order.tax,
            "deliveryFee" to order.deliveryFee,
            "packagingFee" to order.packagingFee,
            "discount" to order.discount,
            "totalAmount" to order.totalAmount,
            "paymentMethod" to order.paymentMethod.name,
            "paymentStatus" to order.paymentStatus.name,
            "assignedDriverId" to order.assignedDriverId,
            "assignedDriverName" to order.assignedDriverName,
            "createdAt" to order.createdAt,
            "estimatedDeliveryMinutes" to order.estimatedDeliveryMinutes,
            "instructions" to order.instructions,
            "cancellationReason" to order.cancellationReason
        )
    }

    private fun parseOrderFromSnapshot(doc: DocumentSnapshot): Order? {
        if (!doc.exists()) return null
        val id = doc.getString("id") ?: doc.id
        val customerName = doc.getString("customerName") ?: doc.getString("userName") ?: "Zayka Guest"
        val customerPhone = doc.getString("customerPhone") ?: doc.getString("userPhone") ?: ""
        val deliveryAddress = doc.getString("deliveryAddress") ?: doc.getString("address") ?: "Store Pickup"
        
        val orderTypeStr = doc.getString("orderType") ?: "DELIVERY"
        val orderType = try { OrderType.valueOf(orderTypeStr.uppercase()) } catch (_: Exception) { OrderType.DELIVERY }
        
        val statusStr = doc.getString("status") ?: "PENDING"
        val status = try { OrderStatus.valueOf(statusStr.uppercase()) } catch (_: Exception) { OrderStatus.PENDING }

        val itemsRaw = doc.get("items") as? List<*>
        val items = itemsRaw?.mapNotNull { itemObj ->
            if (itemObj is Map<*, *>) {
                OrderItem(
                    itemId = itemObj["itemId"]?.toString() ?: "",
                    name = itemObj["name"]?.toString() ?: "",
                    price = (itemObj["price"] as? Number)?.toDouble() ?: 0.0,
                    quantity = (itemObj["quantity"] as? Number)?.toInt() ?: 1,
                    isVeg = (itemObj["isVeg"] as? Boolean) ?: false,
                    addOns = (itemObj["addOns"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList(),
                    spiceLevel = itemObj["spiceLevel"]?.toString() ?: "Medium"
                )
            } else null
        } ?: emptyList()

        val subtotal = (doc.get("subtotal") as? Number)?.toDouble() ?: 0.0
        val tax = (doc.get("tax") as? Number)?.toDouble() ?: 0.0
        val deliveryFee = (doc.get("deliveryFee") as? Number)?.toDouble() ?: 0.0
        val packagingFee = (doc.get("packagingFee") as? Number)?.toDouble() ?: 0.0
        val discount = (doc.get("discount") as? Number)?.toDouble() ?: 0.0
        val totalAmount = (doc.get("totalAmount") as? Number)?.toDouble()
            ?: (doc.get("total") as? Number)?.toDouble()
            ?: (subtotal + tax + deliveryFee + packagingFee - discount)

        val paymentMethodStr = doc.getString("paymentMethod") ?: "ONLINE_UPI"
        val paymentMethod = try { PaymentMethod.valueOf(paymentMethodStr.uppercase()) } catch (_: Exception) { PaymentMethod.ONLINE_UPI }

        val paymentStatusStr = doc.getString("paymentStatus") ?: "PAID"
        val paymentStatus = try { PaymentStatus.valueOf(paymentStatusStr.uppercase()) } catch (_: Exception) { PaymentStatus.PAID }

        val assignedDriverId = doc.getString("assignedDriverId")
        val assignedDriverName = doc.getString("assignedDriverName")
        val createdAt = (doc.get("createdAt") as? Number)?.toLong() ?: System.currentTimeMillis()
        val estimatedMinutes = (doc.get("estimatedDeliveryMinutes") as? Number)?.toInt() ?: 30
        val instructions = doc.getString("instructions") ?: doc.getString("specialInstructions")
        val cancellationReason = doc.getString("cancellationReason")

        return Order(
            id = id,
            customerName = customerName,
            customerPhone = customerPhone,
            deliveryAddress = deliveryAddress,
            orderType = orderType,
            status = status,
            items = items,
            subtotal = subtotal,
            tax = tax,
            deliveryFee = deliveryFee,
            packagingFee = packagingFee,
            discount = discount,
            totalAmount = totalAmount,
            paymentMethod = paymentMethod,
            paymentStatus = paymentStatus,
            assignedDriverId = assignedDriverId,
            assignedDriverName = assignedDriverName,
            createdAt = createdAt,
            estimatedDeliveryMinutes = estimatedMinutes,
            instructions = instructions,
            cancellationReason = cancellationReason
        )
    }

    private fun menuItemToMap(item: MenuItem): Map<String, Any?> {
        return mapOf(
            "id" to item.id,
            "name" to item.name,
            "category" to item.category,
            "description" to item.description,
            "price" to item.price,
            "discountedPrice" to item.discountedPrice,
            "imageUrl" to item.imageUrl,
            "isVeg" to item.isVeg,
            "isAvailable" to item.isAvailable,
            "preparationTimeMinutes" to item.preparationTimeMinutes,
            "spiceOptions" to item.spiceOptions,
            "availableAddOns" to item.availableAddOns,
            "totalOrdersCount" to item.totalOrdersCount,
            "rating" to item.rating
        )
    }

    private fun driverToMap(driver: Driver): Map<String, Any?> {
        return mapOf(
            "id" to driver.id,
            "name" to driver.name,
            "phone" to driver.phone,
            "vehicleNumber" to driver.vehicleNumber,
            "status" to driver.status.name,
            "activeOrderId" to driver.activeOrderId,
            "completedTodayCount" to driver.completedTodayCount,
            "rating" to driver.rating,
            "currentLatitude" to driver.currentLatitude,
            "currentLongitude" to driver.currentLongitude,
            "batteryPercent" to driver.batteryPercent
        )
    }

    private fun couponToMap(coupon: Coupon): Map<String, Any?> {
        return mapOf(
            "id" to coupon.id,
            "code" to coupon.code,
            "title" to coupon.title,
            "description" to coupon.description,
            "discountType" to coupon.discountType,
            "discountValue" to coupon.discountValue,
            "minOrderAmount" to coupon.minOrderAmount,
            "maxDiscountAmount" to coupon.maxDiscountAmount,
            "isActive" to coupon.isActive,
            "usageCount" to coupon.usageCount
        )
    }

    private fun settingsToMap(settings: RestaurantSettings): Map<String, Any?> {
        return mapOf(
            "id" to settings.id,
            "restaurantName" to settings.restaurantName,
            "tagline" to settings.tagline,
            "isStoreOpen" to settings.isStoreOpen,
            "isBusyMode" to settings.isBusyMode,
            "deliveryRadiusKm" to settings.deliveryRadiusKm,
            "minOrderValue" to settings.minOrderValue,
            "baseDeliveryFee" to settings.baseDeliveryFee,
            "packagingCharge" to settings.packagingCharge,
            "gstPercentage" to settings.gstPercentage,
            "prepTimeBufferMinutes" to settings.prepTimeBufferMinutes,
            "contactPhone" to settings.contactPhone,
            "storeAddress" to settings.storeAddress,
            "autoAcceptOrders" to settings.autoAcceptOrders,
            "soundAlertsEnabled" to settings.soundAlertsEnabled
        )
    }

    private fun parseSettingsFromSnapshot(doc: DocumentSnapshot): RestaurantSettings? {
        if (!doc.exists()) return null
        return RestaurantSettings(
            id = 1,
            restaurantName = doc.getString("restaurantName") ?: "ZaykaChicken Cafe & Restaurant",
            tagline = doc.getString("tagline") ?: "Flame-Grilled Perfection & Royal Dum Biryani",
            isStoreOpen = doc.getBoolean("isStoreOpen") ?: true,
            isBusyMode = doc.getBoolean("isBusyMode") ?: false,
            deliveryRadiusKm = (doc.get("deliveryRadiusKm") as? Number)?.toDouble() ?: 10.0,
            minOrderValue = (doc.get("minOrderValue") as? Number)?.toDouble() ?: 149.0,
            baseDeliveryFee = (doc.get("baseDeliveryFee") as? Number)?.toDouble() ?: 40.0,
            packagingCharge = (doc.get("packagingCharge") as? Number)?.toDouble() ?: 25.0,
            gstPercentage = (doc.get("gstPercentage") as? Number)?.toDouble() ?: 5.0,
            prepTimeBufferMinutes = (doc.get("prepTimeBufferMinutes") as? Number)?.toInt() ?: 20,
            contactPhone = doc.getString("contactPhone") ?: "+91 98765 43210",
            storeAddress = doc.getString("storeAddress") ?: "Plot 42, Gourmet Boulevard, Zayka Square",
            autoAcceptOrders = doc.getBoolean("autoAcceptOrders") ?: false,
            soundAlertsEnabled = doc.getBoolean("soundAlertsEnabled") ?: true
        )
    }
}
