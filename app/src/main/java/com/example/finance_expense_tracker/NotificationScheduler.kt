package com.example.finance_expense_tracker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import java.time.LocalDate
import java.time.ZoneId

const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001

object NotificationScheduler {

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnspecifiedImmutableFlag")
    fun scheduleNotification(context: Context, transactionId: Long, isDebt: Boolean, dateOfRepayment: LocalDate, person: String, amount: String) {
        if (!hasNotificationPermission(context)) {
            requestNotificationPermission(context)
            return
        }

        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra("transactionId", transactionId)
                putExtra("isDebt", isDebt)
                putExtra("person", person)
                putExtra("amount", amount)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context.applicationContext,
                transactionId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val triggerTime = dateOfRepayment.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to schedule notification. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun requestNotificationPermission(context: Context) {
        if (context is Activity) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        } else {
            Toast.makeText(context, "Permission request failed. Context is not an Activity.", Toast.LENGTH_SHORT).show()
        }
    }
}
