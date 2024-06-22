package com.example.finance_expense_tracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.financemanagementapp.R
import java.time.LocalDateTime
import java.time.YearMonth
@RequiresApi(Build.VERSION_CODES.O)
@Entity(tableName = "expense_records")
data class ExpenseRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateTime: LocalDateTime?=LocalDateTime.now(),
    val category: String="Credit",
    val accountType: String="Card",
    val amount: Double,
    val isIncome: Boolean,
    val icon: Int= R.drawable.credit_card,
    val date:YearMonth= YearMonth.now(),
    val notes:String=""
)