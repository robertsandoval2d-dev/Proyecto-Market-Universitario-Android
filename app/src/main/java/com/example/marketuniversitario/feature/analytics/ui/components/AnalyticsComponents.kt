package com.example.marketuniversitario.feature.analytics.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.tehras.charts.bar.BarChart
import com.github.tehras.charts.bar.BarChartData
import com.github.tehras.charts.bar.renderer.label.SimpleValueDrawer
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.max
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.foundation.shape.RoundedCornerShape
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer
import com.github.tehras.charts.piechart.renderer.SliceDrawer
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.graphics.Color

val chartColorPalette = listOf(
    Color(0xFF1976D2), // Blue
    Color(0xFF388E3C), // Green
    Color(0xFFD32F2F), // Red
    Color(0xFF7B1FA2), // Purple
    Color(0xFF00796B), // Teal
    Color(0xFFF57C00), // Dark Orange
    Color(0xFF5D4037), // Brown
    Color(0xFF455A64), // Blue Grey
    Color(0xFFC2185B), // Dark Pink
    Color(0xFF0277BD)  // Light Blue Dark
)

@Composable
fun KpiCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardPieChart(
    pieChartData: PieChartData,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PieChart(
            pieChartData = pieChartData,
            modifier = Modifier.size(200.dp),
            sliceDrawer = TextInsideSliceDrawer()
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            labels.forEachIndexed { index, label ->
                val slice = pieChartData.slices.getOrNull(index)
                val sliceColor = slice?.color ?: MaterialTheme.colorScheme.onSurface
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 16.dp, bottom = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(sliceColor, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardBarChart(
    barChartData: BarChartData,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    var selectedBar by remember { mutableStateOf<BarChartData.Bar?>(null) }
    var tapOffset by remember { mutableStateOf(Offset.Zero) }
    
    // Calculamos el texto más largo para determinar el ancho ideal (sin truncar)
    val maxTextLength = labels.maxOfOrNull { it.length } ?: 10
    // Asumimos un espaciado aproximado de 8.dp por cada carácter de la etiqueta
    val widthPerBar = (maxTextLength * 8).dp
    val totalRequiredWidth = widthPerBar * barChartData.bars.size
    
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    // Reemplazamos las etiquetas truncadas que vienen del BarChartData original por las reales,
    // para que en el XAxis (abajo del gráfico) se lea la palabra completa.
    val fullLabelsBars = barChartData.bars.mapIndexed { index, bar ->
        bar.copy(label = labels.getOrNull(index) ?: bar.label)
    }
    val fullLabelsBarChartData = barChartData.copy(bars = fullLabelsBars)

    Row(
        modifier = modifier.horizontalScroll(scrollState),
        // Si no usamos fillMaxWidth en este row y dejamos que el hijo crezca, evitamos el centrado raro.
        // Pero usamos maxOf(totalRequiredWidth, screenWidth) en el width de abajo para que nunca quede
        // colapsado si es menor a la pantalla
    ) {
        BarChart(
            barChartData = fullLabelsBarChartData,
            modifier = Modifier
                .width(max(totalRequiredWidth, screenWidth))
                .height(250.dp)
                .padding(end = 16.dp)
                .pointerInput(fullLabelsBarChartData) {
                    detectTapGestures(
                        onPress = { selectedBar = null }, // Se oculta al tocar cualquier otro lado o scrollear
                        onTap = { offset ->
                            val barSectionWidth = size.width / fullLabelsBarChartData.bars.size
                            if (barSectionWidth > 0) {
                                val index = (offset.x / barSectionWidth).toInt()
                                if (index in fullLabelsBarChartData.bars.indices) {
                                    selectedBar = fullLabelsBarChartData.bars[index]
                                    tapOffset = offset
                                }
                            }
                        }
                    )
                },
            labelDrawer = SimpleValueDrawer(
                drawLocation = SimpleValueDrawer.DrawLocation.XAxis
            )
        )
        
        selectedBar?.let { bar ->
            val index = fullLabelsBarChartData.bars.indexOf(bar)
            val fullLabel = labels.getOrNull(index) ?: bar.label

            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(tapOffset.x.toInt(), tapOffset.y.toInt()),
                onDismissRequest = { selectedBar = null }
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    shadowElevation = 8.dp,
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = fullLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Valor: ${bar.value.toInt()}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

class TextInsideSliceDrawer(
    sliceThickness: Float = 60f,
    textColor: Int = android.graphics.Color.WHITE,
    textSize: Float = 36f
) : SliceDrawer {
    private val delegate = SimpleSliceDrawer(sliceThickness)
    private val textPaint = Paint().apply {
        color = textColor
        this.textSize = textSize
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    override fun drawSlice(
        drawScope: DrawScope,
        canvas: Canvas,
        area: Size,
        startAngle: Float,
        sweepAngle: Float,
        slice: PieChartData.Slice
    ) {
        delegate.drawSlice(drawScope, canvas, area, startAngle, sweepAngle, slice)

        if (sweepAngle > 15f && slice.value > 0) { // Solo dibuja si el slice es suficientemente grande y válido
            val radius = minOf(area.width, area.height) / 2f
            
            // Radio para ubicar el texto dentro del anillo del donut
            // Un valor como 0.65f lo coloca bien centrado en el interior para donut
            val textRadius = radius * 0.65f

            val midAngle = startAngle + (sweepAngle / 2f)
            val midAngleRad = Math.toRadians(midAngle.toDouble())

            val cx = area.width / 2f
            val cy = area.height / 2f

            val x = cx + (textRadius * cos(midAngleRad)).toFloat()
            val y = cy + (textRadius * sin(midAngleRad)).toFloat()

            val yOffset = (textPaint.descent() + textPaint.ascent()) / 2f
            val valueString = slice.value.toInt().toString()

            canvas.nativeCanvas.drawText(valueString, x, y - yOffset, textPaint)
        }
    }
}

