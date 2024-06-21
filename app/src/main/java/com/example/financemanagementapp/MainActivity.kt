package com.example.financemanagementapp

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financemanagementapp.ui.theme.FinanceManagementAppTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)
        setContent {
            FinanceManagementAppTheme {
                val navController = rememberNavController()
                val userDao = AppDatabase.getInstance(applicationContext).loginRegisterDao()
                val userRepository = UserRepository(userDao)
                val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(userRepository, applicationContext))

                val categoryToEdit = intent.getStringExtra("category_to_edit")

                val startDestination = if (isFirstLaunch()) "register" else "main"

                AuthenticationFlow(navController, authViewModel, applicationContext, startDestination, categoryToEdit)
            }
        }
    }

    private fun isFirstLaunch(): Boolean {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean("isFirstLaunch", true)
        if (isFirstLaunch) {
            prefs.edit().putBoolean("isFirstLaunch", false).apply()
        }
        return isFirstLaunch
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AuthenticationFlow(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    context: Context,
    startDestination: String,
    categoryToEdit: String? = null
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable("register") {
            RegistrationScreen(
                onRegisterSuccess = {
                    authViewModel.saveAuthState(true)
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }
        composable("login") {
            LoginScreen(
                onLoginSuccess = { username, password ->
                    authViewModel.authenticate(username, password)
                    if (authViewModel.isAuthenticated) {
                        navController.navigate("main") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                onNavigateToRegister = {
                    authViewModel.errorMessage = null
                    navController.navigate("register")
                },
                authViewModel = authViewModel
            )
        }
        composable("main") {
            FinanceManagementApp(context, navController, categoryToEdit)
        }
    }
}
