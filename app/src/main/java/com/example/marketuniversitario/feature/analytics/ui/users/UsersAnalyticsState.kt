package com.example.marketuniversitario.feature.analytics.ui.users

import androidx.compose.ui.graphics.Color
import com.github.tehras.charts.bar.BarChartData
import com.github.tehras.charts.piechart.PieChartData

sealed interface UsersAnalyticsStatus {
    object Loading : UsersAnalyticsStatus
    object Success : UsersAnalyticsStatus
    data class Error(val message: String) : UsersAnalyticsStatus
}

data class UsersAnalyticsState(
    val isLoading: Boolean = true,
    val status: UsersAnalyticsStatus = UsersAnalyticsStatus.Loading,
    val totalUsers: Int = 0,
    val usersWithBusiness: Int = 0,
    val pieChartData: PieChartData = PieChartData(listOf(PieChartData.Slice(1f, Color.LightGray))),
    val pieChartLabels: List<String> = emptyList(),
    val barChartLabels: List<String> = emptyList(),
    val barChartData: BarChartData = BarChartData(listOf(BarChartData.Bar(0f, Color.LightGray, "")))
)

sealed interface UsersAnalyticsEvent {
    object Refresh : UsersAnalyticsEvent
}
