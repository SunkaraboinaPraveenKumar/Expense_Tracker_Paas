package com.example.finance_expense_tracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.financemanagementapp.R
import java.time.LocalDateTime
import java.time.YearMonth

// In your main composable
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetBudgetCard(
    onClose: () -> Unit,
    onBackHome: () -> Unit,
    budgetedCategories: List<BudgetedCategory>,
    expenseRecordsBudgeted: List<ExpenseRecordEntity>,
    onAddBudgetCategory: (BudgetedCategory) -> Unit,
    onViewBudgetedCategoriesClick: () -> Unit,
    viewModel: ExpenseRecordsViewModel
) {
    FinanceExpenseTrackerTheme {
        var selectedMonthYear by rememberSaveable { mutableStateOf(YearMonth.now()) }
        var isDialogVisible by rememberSaveable { mutableStateOf(false) }
        var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }

        val textColor = MaterialTheme.colorScheme.onBackground
        // Calculate total income for the current month
        val expenseListState = viewModel.expenseList.collectAsState()
        val expenseList = expenseListState.value
        val expenseIcons: List<Icon> = expenseList.map { expense -> // Replace with your logic
            Icon(expense.name, expense.iconResId)
        }
        val categories = expenseList.map{
            it.name
        }
        val totalIncome = expenseRecordsBudgeted
            .filter { it.isIncome && YearMonth.from(it.dateTime) == selectedMonthYear }
            .sumOf { it.amount }

        // Calculate existing budget for the selected month
        val existingBudget = budgetedCategories
            .filter { YearMonth.from(it.monthYear) == selectedMonthYear }
            .sumOf { it.limit }

        // Filter categories that are budgeted for the selected month
        val budgetedCategoriesForMonthAll by viewModel.budgetedCategories.collectAsState()
        val budgetedCategoriesForMonth = budgetedCategoriesForMonthAll.filter { it.monthYear == selectedMonthYear }

        // Get the list of available categories not yet budgeted for the selected month
        val availableCategories = categories.filter { category ->
            budgetedCategoriesForMonth.none { it.category == category }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background) // Use theme's background color
        ) {
            Button(
                onClick = onViewBudgetedCategoriesClick,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(end = 16.dp, top = 16.dp)
            ) {
                Text(text = "View Budgeted Categories")
            }
            Spacer(modifier = Modifier.padding(8.dp))
            Header(
                currentDate = selectedMonthYear,
                onPrevClick = { selectedMonthYear = selectedMonthYear.minusMonths(1) },
                onNextClick = { selectedMonthYear = selectedMonthYear.plusMonths(1) }
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Categories",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor // Use theme's text color
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(availableCategories) { category ->
                    val iconImage = expenseList.find { it.name == category }
                    BudgetCategoryRow(
                        category = category,
                        onSetBudgetClick = {
                            selectedCategory = category
                            isDialogVisible = true
                        },
                        icon = iconImage?.iconResId ?: R.drawable.ic_category
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        if (isDialogVisible && selectedCategory != null) {
            SetBudgetDialog(
                category = selectedCategory!!,
                existingBudget = existingBudget,
                totalIncome = totalIncome,
                onCloseDialog = { isDialogVisible = false },
                onSetBudget = { limit ->
                    val newBudgetedCategory = BudgetedCategory(
                        category = selectedCategory!!,
                        limit = limit,
                        dateTime = LocalDateTime.now(),
                        spent = 0.0,
                        remaining = limit,
                        monthYear = selectedMonthYear
                    )
                    onAddBudgetCategory(newBudgetedCategory)
                    isDialogVisible = false
                },
                viewModel = viewModel,
                expenseIcons = expenseIcons
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BudgetCategoryRow(category: String, onSetBudgetClick: () -> Unit, icon: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color.Gray, CircleShape)
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = "Category Icon",
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        val textColor = MaterialTheme.colorScheme.onBackground
        Text(
            text = category,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textColor // Use the dynamic text color
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onSetBudgetClick,
            modifier = Modifier
                .padding(start = 8.dp)
                .height(36.dp)
        ) {
            Text(text = "Set Budget")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetBudgetDialog(
    category: String,
    existingBudget: Double,
    totalIncome: Double,
    viewModel: ExpenseRecordsViewModel,
    onCloseDialog: () -> Unit,
    onSetBudget: (Double) -> Unit,
    expenseIcons:List<Icon>
) {
    var budgetLimit by remember { mutableStateOf("") }
    val iconImage = expenseIcons.find { it.name == category }
    var errorMessage by remember { mutableStateOf("") }

    // Use dynamic colors based on the theme
    val backgroundColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface

    Dialog(onDismissRequest = onCloseDialog) {
        Column(
            modifier = Modifier
                .background(backgroundColor)
                .padding(16.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Set Budget for $category",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = iconImage?.resourceId ?: R.drawable.ic_category),
                        contentDescription = "Category Icon",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = category,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
            OutlinedTextField(
                value = budgetLimit,
                onValueChange = { budgetLimit = it },
                label = { Text(text = "Budget Limit", color = textColor) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = androidx.compose.ui.text.TextStyle(color = textColor),
                isError = errorMessage.isNotEmpty(),
                colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onCloseDialog) {
                    Text(text = "Cancel", color = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    val limit = budgetLimit.toDoubleOrNull()
                    if (limit != null) {
                        val newTotalBudget = existingBudget + limit
                        if (newTotalBudget <= totalIncome) {
                            // Insert into the database using the ViewModel
                            val newBudgetedCategory = BudgetedCategory(
                                category = category,
                                limit = limit,
                                dateTime = LocalDateTime.now(),
                                spent = 0.0,
                                remaining = limit,
                                monthYear = YearMonth.now()
                            )
                            viewModel.insertOrUpdateBudgetedCategory(newBudgetedCategory)
                            onSetBudget(limit)
                        } else {
                            errorMessage = "Income is insufficient to cover the budget."
                        }
                    } else {
                        errorMessage = "Please enter a valid budget limit."
                    }
                }) {
                    Text(text = "Set", color = textColor)
                }
            }
        }
    }
}
