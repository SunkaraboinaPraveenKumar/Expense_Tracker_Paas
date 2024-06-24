package com.example.finance_expense_tracker

import SettingsViewModel
import UiMode
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    var showUiModeDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    val selectedUiMode = viewModel.selectedUiMode
    val selectedCurrency = viewModel.selectedCurrency

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Settings", style = MaterialTheme.typography.h6)
                },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary,
                elevation = 8.dp
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Appearance section
            SectionTitle("Appearance")

            // UI Mode option
            SettingItem("UI Mode: ${selectedUiMode.name}", onClick = { showUiModeDialog = true })

            // Currencies section
            SectionTitle("Currencies")

            // Currency option
            SettingItem("Current Currency: ${selectedCurrency}", onClick = { showCurrencyDialog = true })

            // Dialog for selecting UI mode
            if (showUiModeDialog) {
                SelectionDialog(
                    title = "Select UI Mode",
                    options = UiMode.values().map { it.name },
                    selectedOption = selectedUiMode.name,
                    onOptionSelected = { selected ->
                        viewModel.putSelectedUiMode(UiMode.valueOf(selected))
                        showUiModeDialog = false
                    },
                    onDismiss = { showUiModeDialog = false }
                )
            }

            // Dialog for selecting Currency
            if (showCurrencyDialog) {
                SelectionDialog(
                    title = "Select Currency",
                    options = listOf(
                        "INR - Indian Rupee (₹)",
                        "USD - United States Dollar (\$)",
                        "GBP - Great British Pound (£)",
                        "EUR - Euro (€)",
                        "JPY - Japanese Yen (¥)"
                    ),
                    selectedOption = selectedCurrency,
                    onOptionSelected = { selected ->
                        val currencyCode = selected.split(" - ")[0]
                        viewModel.putSelectedCurrency(currencyCode)
                        showCurrencyDialog = false
                    },
                    onDismiss = { showCurrencyDialog = false }
                )
            }
        }
    }

    // Effect to handle UI mode changes
    LaunchedEffect(selectedUiMode) {
        when (selectedUiMode) {
            UiMode.Light -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            UiMode.Dark -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            UiMode.SystemDefault -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            else -> {}
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingItem(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        ListItem(
            text = {
                Text(text = text, style = MaterialTheme.typography.body1)
            }
        )
    }
}

@Composable
fun SelectionDialog(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title, style = MaterialTheme.typography.h6)
        },
        buttons = {
            Column {
                Divider()
                options.forEach { option ->
                    RadioOption(
                        text = option,
                        selected = option == selectedOption,
                        onSelect = { onOptionSelected(option) }
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            }
        }
    )
}

@Composable
fun RadioOption(
    text: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onSelect),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.body1)
    }
}
