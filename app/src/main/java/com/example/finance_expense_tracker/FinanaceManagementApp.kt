package com.example.finance_expense_tracker

import BudgetedCategoriesScreen
import MainLayout
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FinanceManagementApp(context: Context, categoryToEdit: String?,amount:Double,isIncome:Boolean) {
    val viewModel = remember { ExpenseRecordsViewModel(context) }
    val navController = rememberNavController()

    var recordToEdit by remember { mutableStateOf<ExpenseRecordEntity?>(null) }
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
                        budgetedCategories = budgetedCategories,
                        onAddBudgetCategory = { budgetedCategory ->
                            budgetedCategories.add(budgetedCategory)
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
                    val expenseRecords by viewModel.expenseRecords.collectAsState()
                    BudgetedCategoriesScreen(
                        navController = navController,
                        expenseRecordsBudgeted = expenseRecords,
                        onBack = { navController.popBackStack() },
                        viewModel = viewModel,
                        categoryToEdit = categoryToEdit // Pass categoryToEdit here
                    )
                }
            }
        }
        composable("addExpense") { backStackEntry ->
            CrossfadeScreen(backStackEntry.id) {
                val expenseRecords by viewModel.expenseRecords.collectAsState()
                AddIncomeOrExpense(
                    notificationRecord=ExpenseRecordEntity(
                        amount = amount,
                        isIncome = isIncome,
                        category = if(isIncome) "Credit" else "Debit"
                    ),
                    initialRecord = recordToEdit,
                    onCancel = { navController.popBackStack() },
                    onSave = { newRecord ->
                        recordToEdit?.let { oldRecord ->
                            viewModel.deleteExpenseRecord(oldRecord.id)
                        }
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
                    DebtsScreen(onBack = { navController.popBackStack() }, viewModel)
                }
            }
        }
        composable("analysis") { backStackEntry ->
            MainLayout(navController = navController, onViewFilterClick = { navController.navigate("filter") }) {
                CrossfadeScreen(backStackEntry.id) {
                    val expenseRecords by viewModel.expenseRecords.collectAsState()
                    MyApp(
                        expenseRecords = expenseRecords,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
        composable("filter") {
            val expenseRecords by viewModel.expenseRecords.collectAsState()
            var expenseRecordsAdd = expenseRecords
            SearchExpenseScreen(
                expenseRecords = expenseRecords,
                onBack = { navController.popBackStack() },
                onEdit = { record ->
                    recordToEdit = record
                    navController.navigate("addExpense")
                },
                onDelete = {newid->
                    expenseRecordsAdd = expenseRecordsAdd.filter { it.id!=newid }.toMutableList()
                }
            )
        }
        composable("settings") {
            SettingsScreen()
        }
        composable("deleteReset") {
        }
        composable("help") {
            HelpScreen()
        }
        composable("addCategories") {
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