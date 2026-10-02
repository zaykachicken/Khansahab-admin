package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Coupon
import com.example.data.model.Driver
import com.example.data.model.DriverStatus
import com.example.data.model.MenuItem
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.RestaurantSettings
import com.example.data.model.StaffRole
import com.example.data.model.StaffUser
import com.example.data.remote.FirebaseAuthManager
import com.example.data.repository.RestaurantRepository
import com.example.data.repository.SalesAnalytics
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val repository = RestaurantRepository(db)
    val authManager = FirebaseAuthManager(application)

    val currentUser: StateFlow<StaffUser?> = authManager.currentUser

    // Orders
    val allOrders = repository.allOrders.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _selectedStatusTab = MutableStateFlow<OrderStatus?>(null) // null = ALL
    val selectedStatusTab: StateFlow<OrderStatus?> = _selectedStatusTab.asStateFlow()

    private val _orderSearchQuery = MutableStateFlow("")
    val orderSearchQuery: StateFlow<String> = _orderSearchQuery.asStateFlow()

    val filteredOrders: StateFlow<List<Order>> = combine(
        allOrders,
        _selectedStatusTab,
        _orderSearchQuery
    ) { orders, statusFilter, query ->
        orders.filter { order ->
            val matchesStatus = statusFilter == null || order.status == statusFilter
            val matchesQuery = query.isBlank() ||
                    order.id.contains(query, ignoreCase = true) ||
                    order.customerName.contains(query, ignoreCase = true) ||
                    order.customerPhone.contains(query, ignoreCase = true) ||
                    order.items.any { it.name.contains(query, ignoreCase = true) }
            matchesStatus && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Menu
    val allMenuItems = repository.allMenuItems.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _selectedMenuCategory = MutableStateFlow("All")
    val selectedMenuCategory: StateFlow<String> = _selectedMenuCategory.asStateFlow()

    val filteredMenuItems: StateFlow<List<MenuItem>> = combine(
        allMenuItems,
        _selectedMenuCategory
    ) { items, cat ->
        if (cat == "All") items else items.filter { it.category == cat }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Drivers
    val allDrivers = repository.allDrivers.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Coupons
    val allCoupons = repository.allCoupons.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Restaurant Settings
    val settings = repository.restaurantSettings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        RestaurantSettings()
    )

    // Sales Analytics
    val analytics: StateFlow<SalesAnalytics> = allOrders.combine(settings) { orders, _ ->
        repository.calculateAnalytics(orders)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SalesAnalytics())

    fun setStatusFilter(status: OrderStatus?) {
        _selectedStatusTab.value = status
    }

    fun setOrderSearchQuery(query: String) {
        _orderSearchQuery.value = query
    }

    fun setMenuCategory(cat: String) {
        _selectedMenuCategory.value = cat
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus)
            triggerHapticFeedback()
        }
    }

    fun assignDriver(orderId: String, driverId: String, driverName: String) {
        viewModelScope.launch {
            repository.assignDriver(orderId, driverId, driverName)
            triggerHapticFeedback()
        }
    }

    fun toggleItemStock(itemId: String, isAvailable: Boolean) {
        viewModelScope.launch {
            repository.toggleItemAvailability(itemId, isAvailable)
        }
    }

    fun saveMenuItem(item: MenuItem) {
        viewModelScope.launch {
            repository.saveMenuItem(item)
        }
    }

    fun deleteMenuItem(itemId: String) {
        viewModelScope.launch {
            repository.deleteMenuItem(itemId)
        }
    }

    fun saveDriver(driver: Driver) {
        viewModelScope.launch {
            repository.saveDriver(driver)
        }
    }

    fun deleteDriver(driverId: String) {
        viewModelScope.launch {
            repository.deleteDriver(driverId)
        }
    }

    fun updateDriverStatus(driverId: String, status: DriverStatus) {
        viewModelScope.launch {
            repository.updateDriverStatus(driverId, status)
        }
    }

    fun saveCoupon(coupon: Coupon) {
        viewModelScope.launch {
            repository.saveCoupon(coupon)
        }
    }

    fun toggleCoupon(couponId: String, isActive: Boolean) {
        viewModelScope.launch {
            repository.toggleCoupon(couponId, isActive)
        }
    }

    fun updateStoreStatus(isOpen: Boolean) {
        val current = settings.value ?: RestaurantSettings()
        viewModelScope.launch {
            repository.saveSettings(current.copy(isStoreOpen = isOpen))
        }
    }

    fun toggleBusyMode(isBusy: Boolean) {
        val current = settings.value ?: RestaurantSettings()
        viewModelScope.launch {
            repository.saveSettings(current.copy(isBusyMode = isBusy))
        }
    }

    fun saveSettings(newSettings: RestaurantSettings) {
        viewModelScope.launch {
            repository.saveSettings(newSettings)
        }
    }

    val isSyncing: StateFlow<Boolean> = repository.isSyncing
    val lastSyncTime: StateFlow<Long> = repository.lastSyncTime
    val isCloudConnected: Boolean
        get() = repository.firestoreSync.isConnected

    fun syncAllToCloud(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.syncFullDatabaseToCloud()
            if (result.isSuccess) {
                triggerHapticFeedback()
                onResult(true, result.getOrNull() ?: "Sync complete")
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Sync failed")
            }
        }
    }

    fun syncMenuToCloud(onComplete: (Int) -> Unit) {
        viewModelScope.launch {
            val count = repository.syncAllMenuToCloud()
            triggerHapticFeedback()
            onComplete(count)
        }
    }

    fun syncCouponsToCloud(onComplete: (Int) -> Unit) {
        viewModelScope.launch {
            val count = repository.syncAllCouponsToCloud()
            triggerHapticFeedback()
            onComplete(count)
        }
    }

    fun syncDriversToCloud(onComplete: (Int) -> Unit) {
        viewModelScope.launch {
            val count = repository.syncAllDriversToCloud()
            triggerHapticFeedback()
            onComplete(count)
        }
    }

    private fun triggerHapticFeedback() {
        try {
            val context = getApplication<Application>()
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                manager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(50)
            }
        } catch (e: Exception) {
            // Ignore
        }
    }
}
