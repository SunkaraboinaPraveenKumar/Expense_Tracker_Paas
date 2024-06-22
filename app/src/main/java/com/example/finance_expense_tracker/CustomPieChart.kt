package com.example.finance_expense_tracker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

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
        Color(0xFF4169E1), // Royal Blue
        Color(0xFF228B22), // Forest Green
        Color(0xFFFF6347), // Tomato Red
        Color(0xFF00BFFF), // Deep Sky Blue
        Color(0xFFDAA520), // Goldenrod
        Color(0xFFBF00FF), // Electric Purple
        Color(0xFF00FFFF), // Aqua
        Color(0xFFFF7F50), // Coral
        Color(0xFF00FF7F), // Spring Green
        Color(0xFF40E0D0), // Turquoise
        Color(0xFF696969), // Dim Gray
        Color(0xFF708090), // Slate Gray
        Color(0xFF778899), // Light Slate Gray
        Color(0xFFE6E6FA), // Lavender
        Color(0xFFFFD1DC), // Pastel Pink
        Color(0xFFAEC6CF), // Pastel Blue
        Color(0xFF77DD77), // Pastel Green
        Color(0xFFFDFD96), // Pastel Yellow
        Color(0xFFFFB347), // Pastel Orange
        Color(0xFFFF00FF), // Magenta
        Color(0xFF00FF00), // Lime
        Color(0xFF00FFFF), // Cyan
        Color(0xFFFF4500), // Orange Red
        Color(0xFF7FFF00)  // Chartreuse
    )

    // State to track selected category
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val colorMap = remember {
        data.mapIndexed { index, pair ->
            pair.first to colors[index % colors.size]
        }.toMap()
    }

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
                    .clickable {
                        selectedCategory = null // Clear selection on canvas click
                    }
            ) {
                var currentAngle = startAngle
                data.forEach { (label, value) ->
                    val isSelected = label == selectedCategory
                    val sweepAngle = (value / totalValue) * 360f
                    val elevation = if (isSelected) (-10).dp else 0.dp // Elevation for selected slice
                    val arcSize = size.width - elevation.toPx() * 2
                    val arcStart = Offset(
                        elevation.toPx(),
                        elevation.toPx()
                    )
                    drawArc(
                        color = if (isSelected) colorMap[label]!!.copy(alpha = 0.8f) else colorMap[label]!!,
                        startAngle = currentAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = arcStart,
                        size = Size(arcSize, arcSize),
                        style = androidx.compose.ui.graphics.drawscope.Fill
                    )

                    // Calculate text position for label
                    val labelAngle = Math.toRadians((currentAngle + sweepAngle / 2).toDouble())
                    val cosLabel = cos(labelAngle).toFloat()
                    val sinLabel = sin(labelAngle).toFloat()
                    val labelOffset = 24.dp // Distance from the pie chart
                    val labelX = size.width / 2 + (arcSize / 2 + labelOffset.toPx()) * cosLabel
                    val labelY = size.height / 2 + (arcSize / 2 + labelOffset.toPx()) * sinLabel

                    // Calculate text position for percentage
                    val percentageAngle = Math.toRadians((currentAngle + sweepAngle / 2).toDouble())
                    val cosPercentage = cos(percentageAngle).toFloat()
                    val sinPercentage = sin(percentageAngle).toFloat()
                    val percentageOffset = 48.dp // Distance from the pie chart
                    val percentageX = size.width / 2 + (arcSize / 2 + percentageOffset.toPx()) * cosPercentage
                    val percentageY = size.height / 2 + (arcSize / 2 + percentageOffset.toPx()) * sinPercentage

                    // Draw label and percentage text
                    if (isSelected) {
                        val labelText = label
                        val percentageText = "${"%.1f".format((value / totalValue) * 100)}%"

                        // Calculate text position
                        val textAngle = Math.toRadians((currentAngle + sweepAngle / 2).toDouble())
                        val cosText = cos(textAngle).toFloat()
                        val sinText = sin(textAngle).toFloat()
                        val textOffset = 36.dp // Distance from the pie chart
                        val textX = size.width / 2 + (arcSize / 2 + textOffset.toPx()) * cosText
                        val textY = size.height / 2 + (arcSize / 2 + textOffset.toPx()) * sinText

                        // Draw both label and percentage text
                        drawContext.canvas.nativeCanvas.drawText(
                            labelText,
                            textX,
                            textY,
                            android.graphics.Paint().apply {
                                color = android.graphics.Color.BLACK
                                textSize = 20.sp.toPx()
                                textAlign = android.graphics.Paint.Align.CENTER
                            }
                        )
                        drawContext.canvas.nativeCanvas.drawText(
                            percentageText,
                            textX,
                            textY + 24.dp.toPx(), // Move percentage text below label text
                            android.graphics.Paint().apply {
                                color = android.graphics.Color.BLACK
                                textSize = 16.sp.toPx()
                                textAlign = android.graphics.Paint.Align.CENTER
                            }
                        )
                    }

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
                val isSelected = label == selectedCategory
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clickable {
                            selectedCategory = if (selectedCategory == label) null else label
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(colorMap[label] ?: Color.Gray, shape = CircleShape)
                            .clip(CircleShape)
                            .background(if (isSelected) Color.LightGray else Color.Transparent)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        color = if (isSelected) colorMap[label]!!.copy(alpha = 0.8f) else Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .fillMaxWidth(0.7f)
                            .clip(CircleShape)
                    ) {
                        LinearProgressIndicator(
                            progress = value / totalValue,
                            color = if (isSelected) colorMap[label]!!.copy(alpha = 0.8f) else colorMap[label]!!,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${"%.1f".format((value / totalValue) * 100)}%",
                        color = if (isSelected) colorMap[label]!!.copy(alpha = 0.8f) else Color.Black
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
