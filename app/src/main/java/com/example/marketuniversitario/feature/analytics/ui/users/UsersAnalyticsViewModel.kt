package com.example.marketuniversitario.feature.analytics.ui.users

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.user.domain.repositories.UserRepository
import com.github.tehras.charts.bar.BarChartData
import com.example.marketuniversitario.feature.analytics.ui.components.chartColorPalette
import com.github.tehras.charts.piechart.PieChartData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersAnalyticsViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(UsersAnalyticsState())
    val state: StateFlow<UsersAnalyticsState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            userRepository.getAllUsers().collect { users ->
                val totalUsers = users.size
                val usersWithBusiness = users.count { it.hasBusiness }

                // PieChart by faculty
                val facultyGroups = users.groupingBy { 
                    it.faculty?.takeIf { f -> f.isNotEmpty() } ?: "Sin Facultad" 
                }.eachCount()
                
                val pieLabels = mutableListOf<String>()
                val pieSlices = facultyGroups.entries.mapIndexed { index, entry ->
                    pieLabels.add(entry.key)
                    PieChartData.Slice(
                        value = entry.value.toFloat(),
                        color = chartColorPalette[index % chartColorPalette.size]
                    )
                }
                val pieChartData = if (pieSlices.isNotEmpty()) PieChartData(pieSlices) else PieChartData(listOf(PieChartData.Slice(1f, Color.LightGray)))
                val finalPieLabels = if (pieSlices.isNotEmpty()) pieLabels else listOf("Sin Datos")

                // BarChart by primaryIntent
                val intentGroups = users.groupingBy { 
                    it.primaryIntent?.takeIf { i -> i.isNotEmpty() } ?: "No Especificado" 
                }.eachCount()
                
                val barLabels = mutableListOf<String>()
                val bars = intentGroups.entries.mapIndexed { index, entry ->
                    barLabels.add(entry.key)
                    BarChartData.Bar(
                        value = entry.value.toFloat(),
                        color = chartColorPalette[(index + 3) % chartColorPalette.size],
                        label = entry.key
                    )
                }
                val barChartData = if (bars.isNotEmpty()) BarChartData(bars) else BarChartData(listOf(BarChartData.Bar(0f, Color.LightGray, "")))
                val finalBarLabels = if (bars.isNotEmpty()) barLabels else listOf("Sin Datos")

                _state.update {
                    it.copy(
                        isLoading = false,
                        status = UsersAnalyticsStatus.Success,
                        totalUsers = totalUsers,
                        usersWithBusiness = usersWithBusiness,
                        pieChartData = pieChartData,
                        pieChartLabels = finalPieLabels,
                        barChartLabels = finalBarLabels,
                        barChartData = barChartData
                    )
                }
            }
        }
    }
    
    fun onEvent(event: UsersAnalyticsEvent) {
        when (event) {
            is UsersAnalyticsEvent.Refresh -> {
                loadData()
            }
        }
    }
}
