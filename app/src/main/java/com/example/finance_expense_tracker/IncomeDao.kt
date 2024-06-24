package com.example.finance_expense_tracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface IncomeDao {
    @Insert
    suspend fun insertAll(incomes: List<Income>)

    @Update
    suspend fun update(income: Income)

    @Update
    suspend fun updateAll(incomes: List<Income>)

    @Query("SELECT * FROM income_table")
    suspend fun getAllIncomes(): List<Income>
}