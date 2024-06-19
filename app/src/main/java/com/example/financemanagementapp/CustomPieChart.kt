package com.example.financemanagementapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CustomPieChart(
    data: List<Pair<String, Float>>,
    modifier: Modifier = Modifier,
    analysisType: String,
    onBack: () -> Unit
) {
    val totalValue = data.sumOf { it.second }
    val startAngle = -90f
    val colors = listOf(
        // Primary Colors
        Color(0xFF4169E1), // Royal Blue
        Color(0xFF228B22), // Forest Green
        Color(0xFFFF6347), // Tomato Red
        Color(0xFF00BFFF), // Deep Sky Blue
        Color(0xFFDAA520), // Goldenrod

        // Accent Colors
        Color(0xFFBF00FF), // Electric Purple
        Color(0xFF00FFFF), // Aqua
        Color(0xFFFF7F50), // Coral
        Color(0xFF00FF7F), // Spring Green
        Color(0xFF40E0D0), // Turquoise

        // Neutrals
        Color(0xFF696969), // Dim Gray
        Color(0xFF708090), // Slate Gray
        Color(0xFF778899), // Light Slate Gray
        Color(0xFFE6E6FA), // Lavender

        // Pastel Colors
        Color(0xFFFFD1DC), // Pastel Pink
        Color(0xFFAEC6CF), // Pastel Blue
        Color(0xFF77DD77), // Pastel Green
        Color(0xFFFDFD96), // Pastel Yellow
        Color(0xFFFFB347), // Pastel Orange

        // Vibrant Colors
        Color(0xFFFF00FF), // Magenta
        Color(0xFF00FF00), // Lime
        Color(0xFF00FFFF), // Cyan
        Color(0xFFFF4500), // Orange Red
        Color(0xFF7FFF00)  // Chartreuse
    )

    val colorMap = remember { data.mapIndexed { index, pair -> pair.first to colors[index % colors.size] }.toMap() }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "$analysisType Analysis",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color(0xFF008080)
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            Canvas(
                modifier = Modifier
                    .size(200.dp)
                    .padding(8.dp)
            ) {
                var currentAngle = startAngle
                data.forEach { (label, value) ->
                    val sweepAngle = (value / totalValue) * 360f
                    drawArc(
                        color = colorMap[label] ?: Color.Gray,
                        startAngle = currentAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = Offset.Zero,
                        size = Size(size.width, size.height),
                        style = androidx.compose.ui.graphics.drawscope.Fill
                    )
                    currentAngle += sweepAngle
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            data.forEach { (label, value) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(colorMap[label] ?: Color.Gray, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .fillMaxWidth(0.7f) // Adjust the width to accommodate the percentage text
                            .clip(CircleShape)
                    ) {
                        LinearProgressIndicator(
                            progress = value / totalValue,
                            color = colorMap[label] ?: Color.Gray,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${"%.1f".format((value / totalValue) * 100)}%",
                        color = Color.Black
                    )
                }
            }
        }
    }
}

private fun <T> Iterable<T>.sumOf(selector: (T) -> Float): Float {
    var sum = 0f
    for (element in this) {
        sum += selector(element)
    }
    return sum
}