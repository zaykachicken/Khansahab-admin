package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.ui.components.StaffRoleDialog
import com.example.ui.components.ZaykaTopHeader
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DriverFleetScreen
import com.example.ui.screens.MenuManagementScreen
import com.example.ui.screens.OffersScreen
import com.example.ui.screens.OrdersQueueScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.ZaykaAdminTheme
import com.example.ui.viewmodel.AdminViewModel
import kotlinx.coroutines.launch

enum class AdminNavScreen(val title: String, val icon: ImageVector) {
    DASHBOARD("Live Hub", Icons.Default.Dashboard),
    ORDERS("Orders", Icons.Default.ReceiptLong),
    FLEET("Dispatch", Icons.Default.DeliveryDining),
    MENU("Menu & Stock", Icons.Default.RestaurantMenu),
    ANALYTICS("Sales Report", Icons.Default.Assessment),
    OFFERS("Promos", Icons.Default.LocalOffer),
    SETTINGS("Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    private val viewModel: AdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZaykaAdminTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: AdminViewModel) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isTabletOrLandscape = configuration.screenWidthDp >= 700

    var currentScreen by remember { mutableStateOf(AdminNavScreen.DASHBOARD) }
    var showRoleDialog by remember { mutableStateOf(false) }

    val allOrders by viewModel.allOrders.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val pendingCount = allOrders.count { it.status == OrderStatus.PENDING }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        // Simulation removed
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            ZaykaTopHeader(
                isStoreOpen = settings?.isStoreOpen ?: true,
                isBusyMode = settings?.isBusyMode ?: false,
                pendingOrdersCount = pendingCount,
                currentUser = currentUser,
                onToggleStoreStatus = { isOpen ->
                    viewModel.updateStoreStatus(isOpen)
                    Toast.makeText(context, if (isOpen) "Store is now LIVE" else "Store is now PAUSED", Toast.LENGTH_SHORT).show()
                },
                onToggleBusyMode = { isBusy ->
                    viewModel.toggleBusyMode(isBusy)
                    Toast.makeText(context, if (isBusy) "Rush mode ACTIVE" else "Rush mode DEACTIVATED", Toast.LENGTH_SHORT).show()
                },
                onSimulateOrder = {
                    // Simulation removed
                },
                onOpenRoleDialog = {
                    showRoleDialog = true
                }
            )
        },
        bottomBar = {
            if (!isTabletOrLandscape) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.navigationBarsPadding().testTag("admin_bottom_navigation")
                ) {
                    AdminNavScreen.values().forEach { screen ->
                        val isSelected = currentScreen == screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentScreen = screen },
                            icon = {
                                if (screen == AdminNavScreen.ORDERS && pendingCount > 0) {
                                    BadgedBox(badge = { Badge { Text("$pendingCount") } }) {
                                        Icon(screen.icon, contentDescription = screen.title)
                                    }
                                } else {
                                    Icon(screen.icon, contentDescription = screen.title)
                                }
                            },
                            label = { Text(screen.title, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                            ),
                            modifier = Modifier.testTag("nav_item_${screen.name}")
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Row(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (isTabletOrLandscape) {
                NavigationRail(containerColor = MaterialTheme.colorScheme.surface) {
                    AdminNavScreen.values().forEach { screen ->
                        val isSelected = currentScreen == screen
                        NavigationRailItem(
                            selected = isSelected,
                            onClick = { currentScreen = screen },
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title, fontSize = 10.sp) }
                        )
                    }
                }
            }
            Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                when (currentScreen) {
                    AdminNavScreen.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToOrders = { currentScreen = AdminNavScreen.ORDERS }
                    )
                    AdminNavScreen.ORDERS -> OrdersQueueScreen(viewModel = viewModel)
                    AdminNavScreen.FLEET -> DriverFleetScreen(viewModel = viewModel)
                    AdminNavScreen.MENU -> MenuManagementScreen(viewModel = viewModel)
                    AdminNavScreen.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
                    AdminNavScreen.OFFERS -> OffersScreen(viewModel = viewModel)
                    AdminNavScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                }
            }
        }
    }

    if (showRoleDialog) {
        StaffRoleDialog(
            currentUser = currentUser,
            onDismiss = { showRoleDialog = false },
            onRoleSelected = { newRole ->
                viewModel.authManager.switchStaffRole(newRole)
                Toast.makeText(context, "Switched workspace role to: ${newRole.name}", Toast.LENGTH_SHORT).show()
            },
            onGoogleSignIn = {
                coroutineScope.launch {
                    val webClientId = "701095229515-kccnoijdjfii36ljdhfd35ellllpi6p0.apps.googleusercontent.com"
                    val result = viewModel.authManager.signInWithGoogle(webClientId)
                    val user = result.getOrNull()
                    if (user != null) {
                        Toast.makeText(context, "Signed in as: ${user.name}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Google Sign-In: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }
}
