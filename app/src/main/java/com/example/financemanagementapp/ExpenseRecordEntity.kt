package com.example.financemanagementapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.YearMonth

@Entity(tableName = "expense_records")
data class ExpenseRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateTime: LocalDateTime?,
    val category: String,
    val accountType: String,
    val amount: Double,
    val isIncome: Boolean,
    val icon: Int,
    val date:YearMonth,
    val notes:String
)

