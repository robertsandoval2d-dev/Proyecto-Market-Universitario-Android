package com.market.univpe.feature.analytics.ui.businesses

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.market.univpe.feature.business.domain.repositories.BusinessRepository
import com.github.tehras.charts.bar.BarChartData
import com.github.tehras.charts.piechart.PieChartData
import com.market.univpe.feature.analytics.ui.components.chartColorPalette
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BusinessesAnalyticsViewModel @Inject constructor(
    private val businessRepository: BusinessRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(BusinessesAnalyticsState())
    val state: StateFlow<BusinessesAnalyticsState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            businessRepository.getAllBusinesses().collect { businesses ->
                val totalBusinesses = businesses.size
                val activeBusinesses = businesses.count { it.isActive }

                // PieChart by Category
                val categoryGroups = businesses.groupingBy { 
                    it.categories.firstOrNull() ?: "Sin Categoría" 
                }.eachCount()

                val pieLabels = mutableListOf<String>()
                val pieSlices = categoryGroups.entries.mapIndexed { index, entry ->
                    pieLabels.add(entry.key)
                    PieChartData.Slice(
                        value = entry.value.toFloat(),
                        color = chartColorPalette[index % chartColorPalette.size]
                    )
                }
                val pieChartData = if (pieSlices.isNotEmpty()) PieChartData(pieSlices) else PieChartData(listOf(PieChartData.Slice(1f, Color.LightGray)))
                val finalPieLabels = if (pieSlices.isNotEmpty()) pieLabels else listOf("Sin Datos")

                // BarChart by Weighted Rating (Top 5 - IMDb Formula)
                val m = 5.0 // Umbral mínimo de reseñas de confianza
                
                // Filtramos para considerar solo negocios que tengan al menos 1 reseña
                val businessesWithReviews = businesses.filter { it.totalReviews > 0 }
                
                val C = if (businessesWithReviews.isNotEmpty()) businessesWithReviews.map { it.rating }.average() else 0.0

                val top5Rated = businessesWithReviews.map { business ->
                    val v = business.totalReviews.toDouble()
                    val R = business.rating
                    val weightedScore = if (v + m > 0) ((v * R) + (m * C)) / (v + m) else 0.0
                    Pair(business, weightedScore)
                }.sortedByDescending { it.second }.take(5)

                val barLabels = mutableListOf<String>()
                val bars = top5Rated.mapIndexed { index, pair ->
                    val business = pair.first
                    val weightedScore = pair.second
                    
                    // Mostramos el Puntaje Justo (Ponderado) directamente en la etiqueta
                    val labelText = "${business.name.ifBlank { "Negocio ${index+1}" }} (${String.format(
                        Locale.US, "%.2f", weightedScore)} ⭐)"
                    barLabels.add(labelText)
                    
                    BarChartData.Bar(
                        value = weightedScore.toFloat(), // El gráfico se dibuja en base al puntaje ponderado
                        color = chartColorPalette[(index + 4) % chartColorPalette.size],
                        label = labelText
                    )
                }
                val barChartData = if (bars.isNotEmpty()) BarChartData(bars) else BarChartData(listOf(BarChartData.Bar(0f, Color.LightGray, "")))
                val finalBarLabels = if (bars.isNotEmpty()) barLabels else listOf("Sin Datos")

                _state.update {
                    it.copy(
                        isLoading = false,
                        status = BusinessesAnalyticsStatus.Success,
                        totalBusinesses = totalBusinesses,
                        activeBusinesses = activeBusinesses,
                        pieChartData = pieChartData,
                        pieChartLabels = finalPieLabels,
                        barChartLabels = finalBarLabels,
                        barChartData = barChartData
                    )
                }
            }
        }
    }
    
    fun onEvent(event: BusinessesAnalyticsEvent) {
        when (event) {
            is BusinessesAnalyticsEvent.Refresh -> {
                loadData()
            }
        }
    }
}
