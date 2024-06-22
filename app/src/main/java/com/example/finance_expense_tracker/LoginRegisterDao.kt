package com.example.finance_expense_tracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LoginRegisterDao {
    @Insert
    suspend fun insert(record: RegisterEntity)

    @Query("SELECT * FROM register_records WHERE username = :username")
    suspend fun getUserByUsername(username: String): RegisterEntity?

    @Query("SELECT * FROM register_records WHERE username = :username AND password = :password")
    suspend fun authenticate(username: String, password: String): RegisterEntity?
}
