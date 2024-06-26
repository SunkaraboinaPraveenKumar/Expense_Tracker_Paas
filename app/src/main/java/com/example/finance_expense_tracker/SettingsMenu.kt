import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financemanagementapp.R

@Composable
fun SettingsMenu(
    onSettingsClick: () -> Unit,
    onDeleteResetClick: () -> Unit,
    onHelpClick: () -> Unit,
    onAddNewCategoriesClick: () -> Unit,
    onLogoutClick: () -> Unit // New parameter for logout
) {
    Column {
        Text(
            text = "Finance Management App",
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp)
        )
        Divider(color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
                .clickable { onSettingsClick() }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.settings),
                contentDescription = "Settings",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Settings")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
                .clickable { onDeleteResetClick() }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.delete),
                contentDescription = "Delete and Reset",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Delete")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
                .clickable { onHelpClick() }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.question),
                contentDescription = "Help",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Help")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
                .clickable { onAddNewCategoriesClick() }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Add New Categories",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Add New Categories")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
                .clickable { onLogoutClick() } // New clickable row for logout
        ) {
            Icon(
                painter = painterResource(id = R.drawable.logout),
                contentDescription = "Logout",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Logout")
        }
    }
}

@Composable
fun AddNewCategoryDialog(
    onDismiss: () -> Unit,
    onSaveCategory: (String, Int, Boolean) -> Unit
) {
    val icons = remember { mutableStateListOf<Int>() }
    var isIncome by remember { mutableStateOf(true) }
    var categoryName by remember { mutableStateOf(TextFieldValue()) }
    var selectedIconIndex by remember { mutableStateOf(0) }

    val incomeIcons = listOf(
        R.drawable.farming,
        R.drawable.realestate,
        R.drawable.internship,
        R.drawable.gifts,
        R.drawable.scholarship,
        R.drawable.stocks,
        R.drawable.business,
        R.drawable.content_creation,
        R.drawable.trophy,
        R.drawable.coupons,
        R.drawable.grants,
        R.drawable.lottery,
        R.drawable.refund,
        R.drawable.rental,
        R.drawable.salary,
        R.drawable.sale
    )
    val expenseIcons = listOf(
        R.drawable.donate,
        R.drawable.fuel,
        R.drawable.furniture,
        R.drawable.gas,
        R.drawable.tax,
        R.drawable.vacation,
        R.drawable.salary,
        R.drawable.stationary,
        R.drawable.wifi_bill,
        R.drawable.milk_bottle,
        R.drawable.beauty,
        R.drawable.bill,
        R.drawable.car_wash,
        R.drawable.clothes_hanger,
        R.drawable.education,
        R.drawable.cpu,
        R.drawable.confetti,
        R.drawable.diet,
        R.drawable.better_health,
        R.drawable.house,
        R.drawable.insurance,
        R.drawable.bag,
        R.drawable.social_media,
        R.drawable.trophy,
        R.drawable.transportation
    )

    LaunchedEffect(isIncome) {
        icons.clear()
        icons.addAll(if (isIncome) incomeIcons else expenseIcons)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Add New Category", style = androidx.compose.material.MaterialTheme.typography.h5)
        },
        text = {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Category Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = "Type:", style = androidx.compose.material.MaterialTheme.typography.body1)
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Expense",
                            style = androidx.compose.material.MaterialTheme.typography.body1
                        )
                        Switch(
                            checked = isIncome,
                            onCheckedChange = { isIncome = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.onBackground,
                                uncheckedThumbColor = MaterialTheme.colorScheme.onBackground,
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Text(
                            text = "Income",
                            style = androidx.compose.material.MaterialTheme.typography.body1
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Select Icon:", style = androidx.compose.material.MaterialTheme.typography.body1)
                Spacer(modifier = Modifier.height(8.dp))
                LazyHorizontalGrid(
                    rows= GridCells.Fixed(2),
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .height(140.dp)
                ) {
                    items(icons.size) { index ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    selectedIconIndex = index
                                }
                                .size(56.dp)
                                .background(
                                    color = if (selectedIconIndex == index) MaterialTheme.colorScheme.primary.copy(
                                        alpha = 0.2f
                                    ) else Color.Transparent,
                                    shape = CircleShape
                                )
                                .border(
                                    width = 2.dp,
                                    color = if (selectedIconIndex == index) MaterialTheme.colorScheme.primary else Color.Gray,
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                painter = painterResource(id = icons[index]),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = Color.Unspecified
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveCategory(categoryName.text, icons[selectedIconIndex], isIncome)
                    onDismiss()
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Save")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Cancel")
            }
        }
    )
}


