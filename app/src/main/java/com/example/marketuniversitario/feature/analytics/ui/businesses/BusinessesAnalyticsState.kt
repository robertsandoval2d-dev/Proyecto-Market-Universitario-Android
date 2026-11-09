package com.example.marketuniversitario.feature.analytics.ui.businesses

import androidx.compose.ui.graphics.Color
import com.github.tehras.charts.bar.BarChartData
import com.github.tehras.charts.piechart.PieChartData

sealed interface BusinessesAnalyticsStatus {
    object Loading : BusinessesAnalyticsStatus
    object Success : BusinessesAnalyticsStatus
    data class Error(val message: String) : BusinessesAnalyticsStatus
}

data class BusinessesAnalyticsState(
    val isLoading: Boolean = true,
    val status: BusinessesAnalyticsStatus = BusinessesAnalyticsStatus.Loading,
    val totalBusinesses: Int = 0,
    val activeBusinesses: Int = 0,
    val pieChartData: PieChartData = PieChartData(listOf(PieChartData.Slice(1f, Color.LightGray))),
    val pieChartLabels: List<String> = emptyList(),
    val barChartLabels: List<String> = emptyList(),
    val barChartData: BarChartData = BarChartData(listOf(BarChartData.Bar(0f, Color.LightGray, "")))
)

sealed interface BusinessesAnalyticsEvent {
    object Refresh : BusinessesAnalyticsEvent
}
