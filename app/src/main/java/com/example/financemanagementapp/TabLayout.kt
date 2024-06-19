import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TabLayout(tabs: List<String>, selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    val isDarkTheme = isSystemInDarkTheme()
    val selectedTextColor = if (isDarkTheme) Color.White else Color.Black
    val unselectedTextColor = if (isDarkTheme) Color.LightGray else Color.DarkGray

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFF5722))
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
            contentColor = Color.White
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { onTabSelected(index) },
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTabIndex == index) selectedTextColor else unselectedTextColor
                        )
                    }
                )
            }
        }
    }
}

