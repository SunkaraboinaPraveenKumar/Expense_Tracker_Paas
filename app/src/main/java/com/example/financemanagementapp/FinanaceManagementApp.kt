package com.example.financemanagementapp

import BudgetedCategoriesScreen
import MainLayout
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

//private var expenseRecords by mutableStateOf(mutableListOf<ExpenseRecord>()) // Mutable state for expense records
//private var budgetedCategories by mutableStateOf(mutableListOf<BudgetedCategory>()) // Mutable state for budgeted categories


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FinanceManagementApp(context: Context) {
    val viewModel = remember { ExpenseRecordsViewModel(context) }
    val navController = rememberNavController()

    var recordToEdit by remember { mutableStateOf<ExpenseRecordEntity?>(null) }
//    var expenseRecords by remember { mutableStateOf(mutableListOf<ExpenseRecordEntity>()) }
    var budgetedCategories by remember { mutableStateOf(mutableListOf<BudgetedCategory>()) }

    NavHost(navController = navController, startDestination = "expenseTracker") {
        composable("expenseTracker") { backStackEntry ->
            MainLayout(navController = navController, onViewFilterClick = { navController.navigate("filter") }) {
                CrossfadeScreen(backStackEntry.id) {
                    val expenseRecords by viewModel.expenseRecords.collectAsState()
                    ExpenseTrackerScreen(
                        navController = navController,
                        onAddExpenseClick = {
                            recordToEdit = null
                            navController.navigate("addExpense")
                        },
                        expenseRecords = expenseRecords, // Pass the observed records
                        onViewRecordsClick = { navController.navigate("viewRecords") },
                        onSetBudgetClick = { navController.navigate("setBudget") },
                        onViewDebtsClick = { navController.navigate("debts") },
                        onViewAnalysisClick = { navController.navigate("analysis") }
                    )
                }
            }
        }
        composable("viewRecords") { backStackEntry ->
            MainLayout(navController = navController, onViewFilterClick = { navController.navigate("filter") }) {
                CrossfadeScreen(backStackEntry.id) {
                    val expenseRecords by viewModel.expenseRecords.collectAsState()
                    ViewRecordsScreen(
                        viewModel = viewModel,
                        onEdit = { record ->
                            recordToEdit = record
                            navController.navigate("addExpense")
                        },
                        onDelete = { recordId ->
                            viewModel.deleteExpenseRecord(recordId)
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
        composable("setBudget") { backStackEntry ->
            MainLayout(navController = navController, onViewFilterClick = { navController.navigate("filter") }) {
                CrossfadeScreen(backStackEntry.id) {
                    val expenseRecords by viewModel.expenseRecords.collectAsState()
                    SetBudgetCard(
                        onClose = { navController.popBackStack() },
                        onBackHome = { navController.popBackStack() },
                        expenseRecordsBudgeted = expenseRecords,
                        budgetedCategories = budgetedCategories, // Pass mutable list
                        onAddBudgetCategory = { budgetedCategory ->
                            budgetedCategories.add(budgetedCategory) // Directly add to mutable list
                        },
                        onViewBudgetedCategoriesClick = {
                            navController.navigate("budgetedCategories")
                        },
                        viewModel = viewModel
                    )
                }
            }
        }
        composable("budgetedCategories") { backStackEntry ->
            MainLayout(navController = navController, onViewFilterClick = { navController.navigate("filter") }) {
                CrossfadeScreen(backStackEntry.id) {
//                    val viewModel: ExpenseRecordsViewModel = viewModel()

                    // Collect expenseRecords and budgetedCategories as State
                    val expenseRecords by viewModel.expenseRecords.collectAsState()
//                    val budgetedCategories by viewModel.budgetedCategories.collectAsState()

                    BudgetedCategoriesScreen(
                        expenseRecordsBudgeted = expenseRecords,
                        onBack = { navController.popBackStack() },
                        viewModel = viewModel
                    )
                }
            }
        }

        composable("addExpense") { backStackEntry ->
            CrossfadeScreen(backStackEntry.id) {
                val expenseRecords by viewModel.expenseRecords.collectAsState()
//                var expenseRecordsAdd =expenseRecords
                AddIncomeOrExpense(
                    initialRecord = recordToEdit,
                    onCancel = { navController.popBackStack() },
                    onSave = { newRecord ->
                        recordToEdit?.let { oldRecord ->
                            // Remove the old record
                            viewModel.deleteExpenseRecord(oldRecord.id)
                            expenseRecords.toMutableList().removeIf { it.id == oldRecord.id }
                        }
                        // Add the new/updated record
                        viewModel.insertOrUpdateExpenseRecord(newRecord)
                        navController.popBackStack()
                    },
                    viewModel = viewModel
                )
            }
        }
        composable("debts") { backStackEntry ->
            MainLayout(navController = navController, onViewFilterClick = { navController.navigate("filter") }) {
                CrossfadeScreen(backStackEntry.id) {
                    DebtsScreen(onBack = { navController.popBackStack() },viewModel)
                }
            }
        }
        composable("analysis") { backStackEntry ->
            MainLayout(navController = navController, onViewFilterClick = { navController.navigate("filter") }) {
                CrossfadeScreen(backStackEntry.id) {
                    // Observe expenseRecords from ViewModel
                    val expenseRecords by viewModel.expenseRecords.collectAsState()

                    // Pass expenseRecords and handle back navigation in MyApp
                    MyApp(
                        expenseRecords = expenseRecords,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
        composable("filter") {
            val expenseRecords by viewModel.expenseRecords.collectAsState()
            var expenseRecordsAdd =expenseRecords
            SearchExpenseScreen(
                expenseRecords = expenseRecords,
                onBack = { navController.popBackStack() },
                onEdit = { record ->
                    recordToEdit = record
                    navController.navigate("addExpense")
                }
            ) { newid ->
                expenseRecordsAdd = expenseRecordsAdd.filter { it.id != newid }.toMutableList()
            }
        }
        composable("settings") {
            SettingsScreen()
        }
        composable("settings") {
            // Content for settings screen
            SettingsScreen()

        }
        composable("deleteReset") {
            // Content for delete/reset screen
        }
        composable("help") {
            // Content for help screen
        }
        composable("addCategories") {
            // Content for add new categories screen
        }
    }
}

@SuppressLint("UnusedCrossfadeTargetStateParameter")
@Composable
fun CrossfadeScreen(
    targetState: Any,
    content: @Composable () -> Unit
) {
    Crossfade(targetState = targetState, modifier = Modifier.fillMaxSize()) {
        content()
    }
}