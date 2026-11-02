package com.example.marketuniversitario.feature.reports.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.reports.domain.models.Report
import com.example.marketuniversitario.feature.reports.domain.models.ReportStatus as DomainReportStatus
import com.example.marketuniversitario.feature.reports.domain.usecases.CreateReportUseCase
import com.example.marketuniversitario.feature.reports.domain.usecases.GetReportByOrderIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val createReportUseCase: CreateReportUseCase,
    private val getReportByOrderIdUseCase: GetReportByOrderIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ReportState())
    val state = _state.asStateFlow()

    fun onEvent(event: ReportEvent) {
        when (event) {
            is ReportEvent.InitData -> {
                _state.update {
                    it.copy(
                        orderId = event.orderId,
                        businessId = event.businessId,
                        studentId = event.studentId,
                        orderStatusAtReport = event.orderStatusAtReport
                    )
                }
                checkExistingReport(event.orderId)
            }
            is ReportEvent.ReasonChanged -> {
                _state.update { it.copy(reasonCategory = event.reason) }
            }
            is ReportEvent.DescriptionChanged -> {
                if (event.description.length <= 300) {
                    _state.update { it.copy(description = event.description) }
                }
            }
            is ReportEvent.SubmitReport -> {
                submitReport()
            }
            is ReportEvent.ResetStatus -> {
                _state.update { it.copy(status = ReportStatus.Idle) }
            }
            is ReportEvent.ResetForm -> {
                _state.update { ReportState() }
            }
        }
    }

    private fun checkExistingReport(orderId: String) {
        if (orderId.isBlank()) return
        viewModelScope.launch {
            getReportByOrderIdUseCase(orderId)
                .onSuccess { report ->
                    _state.update { it.copy(existingReport = report) }
                }
        }
    }

    private fun submitReport() {
        val currentState = _state.value
        if (currentState.orderId.isBlank() || currentState.studentId.isBlank() || currentState.description.isBlank()) {
            _state.update { it.copy(status = ReportStatus.Error("Por favor completa los campos requeridos")) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(status = ReportStatus.Loading) }
            val report = Report(
                id = "",
                orderId = currentState.orderId,
                businessId = currentState.businessId,
                studentId = currentState.studentId,
                reasonCategory = currentState.reasonCategory,
                description = currentState.description.trim(),
                status = DomainReportStatus.PENDING,
                orderStatusAtReport = currentState.orderStatusAtReport,
                adminResolutionNotes = null,
                createdAt = System.currentTimeMillis()
            )

            createReportUseCase(report)
                .onSuccess {
                    _state.update { it.copy(status = ReportStatus.Success) }
                }
                .onFailure { error ->
                    _state.update { it.copy(status = ReportStatus.Error(error.message ?: "Error al enviar el reporte")) }
                }
        }
    }
}
