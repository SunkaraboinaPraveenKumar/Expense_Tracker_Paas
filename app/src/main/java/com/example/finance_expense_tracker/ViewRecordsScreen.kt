package com.example.finance_expense_tracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financemanagementapp.R
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViewRecordsScreen(
    viewModel: ExpenseRecordsViewModel, // ViewModel to fetch expense records
    onEdit: (ExpenseRecordEntity) -> Unit,
    onDelete: (Long) -> Unit,
    onBack: () -> Unit
) {
    // Collect expenseRecords as State from the ViewModel
    val expenseRecords by viewModel.expenseRecords.collectAsState(emptyList())

    // Mutable state for current date and filter option
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var currentFilterOption by remember { mutableStateOf(FilterOption.MONTHLY) }

    // Calculate date range based on current date and filter option
    val dateRange = calculateDateRange(currentDate, currentFilterOption)

    // Filter records based on date range
    val filteredRecords = filterRecords(expenseRecords, currentFilterOption, dateRange)

    // Sort records by date
    val sortedExpenseRecords = filteredRecords.sortedByDescending { it.dateTime }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header and filtering controls
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

        Spacer(modifier = Modifier.height(16.dp))

        // Displaying current month card
        CurrentMonthCard(
            currentFilterOption = currentFilterOption,
            dateRange = dateRange,
            incomeRecords = filteredRecords.filter { it.isIncome },
            expenseRecords = filteredRecords.filter { !it.isIncome }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // LazyColumn to display expense records
        LazyColumn {
            items(sortedExpenseRecords) { record ->
                ExpenseRecordItem(
                    record = record,
                    onEdit = onEdit,
                    onDelete = onDelete
                )
                Divider(color = Color.Gray, thickness = 1.dp)
            }
        }
    }
}



// Function to calculate date range based on filter option
@RequiresApi(Build.VERSION_CODES.O)
private fun calculateDateRange(
    currentDate: LocalDate,
    currentFilterOption: FilterOption
): Pair<LocalDate, LocalDate> {
    return when (currentFilterOption) {
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

// Function to filter records based on date range and filter option
@RequiresApi(Build.VERSION_CODES.O)
private fun filterRecords(
    expenseRecords: List<ExpenseRecordEntity>, // Use ExpenseRecordEntity instead of ExpenseRecord
    currentFilterOption: FilterOption,
    dateRange: Pair<LocalDate, LocalDate>
): List<ExpenseRecordEntity> { // Return List<ExpenseRecordEntity> instead of List<ExpenseRecord>
    return expenseRecords.filter { record ->
        when (currentFilterOption) {
            FilterOption.DAILY -> record.dateTime?.toLocalDate() == dateRange.first
            FilterOption.WEEKLY -> {
                val recordDate = record.dateTime?.toLocalDate()
                recordDate!! in dateRange.first..dateRange.second
            }
            FilterOption.MONTHLY -> YearMonth.from(record.dateTime) == YearMonth.from(dateRange.first)
        }
    }
}


// Function to handle deletion of record
private fun onDeleteRecord(record: ExpenseRecordEntity, onDelete: (ExpenseRecordEntity) -> Unit) {
    onDelete(record)
}



enum class FilterOption {
    DAILY,
    WEEKLY,
    MONTHLY
}@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HeaderRecord(
    currentDate: LocalDate,
    currentFilterOption: FilterOption,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    onFilterOptionSelected: (FilterOption) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    // Define the date formats for different filter options
    val dailyFormatter = DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy")
    val monthlyFormatter = DateTimeFormatter.ofPattern("MMMM, yyyy")

    // Weekly format is special because it needs to show the start and end of the week
    val startOfWeek = currentDate.with(java.time.DayOfWeek.MONDAY)
    val endOfWeek = startOfWeek.plusDays(6)
    val weeklyFormatter = DateTimeFormatter.ofPattern("MMM d")
    val weeklyText = "${startOfWeek.format(weeklyFormatter)} - ${endOfWeek.format(weeklyFormatter)}"

    // Determine the displayed text based on current filter option
    val displayText = when (currentFilterOption) {
        FilterOption.DAILY -> currentDate.format(dailyFormatter)
        FilterOption.WEEKLY -> weeklyText
        FilterOption.MONTHLY -> currentDate.format(monthlyFormatter)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFF5722))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevClick) {
            Image(
                painter = painterResource(id = R.drawable.ic_chevron_left),
                contentDescription = "Previous Date",
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = displayText,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        IconButton(onClick = onNextClick) {
            Image(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = "Next Date",
                modifier = Modifier.size(24.dp)
            )
        }

        IconButton(onClick = { showDialog = true }) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = "Filter",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        // Display the filter dialog if showDialog is true
        if (showDialog) {
            FilterDialog(
                currentFilterOption = currentFilterOption,
                onFilterOptionSelected = onFilterOptionSelected,
                onDismissRequest = { showDialog = false }
            )
        }
    }
}

@Composable
fun DropdownMenuItem(
    onClick: () -> Unit,
    content: @Composable () -> Unit // Added a content lambda to provide flexibility
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            content() // Invoke the content lambda provided by the caller
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExpenseRecordItem(
    record: ExpenseRecordEntity,
    onEdit: (ExpenseRecordEntity) -> Unit,
    onDelete: (Long) -> Unit
) {
    val color = if (record.isIncome) Color(0xFF4CAF50) else Color.Red
    val sign = if (record.isIncome) "+" else "-"
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .background(Color.LightGray, shape = MaterialTheme.shapes.small)
    ) {
        record.dateTime?.let {
            Text(
                text = it.format(formatter),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .background(Color.White)
                    .padding(bottom = 4.dp)
            )
        }
        Divider(color = color, thickness = 1.dp, modifier = Modifier.padding(bottom = 8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = record.icon),
                contentDescription = "Category Icon",
                modifier = Modifier
                    .size(40.dp)
                    .padding(8.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Text(text = record.category, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = record.accountType, fontSize = 14.sp)

//                // Display notes if present
                record.notes?.let { notes ->
                    Text(
                        text = notes,
                        fontSize = 12.sp,
                        color = Color.DarkGray, // Darker color for notes
                        fontStyle = FontStyle.Italic, // Italic text style for notes
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "$sign${record.amount}",
                    fontSize = 16.sp,
                    color = color
                )
            }

            Row(modifier = Modifier.padding(start = 8.dp)) {
                IconButton(onClick = { onEdit(record) }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { onDelete(record.id) }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
fun FilterDialog(
    currentFilterOption: FilterOption,
    onFilterOptionSelected: (FilterOption) -> Unit,
    onDismissRequest: () -> Unit
) {
    androidx.compose.material.AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Select Filter Option")
        },
        text = {
            Column {
                FilterOption.values().forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onFilterOptionSelected(option)
                                onDismissRequest()
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentFilterOption == option) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.Green
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = option.name, fontSize = 18.sp)
                    }
                }
            }
        },
        confirmButton = {
            // Empty confirm button, actions are handled on item click
        }
    )
}