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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.finance_expense_tracker.BudgetedCategory
import com.example.finance_expense_tracker.ExpenseRecordEntity
import com.example.finance_expense_tracker.ExpenseRecordsViewModel
import com.example.finance_expense_tracker.Header
import com.example.finance_expense_tracker.Icon
import com.example.finance_expense_tracker.sendBudgetExceededNotification
import com.example.financemanagementapp.R
import java.time.YearMonth

val expenseList: MutableList<Icon> = mutableListOf(
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
fun BudgetedCategoriesScreen(
    navController: NavHostController,
    viewModel: ExpenseRecordsViewModel,
    expenseRecordsBudgeted: List<ExpenseRecordEntity>,
    onBack: () -> Unit,
    categoryToEdit: String?
) {
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }

    val budgetedCategoriesState by viewModel.budgetedCategories.collectAsState()

    val filteredBudgetedCategories = remember(budgetedCategoriesState, currentYearMonth) {
        budgetedCategoriesState.filter {
            YearMonth.from(it.monthYear) == currentYearMonth
        }
    }

    var editingCategory by remember { mutableStateOf<BudgetedCategory?>(null) }
    LaunchedEffect(categoryToEdit) {
        categoryToEdit?.let {
            val category = filteredBudgetedCategories.find { it.category == categoryToEdit }
            if (category != null) {
                editingCategory = category
            }
        }
    }

    fun updateFilteredCategories(yearMonth: YearMonth) {
        currentYearMonth = yearMonth
    }

    fun handleEdit(budgetedCategory: BudgetedCategory) {
        editingCategory = budgetedCategory
    }

    fun handleDelete(budgetedCategory: BudgetedCategory) {
        viewModel.deleteBudgetedCategory(budgetedCategory)
    }

    val totalIncome = expenseRecordsBudgeted
        .filter { it.isIncome && YearMonth.from(it.dateTime) == currentYearMonth }
        .sumOf { it.amount }

    val totalBudgeted = filteredBudgetedCategories.sumOf { it.limit }
    val totalSpent = filteredBudgetedCategories.sumOf { budgetedCategory ->
        expenseRecordsBudgeted
            .filter {
                it.category == budgetedCategory.category &&
                        YearMonth.from(it.dateTime) == YearMonth.from(budgetedCategory.monthYear)
            }
            .sumOf { it.amount }
    }
    val budgetUtilization = if (totalBudgeted > 0) (totalSpent / totalBudgeted).toFloat() else 0f

    val remainingIncome = totalIncome - totalBudgeted

    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        Header(
            currentYearMonth,
            onPrevClick = {
                updateFilteredCategories(currentYearMonth.minusMonths(1))
            },
            onNextClick = {
                updateFilteredCategories(currentYearMonth.plusMonths(1))
            }
        )

        IncomeCard(totalIncome = totalIncome)

        BudgetUtilizationProgressBar(
            totalBudgeted = totalBudgeted,
            totalSpent = totalSpent,
            budgetUtilization = budgetUtilization
        )

        if (filteredBudgetedCategories.isEmpty()) {
            NoBudgetedCategoriesScreen(onBack = onBack)
        } else {
            BudgetedCategoriesList(
                filteredBudgetedCategories = filteredBudgetedCategories,
                expenseRecordsBudgeted = expenseRecordsBudgeted,
                currentYearMonth = currentYearMonth,
                onEditClick = ::handleEdit,
                onDeleteClick = ::handleDelete
            )
        }

        editingCategory?.let { category ->
            EditBudgetedCategoryDialog(
                budgetedCategory = editingCategory!!,
                remainingIncome = remainingIncome,
                onDismiss = { editingCategory = null },
                onSave = { updatedCategory ->
                    viewModel.updateBudgetedCategory(updatedCategory)
                    editingCategory = null
                }
            )
        }
    }
}

@Composable
fun IncomeCard(totalIncome: Double) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .background(Color(0xFFE3F2FD), shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Total Income",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2) // Dark blue text
            )
            Text(
                text = "$${String.format("%.2f", totalIncome)}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )
        }
    }
}

@Composable
fun BudgetUtilizationProgressBar(
    totalBudgeted: Double,
    totalSpent: Double,
    budgetUtilization: Float
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Budget Utilization",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        LinearProgressIndicator(
            progress = budgetUtilization,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 10.dp, max = 20.dp) // Limit height to 10-20 dp
                .padding(vertical = 8.dp)
                .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp)),
            color = if (budgetUtilization >= 1f) Color.Red else Color.Green,
            trackColor = Color.LightGray
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Spent: $${formatAmount(totalSpent)}",
                fontSize = 20.sp,
                color = Color.Red // Spent amount in red
            )
            Text(
                text = "Budgeted: $${formatAmount(totalBudgeted)}",
                fontSize = 20.sp,
                color = Color.Green // Budgeted amount in green
            )
        }
    }
}

// Function to format amount, ensuring responsive design
fun formatAmount(amount: Double): String {
    return if (amount >= 1_000_000) {
        String.format("%,.2fM", amount / 1_000_000)
    } else if (amount >= 1_000) {
        String.format("%,.2fK", amount / 1_000)
    } else {
        String.format("%,.2f", amount)
    }
}

@Composable
fun NoBudgetedCategoriesScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No Budgeted Categories Available",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text(text = "Go Back")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BudgetedCategoriesList(
    filteredBudgetedCategories: List<BudgetedCategory>,
    expenseRecordsBudgeted: List<ExpenseRecordEntity>,
    currentYearMonth: YearMonth,
    onEditClick: (BudgetedCategory) -> Unit,
    onDeleteClick: (BudgetedCategory) -> Unit
) {
    val sortedCategories = remember(filteredBudgetedCategories) {
        filteredBudgetedCategories.sortedByDescending { category ->
            val totalSpent = expenseRecordsBudgeted
                .filter {
                    it.category == category.category &&
                            YearMonth.from(it.dateTime) == YearMonth.from(category.monthYear)
                }
                .sumOf { it.amount }
            totalSpent >= category.limit // True for over-limit categories
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(sortedCategories) { budgetedCategory ->
            val totalSpent = expenseRecordsBudgeted
                .filter {
                    it.category == budgetedCategory.category &&
                            YearMonth.from(it.dateTime) == YearMonth.from(budgetedCategory.monthYear)
                }
                .sumOf { it.amount }

            val isOverLimit = totalSpent > budgetedCategory.limit
            if (isOverLimit) {
                val context = LocalContext.current
                sendBudgetExceededNotification(context, budgetedCategory.category)
            }

            BudgetedCategoryRow(
                budgetedCategory.copy(
                    spent = totalSpent,
                    remaining = budgetedCategory.limit - totalSpent
                ),
                isOverLimit,
                onEditClick = { onEditClick(it) },
                onDeleteClick = { onDeleteClick(it) }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BudgetedCategoryRow(
    budgetedCategory: BudgetedCategory,
    isOverLimit: Boolean,
    onEditClick: (BudgetedCategory) -> Unit,
    onDeleteClick: (BudgetedCategory) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth()
            .border(1.dp, if (isOverLimit) Color.Red else Color.Gray, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Category Icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.Gray, CircleShape)
            ) {
                val icon = expenseList.find { it.name == budgetedCategory.category }
                if (icon != null) {
                    Image(
                        painter = painterResource(id = icon.resourceId),
                        contentDescription = "Category Icon",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = budgetedCategory.category,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Month: ${budgetedCategory.monthYear.monthValue}, Year: ${budgetedCategory.monthYear.year}",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Limit: ${budgetedCategory.limit}",
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = "Spent: ${budgetedCategory.spent}",
                    fontSize = 16.sp,
                    color = if (isOverLimit) Color.Red else Color.Black
                )
                Text(
                    text = "Remaining: ${budgetedCategory.remaining}",
                    fontSize = 16.sp,
                    color = if (isOverLimit) Color.Red else Color.Black
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { onEditClick(budgetedCategory) },
                modifier = Modifier
                    .padding(4.dp)
                    .background(if (isOverLimit) Color.Red else Color.Transparent, shape = CircleShape) // Highlight background
            ) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "Edit",
                    tint = if (isOverLimit) Color.White else Color.Black // Highlight icon color
                )
            }
            IconButton(
                onClick = { onDeleteClick(budgetedCategory) },
                modifier = Modifier.padding(4.dp)
            ) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditBudgetedCategoryDialog(
    budgetedCategory: BudgetedCategory,
    remainingIncome: Double,
    onDismiss: () -> Unit,
    onSave: (BudgetedCategory) -> Unit,
    title: String = "Edit Budgeted Category", // Default title, can be customized
    confirmButtonText: String = "Save", // Default confirm button text, can be customized
    cancelButtonText: String = "Cancel" // Default cancel button text, can be customized
) {
    var limit by remember { mutableStateOf(budgetedCategory.limit.toString()) }
    var errorMessage by remember { mutableStateOf("") }

    fun validateAndSave() {
        val newLimit = limit.toDoubleOrNull()
        if (newLimit == null) {
            errorMessage = "Invalid limit. Please enter a valid number."
        } else if (newLimit > remainingIncome) {
            errorMessage = "Limit exceeds remaining income of $${String.format("%.2f", remainingIncome)}."
        } else {
            onSave(budgetedCategory.copy(limit = newLimit))
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        confirmButton = {
            Button(
                onClick = { validateAndSave() },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = confirmButtonText)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = cancelButtonText)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Category: ${budgetedCategory.category}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = limit,
                    onValueChange = {
                        limit = it
                        errorMessage = ""
                    },
                    label = { Text("Budget Limit") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage.isNotEmpty()
                )
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    )
}
