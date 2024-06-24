package com.example.finance_expense_tracker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HelpScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help", color = Color.White) },
                backgroundColor = Color(0xFF6200EE), // Primary color
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF6F6F6)) // Light background color
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()) // Add vertical scroll
            ) {
                Text(
                    text = "FAQs",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6200EE), // Primary color
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Divider(color = Color(0xFF6200EE), thickness = 1.dp)
                FAQItem(
                    question = "How to add a new expense?",
                    answer = "To add a new expense, go to the main screen and click on the '+' button and select choose Expense button."
                )
                Divider(color = Color(0xFF6200EE), thickness = 1.dp)
                FAQItem(
                    question = "How to set a budget?",
                    answer = "To set a budget,click on 'Budget' button at the bottom of the screen, then add budget amount for selected category."
                )
                Divider(color = Color(0xFF6200EE), thickness = 1.dp)
                FAQItem(
                    question = "How to check expense analysis?",
                    answer = "To check expense analysis, click on 'Analysis' button at the bottom of the screen and click on 'Expense Analysis'."
                )
                Divider(color = Color(0xFF6200EE), thickness = 1.dp)
                FAQItem(
                    question = "Why is the budget not being set?",
                    answer = "The budget may not be setting due to the requirement of having income greater than expenses.",
                    steps = listOf(
                        "Ensure that the total income entered exceeds the total expenses.",
                        "Navigate to the 'Set Budget' screen and input your financial details accordingly.",
                        "Verify that all fields are correctly filled out and confirm the budget settings."
                    )
                )
                Divider(color = Color(0xFF6200EE), thickness = 1.dp)
                FAQItem(
                    question = "Is this app available for iPhone/iOS",
                    answer = "If you use an iPhone/iOS,sorry to let you down.This app is only available for Android platform.",
                )

                Divider(color = Color(0xFF6200EE), thickness = 1.dp)
                FAQItem(
                    question = "Is online backup avialable in the app?",
                    answer = "Online backup/restore feature is not available in app.",
                )
                Divider(color = Color(0xFF6200EE), thickness = 1.dp)
                FAQItem(
                    question = "How to track Debts/Credits?",
                    answer = "Navigate to Mainscreen select view debts button,then select 'Transaction History' button.",
                )

                Divider(color = Color(0xFF6200EE), thickness = 1.dp)
                FAQItem(
                    question = "Can I use this app in multiple devices?",
                    answer = "This is an offline app so it is not possible.You should install and use this app on your Primary device only.",
                )
                Divider(color = Color(0xFF6200EE), thickness = 1.dp)
                // Add more FAQ items here if needed

            }
        }
    )
}

@Composable
fun FAQItem(question: String, answer: String, steps: List<String> = emptyList()) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_dialog_info),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF6200EE) // Primary color
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = question,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF333333)
            )
        }
        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = answer,
                fontSize = 16.sp,
                color = Color(0xFF666666),
                modifier = Modifier.padding(start = 32.dp) // Indent answer
            )
            steps.forEach { step ->
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Text("• ", fontSize = 16.sp, color = Color(0xFF666666))
                    Text(step, fontSize = 16.sp, color = Color(0xFF666666))
                }
            }
        }
    }
}