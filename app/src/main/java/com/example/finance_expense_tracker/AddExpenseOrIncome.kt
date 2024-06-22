package com.example.finance_expense_tracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financemanagementapp.R
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

data class Icon(val name: String, val resourceId: Int)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddIncomeOrExpense(
    notificationRecord: ExpenseRecordEntity?=null,
    initialRecord: ExpenseRecordEntity? = null,
    onCancel: () -> Unit,
    onSave: (ExpenseRecordEntity) -> Unit,
    viewModel: ExpenseRecordsViewModel
) {
    var notificationAmount by remember { mutableStateOf(notificationRecord?.amount?.toString() ?: "") }
    var notificationIsIncome by remember { mutableStateOf(notificationRecord?.isIncome ?: true) }
    // Use initial record data if available
    var accountType by remember { mutableStateOf(initialRecord?.accountType ?: "") }
    var category by remember { mutableStateOf(initialRecord?.category ?: "") }
    var amount by remember { mutableStateOf(initialRecord?.amount?.toString() ?: "") }
    val dateTime by remember { mutableStateOf(initialRecord?.dateTime ?: LocalDateTime.now()) }
    var notes by remember { mutableStateOf(initialRecord?.notes ?: "") }
    var errorMessage by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(initialRecord?.isIncome ?: true) }

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    val incomeCategories = listOf(
        "Awards", "Coupons", "Grants", "Lottery", "Refunds",
        "Rental", "Salary", "Sale"
    )
    val expenseCategories = listOf(
        "Baby", "Beauty", "Bills", "Car", "Clothing", "Education",
        "Electronics", "Entertainment", "Food", "Health", "Home",
        "Insurance", "Shopping", "Social", "Sport", "Transportation"
    )
    val accountTypes = listOf("Card", "Cash", "Savings")

    val textColor = Color.Black
    val selectedColor = if (isSystemInDarkTheme()) Color(0xFFADD8E6) else Color(0xFFFFA500)

    val accountList = listOf(
        Icon("Card", R.drawable.credit_card),
        Icon("Cash", R.drawable.money),
        Icon("Savings", R.drawable.piggy_bank)
    )
    val incomeList = listOf(
        Icon("Awards", R.drawable.trophy),
        Icon("Coupons", R.drawable.coupons),
        Icon("Grants", R.drawable.grants),
        Icon("Lottery", R.drawable.lottery),
        Icon("Refunds", R.drawable.refund),
        Icon("Rental", R.drawable.rental),
        Icon("Salary", R.drawable.salary),
        Icon("Sale", R.drawable.sale)
    )

    val expenseList = listOf(
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

    fun validateInputs(): Boolean {
        if (accountType.isEmpty() || category.isEmpty() || amount.isEmpty() || amount.toDoubleOrNull() == null) {
            errorMessage = "All fields must be filled correctly."
            return false
        }
        return true
    }
    if(notificationRecord!=null&&notificationRecord.amount!=0.0){
        viewModel.insertOrUpdateExpenseRecord(notificationRecord)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Buttons: Cancel and Save
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onCancel) {
                Text(text = "Cancel", fontSize = 16.sp)
            }
            TextButton(
                onClick = {
                    if (validateInputs()) {
                        // Creating ExpenseRecordEntity based on isIncome
                        val record = if (isIncome) {
                            incomeList.find { it.name == category }?.let {
                                ExpenseRecordEntity(
                                    accountType = accountType,
                                    category = category,
                                    amount = amount.toDouble(),
                                    dateTime = dateTime,
                                    isIncome = true,
                                    notes = notes,
                                    date = YearMonth.now(),
                                    icon = it.resourceId
                                )
                            }
                        } else {
                            expenseList.find { it.name == category }?.let {
                                ExpenseRecordEntity(
                                    accountType = accountType,
                                    category = category,
                                    amount = amount.toDouble(),
                                    dateTime = dateTime,
                                    isIncome = false,
                                    notes = notes,
                                    date = YearMonth.now(),
                                    icon = it.resourceId
                                )
                            }
                        }
                        record?.let { onSave(it) }
                    }
                }
            ) {
                Text(text = "Save", fontSize = 16.sp)
            }
        }

        // Buttons: Income and Expense
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { isIncome = true },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "Income",
                    color = if (isIncome) Color.White else Color.Gray
                )
            }
            Button(
                onClick = { isIncome = false },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "Expense",
                    color = if (!isIncome) Color.White else Color.Gray
                )
            }
        }

        Text(
            text = if (isIncome) "Add Income" else "Add Expense",
            fontSize = 24.sp,
            color = Color(0xFFFFA500),
            modifier = Modifier.padding(16.dp)
        )

        // Fields: Account Type and Category
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SelectOptionField(
                options = accountTypes,
                selectedOption = accountType,
                onOptionSelected = { accountType = it },
                text = "Account Type",
                iconList = accountList,
                textColor = textColor,
                backgroundColor = Color(0xFFF0F8FF) // Example background color
            )

            // Category (Income or Expense)
            SelectOptionField(
                options = if (isIncome) incomeCategories else expenseCategories,
                selectedOption = category,
                onOptionSelected = { category = it },
                text = "Category",
                iconList = if (isIncome) incomeList else expenseList,
                textColor = textColor,
                backgroundColor = Color(0xFFF0F8FF)
            )
        }

        // Text Field: Notes
        TextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textStyle = LocalTextStyle.current.copy(color = textColor),
            singleLine = false
        )

        // Text Field: Amount
        TextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textStyle = LocalTextStyle.current.copy(color = textColor),
            singleLine = true
        )

        // Date & Time
        Text(
            text = "Date & Time: ${dateTime.format(formatter)}",
            fontSize = 16.sp,
            modifier = Modifier.padding(vertical = 8.dp),
            color = textColor
        )

        // Calculator
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            Calculator { newAmount ->
                amount = newAmount.toString()
            }
        }

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun SelectOptionField(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    text: String,
    iconList: List<Icon>,
    textColor: Color,
    backgroundColor: Color // Add backgroundColor parameter
) {
    var showDialog by remember { mutableStateOf(false) }
    val selectedIcon = iconList.find { it.name == selectedOption }?.resourceId

    Box(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clickable { showDialog = true }
            .background(color = backgroundColor)// Use backgroundColor parameter here
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            selectedIcon?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp) // Space between icon and text
                )
            }
            Text(
                text = if (selectedOption.isEmpty()) text else selectedOption,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
        }
    }

    if (showDialog) {
        // AlertDialog remains unchanged
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = text)
            },
            text = {
                Column {
                    options.forEach { option ->
                        val icon = iconList.find { it.name == option }
                        if (icon != null) {
                            DropdownMenuItem(
                                icon = icon.resourceId,
                                text = option,
                                onClick = {
                                    onOptionSelected(option)
                                    showDialog = false
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                // No confirm button needed as selection handles closing
            },
            dismissButton = {
                // No dismiss button needed as clicking outside handles closing
            }
        )
    }
}

@Composable
fun DropdownMenuItem(
    icon: Int,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = text,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}