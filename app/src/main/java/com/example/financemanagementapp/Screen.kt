package com.example.financemanagementapp

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Accounts : Screen("accounts")
    object Analysis : Screen("analysis")
    object Debts : Screen("debts")
}