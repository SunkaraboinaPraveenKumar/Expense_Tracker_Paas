package com.example.financemanagementapp
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    var showDialog by remember { mutableStateOf(false) }
    val selectedUiMode = viewModel.selectedUiMode

    Column {
        // Top App Bar with title "Settings"
        TopAppBar(
            title = {
                Text(text = "Settings")
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Content for Settings screen
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Appearance (non-clickable, non-selectable text)
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // UI Mode option
            ListItem(
                modifier = Modifier.clickable(onClick = { showDialog = true }),
                text = {
                    Text(
                        text = "UI Mode: ${selectedUiMode.name}",
                        style = MaterialTheme.typography.body1
                    )
                }
            )

            // Dialog for selecting UI mode
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = {
                        Text(text = "Select UI Mode")
                    },
                    buttons = {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { showDialog = false },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colors.surface
                                )
                            ) {
                                Text("Cancel")
                            }
                        }
                    },
                    text = {
                        Column {
                            RadioOption(
                                text = "Light",
                                selected = selectedUiMode == UiMode.Light,
                                onSelect = { viewModel.setSelectedUiMode(UiMode.Light) }
                            )
                            RadioOption(
                                text = "Dark",
                                selected = selectedUiMode == UiMode.Dark,
                                onSelect = { viewModel.setSelectedUiMode(UiMode.Dark) }
                            )
                            RadioOption(
                                text = "System Default",
                                selected = selectedUiMode == UiMode.SystemDefault,
                                onSelect = { viewModel.setSelectedUiMode(UiMode.SystemDefault) }
                            )
                        }
                    }
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
        }
    }
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
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}

enum class UiMode {
    Light, Dark, SystemDefault
}
