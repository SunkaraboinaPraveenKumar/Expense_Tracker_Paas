package com.example.financemanagementapp

import TabLayout
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.LocalDate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExpenseTrackerScreen(
    navController: NavController,
    onAddExpenseClick: () -> Unit,
    expenseRecords: List<ExpenseRecordEntity>,
    onViewRecordsClick: () -> Unit,
    onSetBudgetClick: () -> Unit,
    onViewDebtsClick: () -> Unit,
    onViewAnalysisClick: () -> Unit
) {
    val viewModel: ExpenseRecordsViewModel = remember {
        ExpenseRecordsViewModel(navController.context)
    }

    val recordsState = viewModel.expenseRecords.collectAsState()

    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var currentFilterOption by remember { mutableStateOf(FilterOption.MONTHLY) }

    // Define date range based on filter option
    val dateRange = remember(currentDate, currentFilterOption) {
        when (currentFilterOption) {
            FilterOption.DAILY -> currentDate to currentDate
            FilterOption.WEEKLY -> {
                val startOfWeek = currentDate.with(java.time.DayOfWeek.MONDAY)
                startOfWeek to startOfWeek.plusDays(6)
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
        val tabs = listOf("Daily", "Monthly", "Calendar", "Notes")
        var selectedTabIndex by remember { mutableStateOf(0) }
        TabLayout(tabs, selectedTabIndex) { index ->
            selectedTabIndex = index
        }

        // Income and expense overview
        CurrentMonthCard(
            currentFilterOption = currentFilterOption,
            dateRange = dateRange,
            incomeRecords = filteredIncomeRecords,
            expenseRecords = filteredExpenseRecords
        )

        // Action buttons
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onSetBudgetClick,
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(56.dp)
            ) {
                Text(text = "Set Budget", fontSize = 18.sp)
            }
            Button(
                onClick = onViewRecordsClick,
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(56.dp)
            ) {
                Text(text = "View Records", fontSize = 18.sp)
            }
            Button(
                onClick = onViewDebtsClick,
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(56.dp)
            ) {
                Text(text = "View Debts", fontSize = 18.sp)
            }
            Button(
                onClick = onViewAnalysisClick,
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(56.dp)
            ) {
                Text(text = "View Analysis", fontSize = 18.sp)
            }
        }

        // Floating action button
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .fillMaxWidth()
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
}

