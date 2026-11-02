package com.example.marketuniversitario.feature.user.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.reports.domain.usecases.GetReportsByStudentUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserReportsViewModel @Inject constructor(
    private val getReportsByStudentUseCase: GetReportsByStudentUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(UserReportsState())
    val state = _state.asStateFlow()

    init {
        loadUserReports()
    }

    private fun loadUserReports() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId.isNullOrBlank()) {
            _state.update { it.copy(isLoading = false, errorMessage = "Usuario no autenticado") }
            return
        }

        viewModelScope.launch {
            getReportsByStudentUseCase(userId)
                .catch { e ->
                    _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error al cargar reportes") }
                }
                .collect { reports ->
                    _state.update {
                        it.copy(
                            reports = reports,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }
}
