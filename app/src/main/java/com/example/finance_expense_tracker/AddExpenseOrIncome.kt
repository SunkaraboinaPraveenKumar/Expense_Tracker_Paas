package com.example.finance_expense_tracker
import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.DialogProperties
import com.example.financemanagementapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

data class Icon(val name: String, val resourceId: Int)

@SuppressLint("SuspiciousIndentation")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddIncomeOrExpense(
    notificationRecord: ExpenseRecordEntity?=null,
    initialRecord: ExpenseRecordEntity? = null,
    onCancel: () -> Unit,
    onSave: (ExpenseRecordEntity) -> Unit,
    viewModel: ExpenseRecordsViewModel
) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val firebaseUser: FirebaseUser? = auth.currentUser
    val currentUser = firebaseUser.toString()
    var notificationAmount by remember { mutableStateOf(notificationRecord?.amount?.toString() ?: "") }
    var notificationIsIncome by remember { mutableStateOf(notificationRecord?.isIncome ?: true) }
    var accountType by remember { mutableStateOf(initialRecord?.accountType ?: "") }
    var category by remember { mutableStateOf(initialRecord?.category ?: "") }
    var amount by remember { mutableStateOf(initialRecord?.amount?.toString() ?: "") }
    val dateTime by remember { mutableStateOf(initialRecord?.dateTime ?: LocalDateTime.now()) }
    var notes by remember { mutableStateOf(initialRecord?.notes ?: "") }
    var errorMessage by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(initialRecord?.isIncome ?: true) }
    val incomeListState = viewModel.incomeList.collectAsState()
    val expenseListState = viewModel.expenseList.collectAsState()

    val incomeList = incomeListState.value
    val expenseList = expenseListState.value

    val incomeIcons: List<Icon> = incomeList.map { income ->
        Icon(income.name, income.iconResId)
    }
    val expenseIcons: List<Icon> = expenseList.map { expense ->
        Icon(expense.name, expense.iconResId)
    }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    val incomeCategories = incomeList.map{
        it.name
    }
    val expenseCategories = expenseList.map{
        it.name
    }
    val accountTypes = listOf("Card", "Cash", "Savings")

    val textColor = Color.Black
    val selectedColor = if (isSystemInDarkTheme()) Color(0xFFADD8E6) else Color(0xFFFFA500)

    val accountList = listOf(
        Icon("Card", R.drawable.credit_card),
        Icon("Cash", R.drawable.money),
        Icon("Savings", R.drawable.piggy_bank)
    )

    fun validateInputs(): Boolean {
        if (accountType.isEmpty() || category.isEmpty() || amount.isEmpty() || amount.toDoubleOrNull() == null) {
            errorMessage = "All fields must be filled correctly."
            return false
        }
        return true
    }

    fun resetForm() {
        accountType = ""
        category = ""
        amount = ""
        notes = ""
        errorMessage = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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
                                    icon = it.iconResId
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
                                    icon = it.iconResId
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    isIncome = true
                    resetForm() // Reset the form when toggling
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "Income",
                    color = if (isIncome) Color.White else Color.Gray
                )
            }
            Button(
                onClick = {
                    isIncome = false
                    resetForm() // Reset the form when toggling
                },
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
                backgroundColor = Color(0xFFF0F8FF)
            )

            SelectOptionField(
                options = if (isIncome) incomeCategories else expenseCategories,
                selectedOption = category,
                onOptionSelected = { category = it },
                text = "Category",
                iconList = if (isIncome) incomeIcons else expenseIcons,
                textColor = textColor,
                backgroundColor = Color(0xFFF0F8FF)
            )
        }
        val fgText=if(isSystemInDarkTheme()) LocalTextStyle.current.copy(color = Color.White) else LocalTextStyle.current.copy(color = textColor)
        TextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textStyle = fgText,
            singleLine = false
        )

        TextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textStyle = fgText,
            singleLine = true
        )

        Text(
            text = "Date & Time: ${dateTime.format(formatter)}",
            fontSize = 16.sp,
            modifier = Modifier.padding(vertical = 8.dp),
            color = textColor
        )

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            Calculator { newAmount ->
                amount = amount.toString()
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
    backgroundColor: Color
) {
    var showDialog by remember { mutableStateOf(false) }
    val selectedIcon = iconList.find { it.name == selectedOption }?.resourceId

    Box(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clickable { showDialog = true }
            .background(color = backgroundColor)
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
                        .padding(end = 8.dp)
                )
            }
            Text(
                text = if (selectedOption.isEmpty()) text else selectedOption,
                color = textColor,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = text)
            },
            text = {
                // Calculate item height assuming each item takes 48.dp
                val itemHeight = 48.dp
                val maxHeight = 12 * itemHeight // Max height for 12 items

                LazyColumn(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .heightIn(max = maxHeight)
                ) {
                    items(options) { option ->
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
            },
            properties = DialogProperties(
                dismissOnClickOutside = true
            )
        )
    }
}

@Composable
fun DropdownMenuItem(
    icon: Int,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .padding(end = 8.dp)
        )
        Text(text = text)
    }
}