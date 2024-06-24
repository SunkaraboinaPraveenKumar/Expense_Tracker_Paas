import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.finance_expense_tracker.BottomNavBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(
    navController: NavController,
    onViewFilterClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedColor = if (isSystemInDarkTheme()) Color.Gray else Color.White
    // Determine selected tab index based on the current destination
    val selectedBottomTabIndex = remember(navBackStackEntry?.destination?.route) {
        when (navBackStackEntry?.destination?.route) {
            "expenseTracker" -> 0
            "viewRecords" -> 1
            "analysis" -> 2
            "setBudget", "budgetedCategories" -> 3
            else -> null // No tab selected for unrecognized routes
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.75f) // Adjust width as needed
                    .background(Color.LightGray)
                    .padding(16.dp)
            ) {
                // Drawer content here
                SettingsMenu(
                    onSettingsClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("settings")
                    },
                    onDeleteResetClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("deleteReset")
                    },
                    onHelpClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("help")
                    },
                    onAddNewCategoriesClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("addCategories")
                    }
                )
            }
        },
        content = {
            Scaffold(
                modifier = Modifier.background(Color.LightGray), // Set background color for Scaffold
                topBar = {
                    TopAppBar(
                        title = { Text("Finance Management App", fontSize = 20.sp) },
                        navigationIcon = {
                            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            IconButton(onClick = onViewFilterClick) {
                                Icon(Icons.Filled.Search, contentDescription = "Search")
                            }
                        }
                    )
                },
                bottomBar = {
                    if (selectedBottomTabIndex != null) {
                        Surface(
                            color = Color.Gray
                        ) {
                            BottomNavBar(
                                selectedTabIndex = selectedBottomTabIndex,
                                onTabSelected = { index ->
                                    if (selectedBottomTabIndex != index) {
                                        val route = when (index) {
                                            0 -> "expenseTracker"
                                            1 -> "viewRecords"
                                            2 -> "analysis"
                                            3 -> "setBudget"
                                            else -> "expenseTracker"
                                        }
                                        navController.navigate(route) {
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                },
                content = { paddingValues ->
                    Box(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                            .background(selectedColor) // Set background color for content Box
                    ) {
                        content()
                    }
                }
            )
        }
    )
}
