package com.example.marketuniversitario.feature.analytics.ui.products

import androidx.compose.ui.graphics.Color
import com.github.tehras.charts.bar.BarChartData
import com.github.tehras.charts.piechart.PieChartData

sealed interface ProductsAnalyticsStatus {
    object Loading : ProductsAnalyticsStatus
    object Success : ProductsAnalyticsStatus
    data class Error(val message: String) : ProductsAnalyticsStatus
}

data class ProductsAnalyticsState(
    val isLoading: Boolean = true,
    val status: ProductsAnalyticsStatus = ProductsAnalyticsStatus.Loading,
    val totalProducts: Int = 0,
    val totalServices: Int = 0,
    val outOfStock: Int = 0,
    val pieChartData: PieChartData = PieChartData(listOf(PieChartData.Slice(1f, Color.LightGray))),
    val pieChartLabels: List<String> = emptyList(),
    val barChartLabels: List<String> = emptyList(),
    val barChartData: BarChartData = BarChartData(listOf(BarChartData.Bar(0f, Color.LightGray, "")))
)

sealed interface ProductsAnalyticsEvent {
    object Refresh : ProductsAnalyticsEvent
}
