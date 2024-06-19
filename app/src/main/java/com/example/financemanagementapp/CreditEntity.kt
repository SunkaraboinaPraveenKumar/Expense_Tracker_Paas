package com.example.financemanagementapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "credit_table")
data class CreditEntity(
    val amount: Double,
    val from: String,
    val description: String,
    val dateOfRepayment: LocalDate
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}