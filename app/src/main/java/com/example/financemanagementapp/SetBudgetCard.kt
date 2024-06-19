package com.example.financemanagementapp

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
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.time.LocalDateTime
import java.time.YearMonth

// Sample list of categories
val categories = listOf(
    "Baby", "Beauty", "Bills", "Car", "Clothing", "Education",
    "Electronics", "Entertainment", "Food", "Health", "Home",
    "Insurance", "Shopping", "Social", "Sport", "Transportation"
)

val expenseList: List<Icon> = listOf(
    Icon("Baby", R.drawable.milk_bottle),
    Icon("Beauty", R.drawable.beauty),
    Icon("Bills", R.drawable.bill),
    Icon("Car", R.drawable.car_wash),
    Icon("Clothing", R.drawable.clothes_hanger),
    Icon("Education", R.drawable.education),
    Icon("Electronics", R.drawable.cpu),
    Icon("Entertainment", R.drawable.confetti),
    Icon("Food", R.drawable.diet),
    Icon("Health", R.drawable.better_health),
    Icon("Home", R.drawable.house),
    Icon("Insurance", R.drawable.insurance),
    Icon("Shopping", R.drawable.bag),
    Icon("Social", R.drawable.social_media),
    Icon("Sport", R.drawable.trophy),
    Icon("Transportation", R.drawable.transportation)
)

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
    var selectedMonthYear by rememberSaveable { mutableStateOf(YearMonth.now()) }
    var isDialogVisible by rememberSaveable { mutableStateOf(false) }
    var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }

    // Calculate total income for the current month
    val totalIncome = expenseRecordsBudgeted
        .filter { it.isIncome && YearMonth.from(it.dateTime) == selectedMonthYear }
        .sumOf { it.amount }

    // Calculate existing budget for the selected month
    val existingBudget = budgetedCategories
        .filter { YearMonth.from(it.monthYear) == selectedMonthYear }
        .sumOf { it.limit }

    // Filter out categories that are already budgeted for the selected month
//    val budgetedCategoriesForMonth = budgetedCategories.filter { it.monthYear == selectedMonthYear }
//    val availableCategories = categories.filter { category ->
//        budgetedCategoriesForMonth.none { it.category == category }
//    }
    val budgetedCategoriesForMonthAll by viewModel.budgetedCategories.collectAsState()

    // Filter categories that are budgeted for the selected month
    val budgetedCategoriesForMonth = budgetedCategoriesForMonthAll.filter { it.monthYear == selectedMonthYear }

    // Get the list of available categories not yet budgeted for the selected month
    val availableCategories = categories.filter { category ->
        budgetedCategoriesForMonth.none { it.category == category }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
                    color = Color.Black
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
                    icon = iconImage?.resourceId ?: R.drawable.ic_category
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
            viewModel = viewModel
        )
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
        Text(
            text = category,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetBudgetDialog(
    category: String,
    existingBudget: Double,
    totalIncome: Double,
    viewModel: ExpenseRecordsViewModel, // Added ViewModel parameter
    onCloseDialog: () -> Unit,
    onSetBudget: (Double) -> Unit
) {
    var budgetLimit by remember { mutableStateOf("") }
    val iconImage = expenseList.find { it.name == category }
    var errorMessage by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onCloseDialog) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Set Budget for $category",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Gray, CircleShape)
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
                    color = Color.Black
                )
            }
            OutlinedTextField(
                value = budgetLimit,
                onValueChange = { budgetLimit = it },
                label = { Text(text = "Budget Limit") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage.isNotEmpty()
            )
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onCloseDialog) {
                    Text(text = "Cancel")
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
                    Text(text = "Set")
                }
            }
        }
    }
}