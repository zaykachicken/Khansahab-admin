package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MenuItem
import com.example.ui.components.AddEditMenuItemDialog
import com.example.ui.components.MenuItemCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.ZaykaAmber
import com.example.ui.theme.ZaykaCharcoalBg
import com.example.ui.theme.ZaykaEmerald
import com.example.ui.theme.ZaykaOrange
import com.example.ui.theme.ZaykaSurface
import com.example.ui.theme.ZaykaSurfaceBorder
import com.example.ui.theme.ZaykaSurfaceElevated
import com.example.ui.viewmodel.AdminViewModel

@Composable
fun MenuManagementScreen(
    viewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    val menuItems by viewModel.filteredMenuItems.collectAsState()
    val allItems by viewModel.allMenuItems.collectAsState()
    val selectedCategory by viewModel.selectedMenuCategory.collectAsState()

    var showAddDishDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<MenuItem?>(null) }

    val categories = listOf(
        "All",
        "Biryani Specials",
        "Tandoori & Charcoal Grills",
        "Rich Curries & Gravies",
        "Starters & Wings",
        "Family Combos & Platters",
        "Breads & Rice",
        "Beverages & Desserts"
    )

    val outOfStockCount = allItems.count { !it.isAvailable }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ZaykaCharcoalBg)
    ) {
        // Header Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ZaykaSurface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Menu & Kitchen Stock (${allItems.size} Items)",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (outOfStockCount > 0) "⚠️ $outOfStockCount item(s) currently marked 86'd (Sold Out)" else "All items available in kitchen",
                        color = if (outOfStockCount > 0) ZaykaAmber else ZaykaEmerald,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = { showAddDishDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ZaykaOrange),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_dish_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Dish", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Category Filter Scroll
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setMenuCategory(cat) },
                        label = {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ZaykaOrange,
                            selectedLabelColor = Color.White,
                            containerColor = ZaykaSurfaceElevated,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) ZaykaOrange else ZaykaSurfaceBorder
                        ),
                        modifier = Modifier.testTag("category_filter_$cat")
                    )
                }
            }
        }

        // Dishes List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(menuItems) { item ->
                MenuItemCard(
                    item = item,
                    onToggleStock = { isAvailable ->
                        viewModel.toggleItemStock(item.id, isAvailable)
                    },
                    onEditClick = {
                        itemToEdit = item
                    },
                    onDeleteClick = {
                        viewModel.deleteMenuItem(item.id)
                    }
                )
            }
        }
    }

    if (showAddDishDialog) {
        AddEditMenuItemDialog(
            itemToEdit = null,
            onDismiss = { showAddDishDialog = false },
            onSave = { newItem ->
                viewModel.saveMenuItem(newItem)
                showAddDishDialog = false
            }
        )
    }

    itemToEdit?.let { item ->
        AddEditMenuItemDialog(
            itemToEdit = item,
            onDismiss = { itemToEdit = null },
            onSave = { updatedItem ->
                viewModel.saveMenuItem(updatedItem)
                itemToEdit = null
            }
        )
    }
}
