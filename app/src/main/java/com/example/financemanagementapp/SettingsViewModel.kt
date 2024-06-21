package com.example.financemanagementapp

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    private var _selectedUiMode by mutableStateOf(UiMode.SystemDefault)
    val selectedUiMode: UiMode
        get() = _selectedUiMode
    fun setSelectedUiMode(uiMode: UiMode) {
        _selectedUiMode = uiMode
    }

    private var _selectedCurrency by mutableStateOf("USD")
    val selectedCurrency: String
        get() = _selectedCurrency

    fun setSelectedCurrency(currency: String) {
        _selectedCurrency = currency
    }
}