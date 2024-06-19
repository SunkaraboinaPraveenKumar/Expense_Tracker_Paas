package com.example.financemanagementapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "debt_table")
data class DebtEntity(
    val amount: Double,
    val to: String,
    val description: String,
    val dateOfRepayment: LocalDate
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}