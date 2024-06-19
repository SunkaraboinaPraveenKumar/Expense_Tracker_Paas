package com.example.financemanagementapp

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource

@Composable
fun BottomNavBar(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Expense Tracker") },
            label = { Text("Home") },
            selected = selectedTabIndex == 0,
            onClick = { onTabSelected(0) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Receipt, contentDescription = "View Records") },
            label = { Text("Records") },
            selected = selectedTabIndex == 1,
            onClick = { onTabSelected(1) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Analytics, contentDescription = "Analysis") },
            label = { Text("Analysis") },
            selected = selectedTabIndex == 2,
            onClick = { onTabSelected(2) }
        )
        NavigationBarItem(
//            icon = { Icon(Icons.Filled.Settings, contentDescription = "Set Budget") },
            icon = { Icon(painter = painterResource(id = R.drawable.ic_debts), contentDescription = "Budget")},
            label = { Text("Budget") },
            selected = selectedTabIndex == 3,
            onClick = { onTabSelected(3) }
        )
    }
}
