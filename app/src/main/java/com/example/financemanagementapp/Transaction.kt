package com.example.financemanagementapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "transaction_record")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val toOrFrom: String,
    val description: String,
    @ColumnInfo(name = "date_of_repayment") val dateOfRepayment: LocalDate,
    val isDebt: Boolean
)
