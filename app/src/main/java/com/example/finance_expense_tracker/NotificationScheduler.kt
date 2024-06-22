package com.example.finance_expense_tracker

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.ZoneId

object NotificationScheduler {

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnspecifiedImmutableFlag")
    fun scheduleNotification(context: Context, transactionId: Long, isDebt: Boolean, dateOfRepayment: LocalDate,person: String,amount:String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("transactionId", transactionId)
            putExtra("isDebt", isDebt)
            putExtra("person",person)
            putExtra("amount",amount)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
            transactionId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = dateOfRepayment.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }
}