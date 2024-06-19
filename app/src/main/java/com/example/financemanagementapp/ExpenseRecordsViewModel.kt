package com.example.financemanagementapp

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log


class ExpenseRecordsViewModel(private val context: Context) : ViewModel() {
    private val database = AppDatabase.getDatabase(context)
    private val _expenseRecords = MutableStateFlow<List<ExpenseRecordEntity>>(emptyList())
    val expenseRecords: StateFlow<List<ExpenseRecordEntity>> = _expenseRecords
    private val _budgetedCategories = MutableStateFlow<List<BudgetedCategory>>(emptyList())
    val budgetedCategories: StateFlow<List<BudgetedCategory>> = _budgetedCategories

    private val _transactionRecord = MutableStateFlow<List<Transaction>>(emptyList())
    val transactionRecord: StateFlow<List<Transaction>> = _transactionRecord
    init {
        loadExpenseRecords()
        loadBudgetedCategories()
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            try {
                val transactions = withContext(Dispatchers.IO) {
                    database.transactionDao().getAllTransactions()
                }
                _transactionRecord.value = transactions
            } catch (e: Exception) {
                Log.e("ExpenseRecordsViewModel", "Error loading budgeted categories", e)
            }
        }
    }
    private fun loadBudgetedCategories() {
        viewModelScope.launch {
            try {
                val categories = withContext(Dispatchers.IO) {
                    database.budgetedCategoryDao().getAllBudgetedCategories()
                }
                _budgetedCategories.value = categories
            } catch (e: Exception) {
                Log.e("ExpenseRecordsViewModel", "Error loading budgeted categories", e)
            }
        }
    }
    ////////////////////////////////////////////
    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    database.transactionDao().insertTransaction(transaction)
                }
                refreshTransactions() // Optional: Refresh UI after insertion
            } catch (e: Exception) {
                Log.e("ExpenseRecordsViewModel", "Error inserting transaction", e)
            }
        }
    }
    //////////////////////////////////////////
    fun insertOrUpdateBudgetedCategory(category: BudgetedCategory) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    if (category.id != 0L) {
                        // Update if it already exists
                        database.budgetedCategoryDao().update(category)
                    } else {
                        // Insert new category
                        database.budgetedCategoryDao().insert(category)
                    }
                }
                refreshBudgetedCategories()
            } catch (e: Exception) {
                Log.e("ExpenseRecordsViewModel", "Error saving budgeted category", e)
            }
        }
    }

    fun deleteBudgetedCategory(category: BudgetedCategory) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    database.budgetedCategoryDao().delete(category)
                }
                refreshBudgetedCategories()
            } catch (e: Exception) {
                Log.e("ExpenseRecordsViewModel", "Error deleting budgeted category", e)
            }
        }
    }
////////////////////////////////////////////////////////
private suspend fun refreshTransactions() {
    try {
        val categories = withContext(Dispatchers.IO) {
            database.transactionDao().getAllTransactions()
        }
        withContext(Dispatchers.Main) {
            _transactionRecord.value = categories
        }
    } catch (e: Exception) {
        Log.e("ExpenseRecordsViewModel", "Error refreshing budgeted categories", e)
    }
}
    /////////////////////////////////////////////
    private suspend fun refreshBudgetedCategories() {
        try {
            val categories = withContext(Dispatchers.IO) {
                database.budgetedCategoryDao().getAllBudgetedCategories()
            }
            withContext(Dispatchers.Main) {
                _budgetedCategories.value = categories
            }
        } catch (e: Exception) {
            Log.e("ExpenseRecordsViewModel", "Error refreshing budgeted categories", e)
        }
    }

    // Other methods for handling Expense Records as before

    private fun loadExpenseRecords() {
        viewModelScope.launch {
            try {
                val records = withContext(Dispatchers.IO) {
                    database.expenseRecordDao().getAllExpenseRecords()
                }
                _expenseRecords.value = records
            } catch (e: Exception) {
                Log.e("ExpenseRecordsViewModel", "Error loading expense records", e)
            }
        }
    }

    // Insert or Update Expense Record
    fun insertOrUpdateExpenseRecord(record: ExpenseRecordEntity) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    if (record.id != 0L) {
                        // If the record has a valid ID, update it
                        database.expenseRecordDao().update(record)
                    } else {
                        // Otherwise, insert a new record
                        database.expenseRecordDao().insert(record)
                    }
                }
                refreshExpenseRecords()
            } catch (e: Exception) {
                Log.e("ExpenseRecordsViewModel", "Error saving expense record", e)
            }
        }
    }

    // Delete Expense Record
    fun deleteExpenseRecord(recordId: Long) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    database.expenseRecordDao().deleteById(recordId)
                }
                refreshExpenseRecords()
            } catch (e: Exception) {
                Log.e("ExpenseRecordsViewModel", "Error deleting expense record", e)
            }
        }
    }

    // Refresh Expense Records
    private suspend fun refreshExpenseRecords() {
        try {
            val records = withContext(Dispatchers.IO) {
                database.expenseRecordDao().getAllExpenseRecords()
            }
            withContext(Dispatchers.Main) {
                _expenseRecords.value = records
            }
        } catch (e: Exception) {
            Log.e("ExpenseRecordsViewModel", "Error refreshing expense records", e)
        }
    }

    suspend fun getAllTransactions(): List<Transaction> {
        return database.transactionDao().getAllTransactions()
    }
    // Fetch all Expense Records
    suspend fun getAllExpenseRecords(): List<ExpenseRecordEntity> {
        return database.expenseRecordDao().getAllExpenseRecords()
    }

    fun updateBudgetedCategory(updatedCategory: BudgetedCategory) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    database.budgetedCategoryDao().update(updatedCategory)
                }
                refreshBudgetedCategories()
            } catch (e: Exception) {
                Log.e("ExpenseRecordsViewModel", "Error updating budgeted category", e)
            }
        }
    }

}