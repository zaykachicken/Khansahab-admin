package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Coupon
import com.example.data.model.Driver
import com.example.data.model.DriverStatus
import com.example.data.model.MenuItem
import com.example.data.model.RestaurantSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuItemDao {
    @Query("SELECT * FROM menu_items ORDER BY category ASC, name ASC")
    fun getAllMenuItems(): Flow<List<MenuItem>>

    @Query("SELECT * FROM menu_items WHERE category = :category ORDER BY name ASC")
    fun getMenuItemsByCategory(category: String): Flow<List<MenuItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItem(item: MenuItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItems(items: List<MenuItem>)

    @Update
    suspend fun updateMenuItem(item: MenuItem)

    @Query("UPDATE menu_items SET isAvailable = :isAvailable WHERE id = :id")
    suspend fun toggleItemAvailability(id: String, isAvailable: Boolean)

    @Query("DELETE FROM menu_items WHERE id = :id")
    suspend fun deleteMenuItem(id: String)
}

@Dao
interface DriverDao {
    @Query("SELECT * FROM drivers ORDER BY name ASC")
    fun getAllDrivers(): Flow<List<Driver>>

    @Query("SELECT * FROM drivers WHERE status = :status")
    fun getDriversByStatus(status: DriverStatus): Flow<List<Driver>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDriver(driver: Driver)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrivers(drivers: List<Driver>)

    @Update
    suspend fun updateDriver(driver: Driver)

    @Query("UPDATE drivers SET status = :status, activeOrderId = :activeOrderId WHERE id = :driverId")
    suspend fun updateDriverStatus(driverId: String, status: DriverStatus, activeOrderId: String?)

    @Query("UPDATE drivers SET currentLatitude = :lat, currentLongitude = :lng WHERE id = :driverId")
    suspend fun updateDriverLocation(driverId: String, lat: Double, lng: Double)

    @Query("DELETE FROM drivers WHERE id = :driverId")
    suspend fun deleteDriver(driverId: String)
}

@Dao
interface CouponDao {
    @Query("SELECT * FROM coupons ORDER BY code ASC")
    fun getAllCoupons(): Flow<List<Coupon>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupon(coupon: Coupon)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupons(coupons: List<Coupon>)

    @Update
    suspend fun updateCoupon(coupon: Coupon)

    @Query("UPDATE coupons SET isActive = :isActive WHERE id = :id")
    suspend fun toggleCouponStatus(id: String, isActive: Boolean)

    @Query("DELETE FROM coupons WHERE id = :id")
    suspend fun deleteCoupon(id: String)
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM restaurant_settings WHERE id = 1")
    fun getSettings(): Flow<RestaurantSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: RestaurantSettings)
}
