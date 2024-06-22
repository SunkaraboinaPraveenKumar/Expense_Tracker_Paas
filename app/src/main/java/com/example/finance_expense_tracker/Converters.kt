package com.example.finance_expense_tracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

object Converters {
    @RequiresApi(Build.VERSION_CODES.O)
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    @RequiresApi(Build.VERSION_CODES.O)
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    @RequiresApi(Build.VERSION_CODES.O)
    private val yearMonthFormatter = DateTimeFormatter.ofPattern("yyyy-MM")

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    @JvmStatic
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it, dateFormatter) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    @JvmStatic
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(dateFormatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    @JvmStatic
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, dateTimeFormatter) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    @JvmStatic
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(dateTimeFormatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    @JvmStatic
    fun toYearMonth(value: String?): YearMonth? {
        return value?.let { YearMonth.parse(it, yearMonthFormatter) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    @JvmStatic
    fun fromYearMonth(yearMonth: YearMonth?): String? {
        return yearMonth?.format(yearMonthFormatter)
    }
}
