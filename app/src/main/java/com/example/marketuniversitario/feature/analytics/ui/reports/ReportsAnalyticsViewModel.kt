package com.example.marketuniversitario.feature.analytics.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.reports.domain.models.ReportStatus
import com.example.marketuniversitario.feature.reports.domain.repositories.ReportRepository
import com.github.tehras.charts.bar.BarChartData
import com.github.tehras.charts.piechart.PieChartData
import com.example.marketuniversitario.feature.analytics.ui.components.chartColorPalette
import androidx.compose.ui.graphics.Color
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportsAnalyticsViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ReportsAnalyticsState())
    val state: StateFlow<ReportsAnalyticsState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            reportRepository.getAllReports().collect { reports ->
                val totalReports = reports.size
                val pendingReports = reports.count { it.status == ReportStatus.PENDING }
                val resolvedReports = reports.count { it.status == ReportStatus.RESOLVED }
                val rejectedReports = reports.count { it.status == ReportStatus.REJECTED }

                val pieSlices = mutableListOf<PieChartData.Slice>()
                val pieLabels = mutableListOf<String>()
                if (pendingReports > 0) {
                    pieSlices.add(PieChartData.Slice(pendingReports.toFloat(), chartColorPalette[5])) // Dark Orange
                    pieLabels.add("Pendientes")
                }
                if (resolvedReports > 0) {
                    pieSlices.add(PieChartData.Slice(resolvedReports.toFloat(), chartColorPalette[1])) // Green
                    pieLabels.add("Resueltos")
                }
                if (rejectedReports > 0) {
                    pieSlices.add(PieChartData.Slice(rejectedReports.toFloat(), chartColorPalette[2])) // Red
                    pieLabels.add("Rechazados")
                }
                val pieChartData = if (pieSlices.isNotEmpty()) PieChartData(pieSlices) else PieChartData(listOf(PieChartData.Slice(1f, Color.LightGray)))
                val finalPieLabels = if (pieSlices.isNotEmpty()) pieLabels else listOf("Sin Datos")

                val reasonGroups = reports.groupingBy { it.reasonCategory.takeIf { r -> r.isNotEmpty() } ?: "Sin Razón" }.eachCount()
                val top5Reasons = reasonGroups.entries.asSequence().sortedByDescending { it.value }.take(5).toList()

                val barLabels = mutableListOf<String>()
                val bars = top5Reasons.mapIndexed { index, entry ->
                    barLabels.add(entry.key)
                    BarChartData.Bar(
                        value = entry.value.toFloat(),
                        color = chartColorPalette[(index + 4) % chartColorPalette.size],
                        label = entry.key
                    )
                }
                val barChartData = if (bars.isNotEmpty()) BarChartData(bars) else BarChartData(listOf(BarChartData.Bar(0f, Color.LightGray, "")))
                val finalBarLabels = if (bars.isNotEmpty()) barLabels else listOf("Sin Datos")

                _state.update {
                    it.copy(
                        isLoading = false,
                        status = ReportsAnalyticsStatus.Success,
                        totalReports = totalReports,
                        pendingReports = pendingReports,
                        resolvedReports = resolvedReports,
                        pieChartData = pieChartData,
                        pieChartLabels = finalPieLabels,
                        barChartLabels = finalBarLabels,
                        barChartData = barChartData
                    )
                }
            }
        }
    }
    
    fun onEvent(event: ReportsAnalyticsEvent) {
        when (event) {
            is ReportsAnalyticsEvent.Refresh -> {
                loadData()
            }
        }
    }
}
