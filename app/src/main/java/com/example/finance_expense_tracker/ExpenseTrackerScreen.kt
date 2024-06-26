package com.example.finance_expense_tracker

import SettingsViewModel
import TabLayout
import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.financemanagementapp.R
import java.time.LocalDate
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExpenseTrackerScreen(
    navController: NavController,
    onAddExpenseClick: () -> Unit,
    onViewRecordsClick: () -> Unit,
    onSetBudgetClick: () -> Unit,
    onViewDebtsClick: () -> Unit,
    onViewAnalysisClick: () -> Unit,
    settingsViewModel:SettingsViewModel,
    context: Context
) {
    val viewModel: ExpenseRecordsViewModel = remember {
        ExpenseRecordsViewModel(navController.context)
    }

    val recordsState = viewModel.expenseRecords.collectAsState()

    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var currentFilterOption by remember { mutableStateOf(FilterOption.MONTHLY) }
    var selectedTabIndex by remember { mutableStateOf(2) } // Default to Monthly tab
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }

    // Define date range based on filter option
    val dateRange = remember(currentDate, currentFilterOption) {
        when (currentFilterOption) {
            FilterOption.DAILY -> currentDate to currentDate
            FilterOption.WEEKLY -> {
                val startOfWeek = currentDate.with(java.time.DayOfWeek.MONDAY)
                val endOfWeek = startOfWeek.plusDays(6)
                startOfWeek to endOfWeek
            }
            FilterOption.MONTHLY -> {
                val startOfMonth = currentDate.withDayOfMonth(1)
                val endOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth())
                startOfMonth to endOfMonth
            }
        }
    }

    // Filter records based on date range and filter option
    val filteredIncomeRecords = remember(recordsState.value, currentFilterOption, dateRange) {
        recordsState.value.filter { record ->
            record.isIncome && record.dateTime?.toLocalDate() in dateRange.first..dateRange.second
        }
    }

    val filteredExpenseRecords = remember(recordsState.value, currentFilterOption, dateRange) {
        recordsState.value.filter { record ->
            !record.isIncome && record.dateTime?.toLocalDate() in dateRange.first..dateRange.second
        }
    }

    val currentIncome = filteredIncomeRecords.sumOf { it.amount }
    val currentExpense = filteredExpenseRecords.sumOf { it.amount }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // HeaderRecord component
            HeaderRecord(
                currentDate = currentDate,
                currentFilterOption = currentFilterOption,
                onPrevClick = {
                    currentDate = when (currentFilterOption) {
                        FilterOption.DAILY -> currentDate.minusDays(1)
                        FilterOption.WEEKLY -> currentDate.minusWeeks(1)
                        FilterOption.MONTHLY -> currentDate.minusMonths(1)
                    }
                },
                onNextClick = {
                    currentDate = when (currentFilterOption) {
                        FilterOption.DAILY -> currentDate.plusDays(1)
                        FilterOption.WEEKLY -> currentDate.plusWeeks(1)
                        FilterOption.MONTHLY -> currentDate.plusMonths(1)
                    }
                },
                onFilterOptionSelected = {
                    currentFilterOption = it
                }
            )

            // Tab layout
            val tabs = listOf("Daily", "Weekly", "Monthly", "Calendar")
            TabLayout(tabs, selectedTabIndex) { index ->
                selectedTabIndex = index
                currentFilterOption = when (index) {
                    0 -> FilterOption.DAILY
                    1 -> FilterOption.WEEKLY
                    2 -> FilterOption.MONTHLY
                    3 -> {
                        showDatePicker = true
                        currentFilterOption // Maintain current filter option
                    }
                    else -> currentFilterOption
                }
            }

            // Current month card
            val currencySymbol = settingsViewModel.getCurrencySymbol()
            CurrentMonthCard(
                currentFilterOption = currentFilterOption,
                dateRange = dateRange,
                incomeRecords = filteredIncomeRecords,
                expenseRecords = filteredExpenseRecords,
                currencySymbol = currencySymbol
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onViewDebtsClick,
                    modifier = Modifier
                        .fillMaxWidth(0.45f)
                        .height(56.dp)
                ) {
                    Text(text = "View Debts", fontSize = 18.sp)
                }
            }
        }

        // Floating action button
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            IconButton(
                onClick = onAddExpenseClick,
                modifier = Modifier
                    .size(64.dp)
                    .shadow(8.dp, CircleShape)
                    .background(Color(0xFFC37A5C), CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "Add Expense",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }

    if (showDatePicker) {
        showDatePickerDialog(context) { selectedDate ->
            currentDate = selectedDate
            currentFilterOption = FilterOption.DAILY
            showDatePicker = false
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun showDatePickerDialog(context: Context, onDateSelected: (LocalDate) -> Unit) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            onDateSelected(selectedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

@Composable
fun TabLayout(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabs.forEachIndexed { index, title ->
            TextButton(
                onClick = { onTabSelected(index) },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (selectedIndex == index) Color.Blue else Color.Black
                )
            ) {
                Text(text = title, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun DailyContent(incomeRecords: List<ExpenseRecordEntity>, expenseRecords: List<ExpenseRecordEntity>) {
    Text("Daily Content")
}

@Composable
fun WeeklyContent(incomeRecords: List<ExpenseRecordEntity>, expenseRecords: List<ExpenseRecordEntity>) {
    Text("Weekly Content")
}

@Composable
fun MonthlyContent(incomeRecords: List<ExpenseRecordEntity>, expenseRecords: List<ExpenseRecordEntity>) {
    Text("Monthly Content")
}

@Composable
fun CalendarContent() {
    Text("Calendar Content")
}