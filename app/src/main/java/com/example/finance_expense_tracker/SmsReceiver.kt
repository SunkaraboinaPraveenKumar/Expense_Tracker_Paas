package com.example.finance_expense_tracker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SmsMessage
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.financemanagementapp.R


class SmsReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "SmsReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Check for permissions before processing SMS
        if (!isSmsPermissionGranted(context)) {
            Log.w(TAG, "SMS permissions not granted.")
            return
        }

        val bundle = intent.extras
        if (bundle != null) {
            val pdus = bundle.get("pdus") as Array<*>
            val messages: Array<SmsMessage?> = arrayOfNulls(pdus.size)

            for (i in pdus.indices) {
                messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                val msgBody = messages[i]?.messageBody ?: ""
                val msgAddress = messages[i]?.originatingAddress
                Log.d(TAG, "Received SMS: $msgBody from $msgAddress")

                // Process incoming SMS here
                processSms(context, msgBody)
            }
        }
    }

    private fun processSms(context: Context, message: String) {
        Log.d(TAG, "Processing SMS: $message")
        // Check for keywords like "debited" or "credited"
        when {
            message.contains("debited", true) -> {
                val amount = extractAmount(message)
                Log.d(TAG, "Debited amount detected: $amount")
                sendNotification(context, amount, isIncome = false)
            }
            message.contains("credited", true) -> {
                val amount = extractAmount(message)
                Log.d(TAG, "Credited amount detected: $amount")
                sendNotification(context, amount, isIncome = true)
            }
            // Add more conditions if needed
            else -> {
                Log.d(TAG, "SMS does not match expected patterns.")
                // Handle other types of messages or ignore
            }
        }
    }

    private fun extractAmount(message: String): Double {
        // Improved regex to capture the amount in different patterns
        val regex = Regex("""(?:credited|debited)\s*(?:by\s*)?(?:Rs\.?\s?|INR\s?)?(\d+(?:\.\d{1,2})?)""", RegexOption.IGNORE_CASE)
        val match = regex.find(message)
        val amountStr = match?.groups?.get(1)?.value
        val amount = amountStr?.toDoubleOrNull() ?: 0.0

        // Debugging output (optional)
        println("Matched amount: $amountStr")

        return amount
    }

    private fun sendNotification(context: Context, amount: Double, isIncome: Boolean) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("amount", amount)
            putExtra("isIncome", isIncome)
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "sms_notification_channel"
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(if (isIncome) "Income Detected" else "Expense Detected")
            .setContentText("Amount: ₹$amount")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "SMS Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Handle the case where notification permission is not granted.
            Log.w(TAG, "Notification permission not granted.")
            return
        }

        NotificationManagerCompat.from(context).notify(1, notificationBuilder.build())
    }

    private fun isSmsPermissionGranted(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.RECEIVE_SMS
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_SMS
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.SEND_SMS
                ) == PackageManager.PERMISSION_GRANTED
    }
}