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
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finance_expense_tracker.ui.theme.FinanceManagementAppTheme
import com.example.financemanagementapp.R
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    companion object {
        private const val SMS_PERMISSION_REQUEST_CODE = 101
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 102
        private const val CHANNEL_ID = "finance_notifications"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannelBudget(this)
        createNotificationChannel(this)
        requestPermissions()
        FirebaseApp.initializeApp(this)
        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun proceedToApp() {
        handleIncomingIntent(intent)
        setContent {
            FinanceManagementAppTheme {
                val viewModel: ExpenseRecordsViewModel by viewModels {
                    ExpenseRecordsViewModelFactory(
                        applicationContext
                    )
                }
                val incomeList = listOf(
                    Income(name = "Awards", iconResId = R.drawable.trophy),
                    Income(name = "Coupons", iconResId = R.drawable.coupons),
                    Income(name = "Grants", iconResId = R.drawable.grants),
                    Income(name = "Lottery", iconResId = R.drawable.lottery),
                    Income(name = "Refunds", iconResId = R.drawable.refund),
                    Income(name = "Rental", iconResId = R.drawable.rental),
                    Income(name = "Salary", iconResId = R.drawable.salary),
                    Income(name = "Sale", iconResId = R.drawable.sale)
                )

                val expenseList = listOf(
                    Expense(name = "Baby", iconResId = R.drawable.milk_bottle),
                    Expense(name = "Beauty", iconResId = R.drawable.beauty),
                    Expense(name = "Bills", iconResId = R.drawable.bill),
                    Expense(name = "Car", iconResId = R.drawable.car_wash),
                    Expense(name = "Clothing", iconResId = R.drawable.clothes_hanger),
                    Expense(name = "Education", iconResId = R.drawable.education),
                    Expense(name = "Electronics", iconResId = R.drawable.cpu),
                    Expense(name = "Entertainment", iconResId = R.drawable.confetti),
                    Expense(name = "Food", iconResId = R.drawable.diet),
                    Expense(name = "Health", iconResId = R.drawable.better_health),
                    Expense(name = "Home", iconResId = R.drawable.house),
                    Expense(name = "Insurance", iconResId = R.drawable.insurance),
                    Expense(name = "Shopping", iconResId = R.drawable.bag),
                    Expense(name = "Social", iconResId = R.drawable.social_media),
                    Expense(name = "Sport", iconResId = R.drawable.trophy),
                    Expense(name = "Transportation", iconResId = R.drawable.transportation)
                )

                LaunchedEffect(Unit) {
                    viewModel.insertInitialData(incomeList, expenseList)
                }

                val navController = rememberNavController()
                val categoryToEdit = intent.getStringExtra("category_to_edit")
                val amount = intent.getDoubleExtra("amount", 0.0)
                val isIncome = intent.getBooleanExtra("isIncome", true)
                val authViewModel=AuthViewModel(applicationContext,auth,firestore)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestPermissions() {
        val permissionsNeeded = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.READ_SMS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsNeeded.toTypedArray(),
                SMS_PERMISSION_REQUEST_CODE
            )
            proceedToApp()
        } else {
            proceedToApp()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            SMS_PERMISSION_REQUEST_CODE -> {
                if (permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "SMS Permission granted.", Toast.LENGTH_SHORT).show()
                    proceedToApp()
                } else {
                    Toast.makeText(this, "SMS Permission denied.", Toast.LENGTH_SHORT).show()
                    // Handle denial or provide alternative flow
                    // Example: Show explanation or disable SMS-related functionality
                }
            }

            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                // Handle notification permission if needed
                if (permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Notification Permission granted.", Toast.LENGTH_SHORT)
                        .show()
                    // Proceed with any actions that require this permission
                } else {
                    Toast.makeText(this, "Notification Permission denied.", Toast.LENGTH_SHORT)
                        .show()
                    // Handle denial or provide alternative flow
                    // Example: Show explanation or disable notification-related functionality
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Finance Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for finance management"
            enableLights(true)
            lightColor = Color.RED
            enableVibration(true)
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

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
            SplashScreen(navController = navController, context = context)
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
                    authViewModel.authenticate(
                        username = username as String,
                        password = password as String,
                        onSuccess = {
                            navController.navigate("main") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onError = { error ->
                            // Handle error, for example, show error message
                        }
                    )
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                authViewModel = authViewModel
            )
        }
        composable("main") {
            FinanceManagementApp(
                context = context,
                categoryToEdit = categoryToEdit,
                amount = amount,
                isIncome = isIncome
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
                style = androidx.compose.material.MaterialTheme.typography.h4
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