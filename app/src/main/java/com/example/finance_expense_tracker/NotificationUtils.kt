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
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.financemanagementapp.R

object NotificationUtils {
    const val CHANNEL_ID = "finance_channel"
    const val TAG = "NotificationUtils"
    const val REQUEST_NOTIFICATION_PERMISSION_CODE = 123

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun createNotification(context: Context, transactionId: Long, isDebt: Boolean, person: String,amount:String) {
        if (!isNotificationPermissionGranted(context)) {
            Log.w(TAG, "Notification permission not granted.")
            requestNotificationPermission(context)
            return
        }

        createNotificationChannel(context)

        val categoryToEdit = if (isDebt) "Expense" else "Income"
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("category_to_edit", categoryToEdit)
            putExtra("transactionId", transactionId)
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            transactionId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationContentText = if (isDebt) {
            "You have to pay ${amount} to $person."
        } else {
            "You have to collect ${amount} from $person."
        }

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Repayment Due")
            .setContentText(notificationContentText)
            .setSubText("Add this ${if (isDebt) "expense" else "income"} to your records.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(transactionId.toInt(), notificationBuilder.build())
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun isNotificationPermissionGranted(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun requestNotificationPermission(context: Context) {
        val intent = Intent(context, PermissionRequestActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Finance Notifications"
            val descriptionText = "Notifications for due transactions"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

class PermissionRequestActivity : Activity() {

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            NotificationUtils.REQUEST_NOTIFICATION_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == NotificationUtils.REQUEST_NOTIFICATION_PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, you might want to finish this activity and handle the action
            }
        }
        finish() // Close the activity after requesting permission
    }
}