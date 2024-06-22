package com.example.finance_expense_tracker

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.financemanagementapp.R

const val CHANNEL_ID = "budget_exceed_channel"
const val NOTIFICATION_ID = 1

fun sendBudgetExceededNotification(context: Context, category: String) {
    // Create an intent to open MainActivity when notification is clicked
    val intent = Intent(context, MainActivity::class.java).apply {
        putExtra("category_to_edit", category)
    }

    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Build the notification
    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.notification_icon) // Ensure this drawable resource exists
        .setContentTitle("Budget Exceeded")
        .setContentText("Your budget for $category has exceeded its limit.")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    // Get an instance of NotificationManagerCompat
    val notificationManager = NotificationManagerCompat.from(context)

    // Check for notification permission and send notification if granted
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
        notificationManager.notify(NOTIFICATION_ID, notification)
    } else {
        // Request notification permission if not already granted
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            1
        )
    }
}

fun createNotificationChannelBudget(context: Context) {
    // Create notification channel for Android O and above
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Budget Exceed Notification"
        val descriptionText = "Notification channel for budget exceed alerts"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        // Register the channel with the system
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}