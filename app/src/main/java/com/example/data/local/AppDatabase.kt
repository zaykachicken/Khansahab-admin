package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Order::class,
        MenuItem::class,
        Driver::class,
        Coupon::class,
        RestaurantSettings::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
    abstract fun menuItemDao(): MenuItemDao
    abstract fun driverDao(): DriverDao
    abstract fun couponDao(): CouponDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "zayka_admin_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                populateInitialData(getInstance(context))
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateInitialData(db: AppDatabase) {
            // Seed Restaurant Settings
            db.settingsDao().saveSettings(
                RestaurantSettings(
                    id = 1,
                    restaurantName = "ZaykaChicken Cafe & Restaurant",
                    tagline = "Flame-Grilled Perfection & Royal Dum Biryani",
                    isStoreOpen = true,
                    isBusyMode = false,
                    deliveryRadiusKm = 10.0,
                    minOrderValue = 149.0,
                    baseDeliveryFee = 40.0,
                    packagingCharge = 25.0,
                    gstPercentage = 5.0,
                    prepTimeBufferMinutes = 20,
                    contactPhone = "+91 98765 43210",
                    storeAddress = "Plot 42, Gourmet Boulevard, Zayka Square, Delhi NCR",
                    autoAcceptOrders = false,
                    soundAlertsEnabled = true
                )
            )

            // Seed Authentic Zayka Menu Items
            val initialMenu = listOf(
                MenuItem(
                    id = "M01",
                    name = "Zayka Royal Chicken Dum Biryani",
                    category = "Biryani Specials",
                    description = "Aromatic slow-cooked long grain basmati rice layered with succulent marinated chicken, saffron, and authentic hand-ground Awadhi spices. Served with creamy mint raita and salan.",
                    price = 349.0,
                    discountedPrice = 299.0,
                    imageUrl = "",
                    isVeg = false,
                    isAvailable = true,
                    preparationTimeMinutes = 20,
                    spiceOptions = listOf("Mild", "Medium", "Spicy"),
                    availableAddOns = listOf("Extra Boiled Egg (₹25)", "Double Chicken Piece (₹90)", "Extra Raita (₹30)"),
                    totalOrdersCount = 428,
                    rating = 4.9f
                ),
                MenuItem(
                    id = "M02",
                    name = "Zayka Charcoal Tandoori Murgh (Full)",
                    category = "Tandoori & Charcoal Grills",
                    description = "Full spring chicken marinated overnight in Kashmiri red chili, hung curd, and roasted garam masala, crisped over open charcoal tandoor. Served with green chutney and spiced onion salad.",
                    price = 480.0,
                    discountedPrice = 440.0,
                    imageUrl = "",
                    isVeg = false,
                    isAvailable = true,
                    preparationTimeMinutes = 25,
                    spiceOptions = listOf("Medium", "Extra Spicy"),
                    availableAddOns = listOf("Extra Butter Brush (₹20)", "Roomali Roti 2 pcs (₹40)"),
                    totalOrdersCount = 310,
                    rating = 4.9f
                ),
                MenuItem(
                    id = "M03",
                    name = "Butter Chicken Delhi Style (Boneless)",
                    category = "Rich Curries & Gravies",
                    description = "Smoked shredded tandoori chicken simmered in a velvety, buttery tomato cashew gravy with fenugreek leaves (kasoori methi) and fresh cream.",
                    price = 380.0,
                    discountedPrice = null,
                    imageUrl = "",
                    isVeg = false,
                    isAvailable = true,
                    preparationTimeMinutes = 18,
                    spiceOptions = listOf("Mild", "Medium"),
                    availableAddOns = listOf("Garlic Butter Naan (₹50)", "Extra Cream Swirl (₹25)"),
                    totalOrdersCount = 390,
                    rating = 4.8f
                ),
                MenuItem(
                    id = "M04",
                    name = "Zayka Fiery Chicken Wings (8 Pcs)",
                    category = "Starters & Wings",
                    description = "Juicy jumbo wings tossed in Zayka's signature fiery chili garlic glaze, garnished with toasted sesame seeds and fresh scallions.",
                    price = 260.0,
                    discountedPrice = 239.0,
                    imageUrl = "",
                    isVeg = false,
                    isAvailable = true,
                    preparationTimeMinutes = 15,
                    spiceOptions = listOf("Spicy", "Ghost Pepper Hot"),
                    availableAddOns = listOf("Garlic Mayo Dip (₹30)", "Chili Cheese Dip (₹35)"),
                    totalOrdersCount = 280,
                    rating = 4.7f
                ),
                MenuItem(
                    id = "M05",
                    name = "Afghani Malai Murgh Tikka (6 Pcs)",
                    category = "Tandoori & Charcoal Grills",
                    description = "Melt-in-mouth chicken chunks steeped in cashew paste, cheese, cardamom, and fresh malai, roasted with gentle char.",
                    price = 320.0,
                    discountedPrice = null,
                    imageUrl = "",
                    isVeg = false,
                    isAvailable = true,
                    preparationTimeMinutes = 20,
                    spiceOptions = listOf("Mild", "Medium"),
                    availableAddOns = listOf("Mint Chutney (₹20)", "Butter Naan (₹45)"),
                    totalOrdersCount = 215,
                    rating = 4.8f
                ),
                MenuItem(
                    id = "M06",
                    name = "Mutton Galouti Kebab (4 Pcs)",
                    category = "Starters & Wings",
                    description = "Legendary Lucknowi tender spiced minced mutton patties that dissolve on the tongue. Served atop mini Mughlai parathas.",
                    price = 390.0,
                    discountedPrice = 360.0,
                    imageUrl = "",
                    isVeg = false,
                    isAvailable = true,
                    preparationTimeMinutes = 22,
                    spiceOptions = listOf("Medium"),
                    availableAddOns = listOf("Mughlai Paratha 2 pcs (₹50)"),
                    totalOrdersCount = 180,
                    rating = 4.9f
                ),
                MenuItem(
                    id = "M07",
                    name = "Paneer Tikka Charcoal Masala",
                    category = "Rich Curries & Gravies",
                    description = "Char-grilled fresh cottage cheese cubes in an aromatic onion tomato bell pepper gravy. A vegetarian favorite.",
                    price = 310.0,
                    discountedPrice = null,
                    imageUrl = "",
                    isVeg = true,
                    isAvailable = true,
                    preparationTimeMinutes = 15,
                    spiceOptions = listOf("Mild", "Medium", "Spicy"),
                    availableAddOns = listOf("Tandoori Roti (₹20)", "Butter Naan (₹45)"),
                    totalOrdersCount = 195,
                    rating = 4.6f
                ),
                MenuItem(
                    id = "M08",
                    name = "Zayka Feast Combo for 4",
                    category = "Family Combos & Platters",
                    description = "1 Full Tandoori Murgh + 2 Chicken Biryanis + 1 Butter Chicken + 4 Butter Naans + 1.25L Cold Beverage + 4 Gulab Jamuns.",
                    price = 1299.0,
                    discountedPrice = 1099.0,
                    imageUrl = "",
                    isVeg = false,
                    isAvailable = true,
                    preparationTimeMinutes = 30,
                    spiceOptions = listOf("Medium"),
                    availableAddOns = listOf("Extra Biryani Salan (₹40)"),
                    totalOrdersCount = 145,
                    rating = 5.0f
                ),
                MenuItem(
                    id = "M09",
                    name = "Garlic Butter Naan (2 Pcs)",
                    category = "Breads & Rice",
                    description = "Refined flour bread infused with fresh minced garlic and coriander, baked in tandoor and brushed generously with butter.",
                    price = 90.0,
                    discountedPrice = null,
                    imageUrl = "",
                    isVeg = true,
                    isAvailable = true,
                    preparationTimeMinutes = 8,
                    spiceOptions = listOf("Mild"),
                    availableAddOns = emptyList(),
                    totalOrdersCount = 520,
                    rating = 4.9f
                ),
                MenuItem(
                    id = "M10",
                    name = "Royal Shahi Tukda with Rabri",
                    category = "Beverages & Desserts",
                    description = "Crispy golden ghee fried bread soaked in saffron sugar syrup, topped with thick condensed cardamom rabri and sliced pistachios.",
                    price = 160.0,
                    discountedPrice = 140.0,
                    imageUrl = "",
                    isVeg = true,
                    isAvailable = true,
                    preparationTimeMinutes = 10,
                    spiceOptions = listOf("Mild"),
                    availableAddOns = emptyList(),
                    totalOrdersCount = 260,
                    rating = 4.9f
                )
            )
            db.menuItemDao().insertMenuItems(initialMenu)

            // Seed Drivers / Delivery Fleet
            val initialDrivers = listOf(
                Driver(
                    id = "DRV-101",
                    name = "Rajesh Sharma",
                    phone = "+91 98112 34567",
                    vehicleNumber = "DL 4S BR 9912",
                    status = DriverStatus.ON_DELIVERY,
                    activeOrderId = "ZK-9102",
                    completedTodayCount = 8,
                    rating = 4.9f,
                    currentLatitude = 28.6180,
                    currentLongitude = 77.2140,
                    batteryPercent = 88
                ),
                Driver(
                    id = "DRV-102",
                    name = "Mohd. Sameer",
                    phone = "+91 98731 87654",
                    vehicleNumber = "DL 8C MK 4210",
                    status = DriverStatus.AVAILABLE,
                    activeOrderId = null,
                    completedTodayCount = 11,
                    rating = 4.95f,
                    currentLatitude = 28.6145,
                    currentLongitude = 77.2095,
                    batteryPercent = 94
                ),
                Driver(
                    id = "DRV-103",
                    name = "Amit Kumar Verma",
                    phone = "+91 99580 12399",
                    vehicleNumber = "DL 3S QP 7741",
                    status = DriverStatus.AVAILABLE,
                    activeOrderId = null,
                    completedTodayCount = 6,
                    rating = 4.8f,
                    currentLatitude = 28.6110,
                    currentLongitude = 77.2050,
                    batteryPercent = 76
                ),
                Driver(
                    id = "DRV-104",
                    name = "Gurpreet Singh",
                    phone = "+91 98105 66723",
                    vehicleNumber = "DL 9S GT 3302",
                    status = DriverStatus.ON_DELIVERY,
                    activeOrderId = "ZK-9101",
                    completedTodayCount = 9,
                    rating = 4.9f,
                    currentLatitude = 28.6250,
                    currentLongitude = 77.2210,
                    batteryPercent = 65
                ),
                Driver(
                    id = "DRV-105",
                    name = "Vikram Patil",
                    phone = "+91 97110 44882",
                    vehicleNumber = "DL 1N TR 8819",
                    status = DriverStatus.OFFLINE,
                    activeOrderId = null,
                    completedTodayCount = 4,
                    rating = 4.7f,
                    currentLatitude = 28.6080,
                    currentLongitude = 77.1990,
                    batteryPercent = 42
                )
            )
            db.driverDao().insertDrivers(initialDrivers)

            // Seed Live Orders across statuses
            val now = System.currentTimeMillis()
            val initialOrders = listOf(
                Order(
                    id = "ZK-9105",
                    customerName = "Aryan Malhotra",
                    customerPhone = "+91 98711 22334",
                    deliveryAddress = "Flat 402, Pinnacle Heights, Sector 15, City Center",
                    orderType = OrderType.DELIVERY,
                    status = OrderStatus.PENDING,
                    items = listOf(
                        OrderItem(itemId = "M01", name = "Zayka Royal Chicken Dum Biryani", price = 299.0, quantity = 2, isVeg = false, addOns = listOf("Extra Raita (₹30)"), spiceLevel = "Spicy"),
                        OrderItem(itemId = "M04", name = "Zayka Fiery Chicken Wings (8 Pcs)", price = 239.0, quantity = 1, isVeg = false, addOns = listOf("Garlic Mayo Dip (₹30)"), spiceLevel = "Spicy")
                    ),
                    subtotal = 897.0,
                    tax = 44.85,
                    deliveryFee = 40.0,
                    packagingFee = 25.0,
                    discount = 50.0,
                    totalAmount = 956.85,
                    paymentMethod = PaymentMethod.ONLINE_UPI,
                    paymentStatus = PaymentStatus.PAID,
                    createdAt = now - (3 * 60 * 1000), // 3 mins ago
                    estimatedDeliveryMinutes = 35,
                    instructions = "Please ring doorbell twice, keep it spicy!"
                ),
                Order(
                    id = "ZK-9104",
                    customerName = "Pooja Sharma",
                    customerPhone = "+91 99100 88221",
                    deliveryAddress = "Villa 12, Palm Meadows, Main Ring Road",
                    orderType = OrderType.DELIVERY,
                    status = OrderStatus.PREPARING,
                    items = listOf(
                        OrderItem(itemId = "M02", name = "Zayka Charcoal Tandoori Murgh (Full)", price = 440.0, quantity = 1, isVeg = false, addOns = listOf("Roomali Roti 2 pcs (₹40)"), spiceLevel = "Medium"),
                        OrderItem(itemId = "M03", name = "Butter Chicken Delhi Style (Boneless)", price = 380.0, quantity = 1, isVeg = false, addOns = listOf("Garlic Butter Naan (₹50)"), spiceLevel = "Mild"),
                        OrderItem(itemId = "M09", name = "Garlic Butter Naan (2 Pcs)", price = 90.0, quantity = 2, isVeg = true)
                    ),
                    subtotal = 1050.0,
                    tax = 52.5,
                    deliveryFee = 40.0,
                    packagingFee = 25.0,
                    discount = 100.0,
                    totalAmount = 1067.5,
                    paymentMethod = PaymentMethod.ONLINE_UPI,
                    paymentStatus = PaymentStatus.PAID,
                    createdAt = now - (12 * 60 * 1000), // 12 mins ago
                    estimatedDeliveryMinutes = 30,
                    instructions = "Send extra green mint chutney please."
                ),
                Order(
                    id = "ZK-9103",
                    customerName = "Rohan Khurana",
                    customerPhone = "+91 98188 55443",
                    deliveryAddress = "Tower B - 1204, Cyber Green Tech Park",
                    orderType = OrderType.PICKUP,
                    status = OrderStatus.READY,
                    items = listOf(
                        OrderItem(itemId = "M06", name = "Mutton Galouti Kebab (4 Pcs)", price = 360.0, quantity = 1, isVeg = false, spiceLevel = "Medium"),
                        OrderItem(itemId = "M05", name = "Afghani Malai Murgh Tikka (6 Pcs)", price = 320.0, quantity = 1, isVeg = false, spiceLevel = "Mild")
                    ),
                    subtotal = 680.0,
                    tax = 34.0,
                    deliveryFee = 0.0,
                    packagingFee = 20.0,
                    discount = 0.0,
                    totalAmount = 734.0,
                    paymentMethod = PaymentMethod.ONLINE_UPI,
                    paymentStatus = PaymentStatus.PAID,
                    createdAt = now - (22 * 60 * 1000), // 22 mins ago
                    estimatedDeliveryMinutes = 5,
                    instructions = "Self pickup in 10 mins"
                ),
                Order(
                    id = "ZK-9102",
                    customerName = "Dr. Shweta Iyer",
                    customerPhone = "+91 97170 33491",
                    deliveryAddress = "House 89, Sector 21-A, Near Apollo Clinic",
                    orderType = OrderType.DELIVERY,
                    status = OrderStatus.OUT_FOR_DELIVERY,
                    items = listOf(
                        OrderItem(itemId = "M08", name = "Zayka Feast Combo for 4", price = 1099.0, quantity = 1, isVeg = false, spiceLevel = "Medium")
                    ),
                    subtotal = 1099.0,
                    tax = 54.95,
                    deliveryFee = 40.0,
                    packagingFee = 25.0,
                    discount = 150.0,
                    totalAmount = 1068.95,
                    paymentMethod = PaymentMethod.CREDIT_CARD,
                    paymentStatus = PaymentStatus.PAID,
                    assignedDriverId = "DRV-101",
                    assignedDriverName = "Rajesh Sharma",
                    createdAt = now - (35 * 60 * 1000), // 35 mins ago
                    estimatedDeliveryMinutes = 10,
                    instructions = "Call on arrival, do not honk."
                ),
                Order(
                    id = "ZK-9101",
                    customerName = "Karan Grover",
                    customerPhone = "+91 98990 11220",
                    deliveryAddress = "Flat 101, Oakwood Residency, Block C",
                    orderType = OrderType.DELIVERY,
                    status = OrderStatus.OUT_FOR_DELIVERY,
                    items = listOf(
                        OrderItem(itemId = "M01", name = "Zayka Royal Chicken Dum Biryani", price = 299.0, quantity = 1, isVeg = false, spiceLevel = "Medium"),
                        OrderItem(itemId = "M10", name = "Royal Shahi Tukda with Rabri", price = 140.0, quantity = 2, isVeg = true)
                    ),
                    subtotal = 579.0,
                    tax = 28.95,
                    deliveryFee = 40.0,
                    packagingFee = 25.0,
                    discount = 0.0,
                    totalAmount = 672.95,
                    paymentMethod = PaymentMethod.COD,
                    paymentStatus = PaymentStatus.PENDING,
                    assignedDriverId = "DRV-104",
                    assignedDriverName = "Gurpreet Singh",
                    createdAt = now - (42 * 60 * 1000),
                    estimatedDeliveryMinutes = 8,
                    instructions = "Keep exact change for COD ₹673"
                ),
                Order(
                    id = "ZK-9098",
                    customerName = "Neha Agarwal",
                    customerPhone = "+91 99881 77223",
                    deliveryAddress = "Plot 55, South City 1",
                    orderType = OrderType.DELIVERY,
                    status = OrderStatus.DELIVERED,
                    items = listOf(
                        OrderItem(itemId = "M03", name = "Butter Chicken Delhi Style (Boneless)", price = 380.0, quantity = 1, isVeg = false),
                        OrderItem(itemId = "M09", name = "Garlic Butter Naan (2 Pcs)", price = 90.0, quantity = 2, isVeg = true)
                    ),
                    subtotal = 560.0,
                    tax = 28.0,
                    deliveryFee = 40.0,
                    packagingFee = 25.0,
                    discount = 50.0,
                    totalAmount = 603.0,
                    paymentMethod = PaymentMethod.ONLINE_UPI,
                    paymentStatus = PaymentStatus.PAID,
                    assignedDriverId = "DRV-102",
                    assignedDriverName = "Mohd. Sameer",
                    createdAt = now - (95 * 60 * 1000),
                    estimatedDeliveryMinutes = 0
                )
            )
            db.orderDao().insertOrders(initialOrders)

            // Seed Coupons
            val initialCoupons = listOf(
                Coupon(
                    id = "C01",
                    code = "ZAYKA100",
                    title = "Flat ₹100 Off on Orders above ₹599",
                    description = "Celebrate royal feast with ₹100 instant discount on all Biryanis and Grills.",
                    discountType = "FLAT",
                    discountValue = 100.0,
                    minOrderAmount = 599.0,
                    maxDiscountAmount = 100.0,
                    isActive = true,
                    usageCount = 284
                ),
                Coupon(
                    id = "C02",
                    code = "FEAST20",
                    title = "20% Off up to ₹150",
                    description = "Valid on all family combos and bulk tandoor orders.",
                    discountType = "PERCENTAGE",
                    discountValue = 20.0,
                    minOrderAmount = 499.0,
                    maxDiscountAmount = 150.0,
                    isActive = true,
                    usageCount = 512
                ),
                Coupon(
                    id = "C03",
                    code = "BIRYANI50",
                    title = "₹50 Off on Royal Dum Biryani",
                    description = "Special daily lunchtime biryani treat.",
                    discountType = "FLAT",
                    discountValue = 50.0,
                    minOrderAmount = 299.0,
                    maxDiscountAmount = 50.0,
                    isActive = true,
                    usageCount = 390
                )
            )
            db.couponDao().insertCoupons(initialCoupons)
        }
    }
}
