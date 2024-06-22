package com.example.finance_expense_tracker

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

class NotificationReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onReceive(context: Context, intent: Intent) {
        val transactionId = intent.getLongExtra("transactionId", -1)
        val isDebt = intent.getBooleanExtra("isDebt", true)
        val person = intent.getStringExtra("person")
        val amount = intent.getStringExtra("amount")
        if (person != null) {
            if (amount != null) {
                NotificationUtils.createNotification(context, transactionId, isDebt, person,amount)
            }
        }
    }
}