package com.example.marketuniversitario.feature.analytics.ui.reports

import androidx.compose.ui.graphics.Color
import com.github.tehras.charts.bar.BarChartData
import com.github.tehras.charts.piechart.PieChartData

sealed interface ReportsAnalyticsStatus {
    object Loading : ReportsAnalyticsStatus
    object Success : ReportsAnalyticsStatus
    data class Error(val message: String) : ReportsAnalyticsStatus
}

data class ReportsAnalyticsState(
    val isLoading: Boolean = true,
    val status: ReportsAnalyticsStatus = ReportsAnalyticsStatus.Loading,
    val totalReports: Int = 0,
    val pendingReports: Int = 0,
    val resolvedReports: Int = 0,
    val pieChartData: PieChartData = PieChartData(listOf(PieChartData.Slice(1f, Color.LightGray))),
    val pieChartLabels: List<String> = emptyList(),
    val barChartLabels: List<String> = emptyList(),
    val barChartData: BarChartData = BarChartData(listOf(BarChartData.Bar(0f, Color.LightGray, "")))
)

sealed interface ReportsAnalyticsEvent {
    object Refresh : ReportsAnalyticsEvent
}
