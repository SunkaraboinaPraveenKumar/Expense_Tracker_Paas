package com.example.financemanagementapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FinanceManagementDao {

    @Insert
    suspend fun insertDebt(debt: DebtEntity)

    @Insert
    suspend fun insertCredit(credit: CreditEntity)

    @Query("SELECT * FROM debt_table")
    suspend fun getAllDebts(): List<DebtEntity>

    @Query("SELECT * FROM credit_table")
    suspend fun getAllCredits(): List<CreditEntity>
}