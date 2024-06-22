package com.example.finance_expense_tracker

import CreditForm
import DebtForm
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financemanagementapp.R

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DebtsScreen(onBack:()->Unit,viewModel: ExpenseRecordsViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    val (debtTransactions, setDebtTransactions) = remember { mutableStateOf(listOf<Debt>()) }
    val (creditTransactions, setCreditTransactions) = remember { mutableStateOf(listOf<Credit>()) }
    val (allTransactions, setAllTransactions) = remember { mutableStateOf<List<Transaction>>(emptyList()) }
    val (showTransactionScreen, setShowTransactionScreen) = remember { mutableStateOf(false) }

    if (showTransactionScreen) {
        val allTransaction by viewModel.transactionRecord.collectAsState()
        TransactionScreen(transactions = allTransaction, onBack = {
            setShowTransactionScreen(false)
        },viewModel)
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            CenterAlignedTopAppBar(
                title = { Text(text = "Debts/Credits") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource( id = R.drawable.arrow_back),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(
                        onClick = { setShowTransactionScreen(true) },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = Color.LightGray
                        )
                    ) {
                        Text(text = "Transaction History", fontSize = 16.sp)
                    }
                    Row {
                        TabButton(
                            text = "Debt",
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 }
                        )
                        TabButton(
                            text = "Credit",
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (selectedTab == 0) {
                    DebtForm(onAddDebt = { debt ->
                        val updatedTransactions = debtTransactions.toMutableList()
                        updatedTransactions.add(debt)
                        setDebtTransactions(updatedTransactions)
                        updateAllTransactions(viewModel,updatedTransactions, creditTransactions, setAllTransactions)
                    })
                } else {
                    CreditForm(onAddCredit = { credit ->
                        val updatedTransactions = creditTransactions.toMutableList()
                        updatedTransactions.add(credit)
                        setCreditTransactions(updatedTransactions)
                        updateAllTransactions(viewModel,debtTransactions, updatedTransactions, setAllTransactions)
                    })
                }
            }
        }
    }
}

fun updateAllTransactions(
    viewModel: ExpenseRecordsViewModel,
    debtTransactions: List<Debt>,
    creditTransactions: List<Credit>,
    setAllTransactions: (List<Transaction>) -> Unit
) {
    val allTransactions = (debtTransactions.map { transaction ->
        Transaction(
            amount = transaction.amount,
            toOrFrom = transaction.to,
            description = transaction.description,
            dateOfRepayment = transaction.dateOfRepayment,
            isDebt = true
        )
    } + creditTransactions.map { transaction ->
        Transaction(
            amount = transaction.amount,
            toOrFrom = transaction.from,
            description = transaction.description,
            dateOfRepayment = transaction.dateOfRepayment,
            isDebt = false
        )
    }).sortedBy { it.dateOfRepayment }

    for(transaction in allTransactions){
        viewModel.insertTransaction(transaction)
    }
    setAllTransactions(allTransactions)
}

@Composable
fun TabButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.White,
            containerColor = if (selected) Color.Blue else Color.Gray
        ),
        modifier = Modifier.padding(4.dp)
    ) {
        Text(text = text)
    }
}

@Composable
fun TransactionScreen(transactions: List<Transaction>, onBack: () -> Unit, viewModel: ExpenseRecordsViewModel) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        TextButton(
            onClick = onBack,
            colors = ButtonDefaults.textButtonColors(
                containerColor = Color.LightGray
            )
        ) {
            Text(text = "Back to Debt/Credit")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (transactions.isEmpty()) {
                Text(
                    text = "No transaction history",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                        contentColor = Color.White
                    ),
                ) {
                    Text(text = "Go Back")
                }
            } else {
//                val allTransaction by viewModel.transactionRecord.collectAsState()
                TransactionHistoryScreen(transactions = transactions, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TransactionHistoryScreen(transactions: List<Transaction>,viewModel: ExpenseRecordsViewModel) {
//    val allTransaction by viewModel.transactionRecord.collectAsState()
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        items(transactions) { transaction ->
            TransactionRow(transaction)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TransactionRow(transaction: Transaction) {
    val borderColor = if (transaction.isDebt) Color.Red else Color.Green
    val backgroundColor = if (transaction.isDebt) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
    val textColor = if (transaction.isDebt) Color(0xFFD32F2F) else Color(0xFF388E3C)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Amount: ${transaction.amount}",
            color = textColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        if (transaction.isDebt) {
            Text(
                text = "To: ${transaction.toOrFrom}",
                color = textColor,
                fontSize = 16.sp
            )
        } else {
            Text(
                text = "From: ${transaction.toOrFrom}",
                color = textColor,
                fontSize = 16.sp
            )
        }
        Text(
            text = "Description: ${transaction.description}",
            color = textColor,
            fontSize = 16.sp
        )
        Text(
            text = "Date of Repayment: ${transaction.dateOfRepayment}",
            color = textColor,
            fontSize = 16.sp
        )
    }
}