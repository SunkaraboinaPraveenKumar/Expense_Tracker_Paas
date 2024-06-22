package com.example.finance_expense_tracker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finance_expense_tracker.ui.theme.FinanceManagementAppTheme
import com.example.financemanagementapp.R
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    companion object {
        private const val SMS_PERMISSION_REQUEST_CODE = 101
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 102
        private const val CHANNEL_ID = "finance_notifications"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannelBudget(this)
        // Create notification channel if necessary
        createNotificationChannel(this)
        // Request notification permissions
        requestNotificationPermissions()
        // Handle any incoming intent
        handleIncomingIntent(intent)

        // Set the content view using Jetpack Compose
        setContent {
            FinanceManagementAppTheme {
                val navController = rememberNavController()
                val userDao = AppDatabase.getInstance(applicationContext).loginRegisterDao()
                val userRepository = UserRepository(userDao)
                val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(userRepository, applicationContext))

                val categoryToEdit = intent.getStringExtra("category_to_edit")
                val amount = intent.getDoubleExtra("amount", 0.0)
                val isIncome = intent.getBooleanExtra("isIncome", true)

                AuthenticationFlow(
                    navController,
                    authViewModel,
                    context = applicationContext,
                    startDestination = "splash",
                    categoryToEdit = categoryToEdit,
                    amount = amount,
                    isIncome = isIncome
                )
            }
        }
    }

    // Create the notification channel for API 26+
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Finance Notifications",
            NotificationManager.IMPORTANCE_HIGH // Set to high to ensure notifications are shown
        ).apply {
            description = "Notifications for finance management"
            enableLights(true)
            lightColor = Color.RED
            enableVibration(true)
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // Request notification permissions for API 33+
    private fun requestNotificationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_REQUEST_CODE)
            } else {
                // Permission already granted, you can send a notification here for testing
                sendNotification(this, "Test Notification", "This is a test notification.")
            }
        }
    }

    // Handle the result of permission requests
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permissions granted.", Toast.LENGTH_SHORT).show()
                // Send a test notification to ensure it works
                sendNotification(this, "Permission Granted", "You will now receive notifications.")
            } else {
                Toast.makeText(this, "Notification permissions denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Send a notification
    private fun sendNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Set priority to high
            .build()
        notificationManager.notify(1, notification)
    }

    // Handle incoming intents
    private fun handleIncomingIntent(intent: Intent?) {
        intent?.let {
            if (it.action == Intent.ACTION_VIEW) {
                val data = it.data
                // Handle the data if necessary
                Log.d("MainActivity", "Incoming data: $data")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AuthenticationFlow(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    context: Context,
    startDestination: String,
    categoryToEdit: String?,
    amount: Double,
    isIncome: Boolean
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable("splash") {
            SplashScreen(navController, context)
        }
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
            FinanceManagementApp(
                context,
                categoryToEdit,
                amount,
                isIncome
            )
        }
    }
}

@Composable
fun SplashScreen(navController: NavHostController, context: Context) {
    LaunchedEffect(Unit) {
        delay(3000) // Delay for 3 seconds
        // Check if it's the first launch and navigate accordingly
        val destination = if (isFirstLaunch(context)) "register" else "main"
        navController.navigate(destination) {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo), // Replace with your actual image resource
                contentDescription = "App Icon",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "FinanceManagementApp",
                fontSize = 24.sp,
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}

// Function to check if it's the first launch
fun isFirstLaunch(context: Context): Boolean {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val isFirstLaunch = prefs.getBoolean("isFirstLaunch", true)
    if (isFirstLaunch) {
        prefs.edit().putBoolean("isFirstLaunch", false).apply()
    }
    return isFirstLaunch
}
