package com.example.finance_expense_tracker

import java.time.LocalDate
data class Debt(
    val amount: Double,
    val to: String,
    val description: String,
    val dateOfRepayment: LocalDate
)