package com.example.marketuniversitario.feature.reports.ui.admin_reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.reports.domain.models.Report
import com.example.marketuniversitario.feature.reports.domain.usecases.GetAdminReportContactDetailsUseCase
import com.example.marketuniversitario.feature.reports.domain.usecases.GetAllReportsUseCase
import com.example.marketuniversitario.feature.reports.domain.usecases.ResolveReportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminReportsViewModel @Inject constructor(
    private val getAllReportsUseCase: GetAllReportsUseCase,
    private val resolveReportUseCase: ResolveReportUseCase,
    private val getAdminReportContactDetailsUseCase: GetAdminReportContactDetailsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AdminReportsState())
    val state = _state.asStateFlow()

    init {
        loadReports()
    }

    fun onEvent(event: AdminReportsEvent) {
        when (event) {
            is AdminReportsEvent.FilterChanged -> {
                _state.update { it.copy(selectedFilter = event.filter) }
            }
            is AdminReportsEvent.SelectReport -> {
                _state.update {
                    it.copy(
                        selectedReport = event.report,
                        adminNotes = event.report?.adminResolutionNotes.orEmpty(),
                        selectedReportContacts = ContactDetails()
                    )
                }
                if (event.report != null) {
                    loadContactDetails(event.report)
                }
            }
            is AdminReportsEvent.AdminNotesChanged -> {
                _state.update { it.copy(adminNotes = event.notes) }
            }
            is AdminReportsEvent.RejectReport -> {
                executeResolution(resolutionStatus = "REJECTED", cancelOrder = false)
            }
            is AdminReportsEvent.ResolveAndCancelOrder -> {
                executeResolution(resolutionStatus = "RESOLVED", cancelOrder = true)
            }
            is AdminReportsEvent.ClearMessages -> {
                _state.update { it.copy(errorMessage = null, successMessage = null) }
            }
        }
    }

    private fun loadReports() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getAllReportsUseCase()
                .catch { e ->
                    _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error al cargar reportes") }
                }
                .collect { reports ->
                    _state.update {
                        it.copy(
                            reports = reports,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun loadContactDetails(report: Report) {
        viewModelScope.launch {
            val contactDetails = getAdminReportContactDetailsUseCase(report)
            _state.update {
                it.copy(
                    selectedReportContacts = ContactDetails(
                        buyer = contactDetails.buyer,
                        seller = contactDetails.seller,
                        order = contactDetails.order
                    )
                )
            }
        }
    }

    private fun executeResolution(resolutionStatus: String, cancelOrder: Boolean) {
        val currentState = _state.value
        val report = currentState.selectedReport ?: return
        val notes = currentState.adminNotes.trim()

        if (notes.isBlank()) {
            _state.update { it.copy(errorMessage = "Debes ingresar las notas de resolución antes de continuar") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isResolving = true, errorMessage = null) }

            resolveReportUseCase(
                reportId = report.id,
                orderId = report.orderId,
                businessId = report.businessId,
                resolutionStatus = resolutionStatus,
                adminNotes = notes,
                cancelOrder = cancelOrder
            )
                .onSuccess {
                    val msg = if (cancelOrder) "Reporte resuelto y orden cancelada." else "Reporte rechazado."
                    _state.update {
                        it.copy(
                            isResolving = false,
                            selectedReport = null,
                            adminNotes = "",
                            successMessage = msg
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isResolving = false,
                            errorMessage = error.message ?: "Error al procesar la resolución"
                        )
                    }
                }
        }
    }
}
