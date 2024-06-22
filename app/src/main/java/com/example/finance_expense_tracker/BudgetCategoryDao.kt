package com.example.finance_expense_tracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth

@Dao
interface BudgetedCategoryDao {

    @Insert
    suspend fun insert(category: BudgetedCategory)

    @Update
    suspend fun update(category: BudgetedCategory)

    @Delete
    suspend fun delete(category: BudgetedCategory): Int

    @Query("SELECT * FROM budgeted_categories WHERE monthYear = :monthYear")
    fun getBudgetedCategoriesForMonth(monthYear: YearMonth): Flow<List<BudgetedCategory>>

    @Query("SELECT * FROM budgeted_categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Long): BudgetedCategory?

    @Query("SELECT * FROM budgeted_categories")
    fun getAllBudgetedCategories(): List<BudgetedCategory>
}