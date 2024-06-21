package com.example.financemanagementapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ExpenseRecordEntity::class, BudgetedCategory::class, Transaction::class, RegisterEntity::class],
    version = 3
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun budgetedCategoryDao(): BudgetedCategoryDao
    abstract fun expenseRecordDao(): ExpenseRecordDao
    abstract fun loginRegisterDao(): LoginRegisterDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finance_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        @JvmStatic
        fun getInstance(applicationContext: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java,
                        "finance_database"
                    )
                        .addMigrations(MIGRATION_1_2)
                        .addMigrations(MIGRATION_2_3)
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE!!
        }

        // Migration from version 1 to 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Handle migration, including altering or dropping the `date_time` column
                db.execSQL("ALTER TABLE expense_records RENAME TO temp_expense_records")
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS `expense_records` (
                `accountType` TEXT NOT NULL,
                `amount` REAL NOT NULL,
                `category` TEXT NOT NULL,
                `date` TEXT NOT NULL,
                `dateTime` TEXT,
                `icon` INTEGER NOT NULL,
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `isIncome` INTEGER NOT NULL,
                `notes` TEXT NOT NULL
            )
            """
                )
                db.execSQL("INSERT INTO expense_records (accountType, amount, category, date, dateTime, icon, id, isIncome, notes) " +
                        "SELECT accountType, amount, category, date, dateTime, icon, id, isIncome, notes FROM temp_expense_records")
                db.execSQL("DROP TABLE temp_expense_records")
            }
        }


        // Migration from version 2 to 3: Create RegisterEntity table
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `register_records` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `username` TEXT NOT NULL,
                        `password` TEXT NOT NULL,
                        `confirmPassword` TEXT NOT NULL
                    )
                    """
                )
            }
        }
    }
}
