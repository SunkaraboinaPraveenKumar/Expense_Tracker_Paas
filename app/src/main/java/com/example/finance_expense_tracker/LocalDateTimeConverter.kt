package com.example.finance_expense_tracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LocalDateTimeConverter {
    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    @JvmStatic
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let {
            return LocalDateTime.parse(it, formatter)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromLocalDateTime(date: LocalDateTime?): String? {
        return date?.format(formatter)
    }
}
