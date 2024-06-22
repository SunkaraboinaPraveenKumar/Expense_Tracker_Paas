package com.example.finance_expense_tracker

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository, context: Context) : ViewModel() {

    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    var isAuthenticated by mutableStateOf(getAuthState())
        private set

    var currentUser by mutableStateOf<RegisterEntity?>(loadCurrentUser())
        private set

    var errorMessage by mutableStateOf<String?>(null)

    fun checkUserExists(username: String) {
        viewModelScope.launch {
            val user = userRepository.getUserByUsername(username)
            errorMessage = if (user != null) "User already exists. Please Login!" else null
        }
    }

    suspend fun register(username: String, password: String, confirmPassword: String) {
        // Simulated registration
        if (password == confirmPassword && userRepository.getUserByUsername(username) == null) {
            userRepository.registerUser(RegisterEntity(username = username, password = password, confirmPassword = password))
        }
    }

    fun registerUser(username: String, password: String) {
        viewModelScope.launch {
            val existingUser = userRepository.getUserByUsername(username)
            if (existingUser != null) {
                errorMessage = "User already exists! Please Login"
            } else {
                val newUser = RegisterEntity(username = username, password = password, confirmPassword = password)
                userRepository.registerUser(newUser)
                errorMessage = null
                saveAuthState(true)
                saveCurrentUser(newUser)
                isAuthenticated = true
                currentUser = newUser
            }
        }
    }

    fun authenticate(username: String, password: String) {
        viewModelScope.launch {
            val user = userRepository.authenticateUser(username, password)
            if (user != null) {
                isAuthenticated = true
                currentUser = user
                errorMessage = null
                saveAuthState(true)
                saveCurrentUser(user)
            } else {
                errorMessage = "Invalid username or password!"
            }
        }
    }

    fun logout() {
        isAuthenticated = false
        currentUser = null
        clearAuthState()
        clearCurrentUser()
    }

    private fun getAuthState(): Boolean {
        return prefs.getBoolean("isAuthenticated", false)
    }

    fun saveAuthState(authState: Boolean) {
        prefs.edit().putBoolean("isAuthenticated", authState).apply()
    }

    private fun clearAuthState() {
        prefs.edit().remove("isAuthenticated").apply()
    }

    private fun loadCurrentUser(): RegisterEntity? {
        val username = prefs.getString("username", null) ?: return null
        val password = prefs.getString("password", null) ?: return null
        return RegisterEntity(username = username, password = password, confirmPassword = password)
    }

    private fun saveCurrentUser(user: RegisterEntity) {
        prefs.edit().putString("username", user.username)
            .putString("password", user.password)
            .apply()
    }

    private fun clearCurrentUser() {
        prefs.edit().remove("username")
            .remove("password")
            .apply()
    }
}
