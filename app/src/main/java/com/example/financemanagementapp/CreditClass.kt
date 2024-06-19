package com.example.financemanagementapp

import androidx.room.DatabaseView
import java.time.LocalDate
@DatabaseView("SELECT * FROM credit_table")
data class Credit(
    val amount: Double,
    val from: String,
    val description: String,
    val dateOfRepayment: LocalDate
)