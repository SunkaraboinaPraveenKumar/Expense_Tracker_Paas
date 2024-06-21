package com.example.financemanagementapp
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnalysisScreen(
    analysisType: String,
    expenseRecords: List<ExpenseRecordEntity>,
    currentYearMonth: YearMonth,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    onBack: () -> Unit
) {
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var currentFilterOption by remember { mutableStateOf(FilterOption.MONTHLY) }

    // Define the start and end dates based on the filter option
    val startDate: LocalDate
    val endDate: LocalDate

    when (currentFilterOption) {
        FilterOption.DAILY -> {
            startDate = currentDate
            endDate = currentDate
        }
        FilterOption.WEEKLY -> {
            startDate = currentDate.with(java.time.DayOfWeek.MONDAY)
            endDate = startDate.plusDays(6)
        }
        FilterOption.MONTHLY -> {
            startDate = currentDate.withDayOfMonth(1)
            endDate = currentDate.withDayOfMonth(currentDate.lengthOfMonth())
        }
    }

    // Filter expense records based on the analysis type and the selected date range
    val filteredRecords = if (analysisType == "Income") {
        expenseRecords.filter { it.isIncome && !it.dateTime?.toLocalDate()?.isBefore(startDate)!! && !it.dateTime.toLocalDate().isAfter(endDate) }
    } else {
        expenseRecords.filter { !it.isIncome && !it.dateTime?.toLocalDate()?.isBefore(startDate)!! && !it.dateTime.toLocalDate().isAfter(endDate) }
    }

    val groupedData = filteredRecords.groupBy({ it.category }, { it.amount.toFloat() })
    val data = groupedData.map { (category, amounts) ->
        category to amounts.sum()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
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

        CustomPieChart(
            data = data,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            analysisType = analysisType,
            onBack = onBack
        )
    }
}
@Composable
fun MainScreen(onIncomeClick: () -> Unit, onExpenseClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = onIncomeClick, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text(text = "Income Analysis")
        }
        Button(onClick = onExpenseClick, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text(text = "Expense Analysis")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyApp(expenseRecords: List<ExpenseRecordEntity>, onBack: () -> Unit) {
    var currentScreen by remember { mutableStateOf("main") }
    var analysisType by remember { mutableStateOf("") }
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }

    when (currentScreen) {
        "main" -> MainScreen(
            onIncomeClick = {
                analysisType = "Income"
                currentScreen = "analysis"
            },
            onExpenseClick = {
                analysisType = "Expense"
                currentScreen = "analysis"
            }
        )
        "analysis" -> AnalysisScreen(
            analysisType = analysisType,
            expenseRecords = expenseRecords,
            currentYearMonth = currentYearMonth,
            onPrevClick = { currentYearMonth = currentYearMonth.minusMonths(1) },
            onNextClick = { currentYearMonth = currentYearMonth.plusMonths(1) },
            onBack = {
                currentScreen = "main"
                currentYearMonth = YearMonth.now()  // Reset to current month when going back
            }
        )
    }
}