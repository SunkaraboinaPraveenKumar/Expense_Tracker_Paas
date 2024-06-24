package com.example.finance_expense_tracker
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@RequiresApi(Build.VERSION_CODES.O)
class ExpenseRecordsViewModel(private val context: Context) : ViewModel() {
    private val database = AppDatabase.getDatabase(context)

    private val _expenseRecords = MutableStateFlow<List<ExpenseRecordEntity>>(emptyList())
    val expenseRecords: StateFlow<List<ExpenseRecordEntity>> = _expenseRecords

    private val _budgetedCategories = MutableStateFlow<List<BudgetedCategory>>(emptyList())
    val budgetedCategories: StateFlow<List<BudgetedCategory>> = _budgetedCategories

    private val _transactionRecord = MutableStateFlow<List<Transaction>>(emptyList())
    val transactionRecord: StateFlow<List<Transaction>> = _transactionRecord

    private val _incomeList = MutableStateFlow<List<Income>>(emptyList())
    val incomeList: StateFlow<List<Income>> = _incomeList

    private val _expenseList = MutableStateFlow<List<Expense>>(emptyList())
    val expenseList: StateFlow<List<Expense>> = _expenseList

    init {
        loadExpenseRecords()
        loadBudgetedCategories()
        loadTransactions()
        loadLists()
    }

    private fun loadLists() {
        viewModelScope.launch {
            try {
                val incomeList = withContext(Dispatchers.IO) {
                    database.incomeDao().getAllIncomes()
                }
                _incomeList.value = incomeList

                val expenseList = withContext(Dispatchers.IO) {
                    database.expenseDao().getAllExpenses()
                }
                _expenseList.value = expenseList
            } catch (e: Exception) {
                Log.e("ExpenseRecordsViewModel", "Error loading income or expense lists", e)
            }
        }
    }

    fun insertInitialData(incomeList: List<Income>, expenseList: List<Expense>) {
        viewModelScope.launch {
            try {
                // Fetch existing incomes and expenses
                val existingIncomes = withContext(Dispatchers.IO) { database.incomeDao().getAllIncomes() }
                val existingExpenses = withContext(Dispatchers.IO) { database.expenseDao().getAllExpenses() }

                // Filter out duplicates
                val newIncomes = incomeList.filterNot { newIncome ->
                    existingIncomes.any { existingIncome ->
                        existingIncome.name == newIncome.name
                    }
                }

                val newExpenses = expenseList.filterNot { newExpense ->
                    existingExpenses.any { existingExpense ->
                        existingExpense.name == newExpense.name
                    }
                }

                // Insert only non-duplicates
                insertIncomes(newIncomes)
                insertExpenses(newExpenses)
            } catch (e: Exception) {
                Log.e("ExpenseRecordsViewModel", "Error inserting initial data", e)
            }
        }
    }

    fun insertIncome(income: Income) {
        viewModelScope.launch {
            try {
                val existingIncomes = withContext(Dispatchers.IO) { database.incomeDao().getAllIncomes() }
                if (existingIncomes.none { it.name == income.name }) {
                    insertIncomes(listOf(income))
                }
            } catch (e: Exception) {
                Log.e("ExpenseRecordsViewModel", "Error inserting income", e)
            }
        }
    }

    fun insertExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                val existingExpenses = withContext(Dispatchers.IO) { database.expenseDao().getAllExpenses() }
                if (existingExpenses.none { it.name == expense.name }) {
                    insertExpenses(listOf(expense))
                }
            } catch (e: Exception) {
                Log.e("ExpenseRecordsViewModel", "Error inserting expense", e)
            }
        }
    }

    private suspend fun insertIncomes(incomes: List<Income>) {
        withContext(Dispatchers.IO) {
            database.incomeDao().insertAll(incomes)
        }
        refreshIncomes()
    }

    private suspend fun insertExpenses(expenses: List<Expense>) {
        withContext(Dispatchers.IO) {
            database.expenseDao().insertAll(expenses)
        }
        refreshExpenses()
    }

    private suspend fun refreshIncomes() {
        try {
            val incomes = withContext(Dispatchers.IO) {
                database.incomeDao().getAllIncomes()
            }
            withContext(Dispatchers.Main) {
                _incomeList.value = incomes
            }
        } catch (e: Exception) {
            Log.e("ExpenseRecordsViewModel", "Error refreshing incomes", e)
        }
    }

    private suspend fun refreshExpenses() {
        try {
            val expenses = withContext(Dispatchers.IO) {
                database.expenseDao().getAllExpenses()
            }
            withContext(Dispatchers.Main) {
                _expenseList.value = expenses
            }
        } catch (e: Exception) {
            Log.e("ExpenseRecordsViewModel", "Error refreshing expenses", e)
        }
    }

    // Inside ExpenseRecordsViewModel
    fun updateIncome(income: Income) {
        viewModelScope.launch {
            try {
                updateIncomes(listOf(income))
            } catch (e: Exception) {
                Log.e("ExpenseRecordsViewModel", "Error updating income", e)
            }
        }
    }

    private suspend fun updateIncomes(incomes: List<Income>) {
        withContext(Dispatchers.IO) {
            database.incomeDao().updateAll(incomes)
        }
        refreshIncomes()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            try {
                val transactions = withContext(Dispatchers.IO) {
                    database.transactionDao().getAllTransactions()
                }
                _transactionRecord.value = transactions
            } catch (e: Exception) {
                Log.e("ExpenseRecordsViewModel", "Error loading transactions", e)
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

    private suspend fun refreshTransactions() {
        try {
            val transactions = withContext(Dispatchers.IO) {
                database.transactionDao().getAllTransactions()
            }
            withContext(Dispatchers.Main) {
                _transactionRecord.value = transactions
            }
        } catch (e: Exception) {
            Log.e("ExpenseRecordsViewModel", "Error refreshing transactions", e)
        }
    }

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