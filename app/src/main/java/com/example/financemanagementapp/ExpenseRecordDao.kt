package com.example.financemanagementapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ExpenseRecordDao {
    @Insert
    suspend fun insert(record: ExpenseRecordEntity)

    @Query("SELECT * FROM expense_records ORDER BY dateTime DESC")
    suspend fun getAllExpenseRecords(): List<ExpenseRecordEntity>
    @Update
    suspend fun update(record: ExpenseRecordEntity)
    @Query("DELETE FROM expense_records WHERE id = :recordId")
    suspend fun deleteById(recordId: Long)
}
