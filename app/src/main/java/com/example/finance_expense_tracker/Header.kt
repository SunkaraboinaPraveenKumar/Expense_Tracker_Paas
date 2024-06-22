package com.example.finance_expense_tracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financemanagementapp.R
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Header(currentDate: YearMonth, onPrevClick: () -> Unit, onNextClick: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("MMMM, yyyy")
    val formattedDate = currentDate.format(formatter)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFF5722))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevClick) {
            Image(
                painter = painterResource(id = R.drawable.ic_chevron_left),
                contentDescription = "Previous Date",
            )
        }

        Text(
            text = formattedDate,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        IconButton(onClick = onNextClick) {
            Image(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = "Next Date",
            )
        }
    }
}
