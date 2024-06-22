package com.example.finance_expense_tracker

import java.time.LocalDateTime
import java.time.YearMonth

data class ExpenseRecord(
    val accountType: String,
    val category: String,
    val amount: Double,
    val dateTime: LocalDateTime,
    val isIncome: Boolean,
    val notes: String,
    val date:YearMonth,
    val icon: Int
)