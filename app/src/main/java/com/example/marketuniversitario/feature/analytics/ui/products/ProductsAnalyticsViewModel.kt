package com.example.marketuniversitario.feature.analytics.ui.products

import androidx.compose.ui.graphics.Color

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.business.domain.repositories.ProductRepository
import com.github.tehras.charts.bar.BarChartData
import com.github.tehras.charts.piechart.PieChartData
import com.example.marketuniversitario.feature.analytics.ui.components.chartColorPalette
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsAnalyticsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ProductsAnalyticsState())
    val state: StateFlow<ProductsAnalyticsState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            productRepository.getProducts().collect { products ->
                val totalProducts = products.count { it.type.equals("PRODUCT", ignoreCase = true) }
                val totalServices = products.count { it.type.equals("SERVICE", ignoreCase = true) }
                val outOfStock = products.count { !it.isAvailable || (it.stock != null && it.stock == 0) }

                val pieSlices = mutableListOf<PieChartData.Slice>()
                val pieLabels = mutableListOf<String>()
                if (totalProducts > 0) {
                    pieSlices.add(PieChartData.Slice(totalProducts.toFloat(), chartColorPalette[0]))
                    pieLabels.add("Productos")
                }
                if (totalServices > 0) {
                    pieSlices.add(PieChartData.Slice(totalServices.toFloat(), chartColorPalette[1]))
                    pieLabels.add("Servicios")
                }
                val pieChartData = if (pieSlices.isNotEmpty()) PieChartData(pieSlices) else PieChartData(listOf(PieChartData.Slice(1f, Color.LightGray)))
                val finalPieLabels = if (pieSlices.isNotEmpty()) pieLabels else listOf("Sin Datos")

                val categoryGroups = products.groupingBy { it.category.takeIf { c -> c.isNotEmpty() } ?: "Sin Categoría" }.eachCount()
                val top5Categories = categoryGroups.entries.sortedByDescending { it.value }.take(5)
                
                val barLabels = mutableListOf<String>()
                val bars = top5Categories.mapIndexed { index, entry ->
                    barLabels.add(entry.key)
                    BarChartData.Bar(
                        value = entry.value.toFloat(),
                        color = chartColorPalette[(index + 2) % chartColorPalette.size],
                        label = entry.key
                    )
                }
                val barChartData = if (bars.isNotEmpty()) BarChartData(bars) else BarChartData(listOf(BarChartData.Bar(0f, Color.LightGray, "")))
                val finalBarLabels = if (bars.isNotEmpty()) barLabels else listOf("Sin Datos")

                _state.update {
                    it.copy(
                        isLoading = false,
                        status = ProductsAnalyticsStatus.Success,
                        totalProducts = totalProducts,
                        totalServices = totalServices,
                        outOfStock = outOfStock,
                        pieChartData = pieChartData,
                        pieChartLabels = finalPieLabels,
                        barChartLabels = finalBarLabels,
                        barChartData = barChartData
                    )
                }
            }
        }
    }

    fun onEvent(event: ProductsAnalyticsEvent) {
        when (event) {
            is ProductsAnalyticsEvent.Refresh -> {
                loadData()
            }
        }
    }
}
