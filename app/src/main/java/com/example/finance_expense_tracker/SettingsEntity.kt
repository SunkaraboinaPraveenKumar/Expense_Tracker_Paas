package com.example.finance_expense_tracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 1, // Use a single row to store settings
    val currencySymbol: String,
    val uiMode: String
)
