package com.example.financemanagementapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.YearMonth
import java.time.format.DateTimeFormatter

object YearMonthConverter {
    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM")

    @TypeConverter
    @JvmStatic
    fun toYearMonth(value: String?): YearMonth? {
        return value?.let {
            return YearMonth.parse(it, formatter)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromYearMonth(date: YearMonth?): String? {
        return date?.format(formatter)
    }
}
