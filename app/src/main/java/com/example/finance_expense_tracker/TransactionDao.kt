package com.example.finance_expense_tracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    @Query("SELECT * FROM transaction_record")
    suspend fun getAllTransactions(): List<Transaction>
}
