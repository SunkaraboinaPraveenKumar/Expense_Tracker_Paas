package com.example.finance_expense_tracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insertAll(expenses: List<Expense>)

    @Query("SELECT * FROM expense_table")
    suspend fun getAllExpenses(): List<Expense>
}