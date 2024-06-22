package com.example.finance_expense_tracker
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "register_records")
data class RegisterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val password: String,
    val confirmPassword: String,
)

