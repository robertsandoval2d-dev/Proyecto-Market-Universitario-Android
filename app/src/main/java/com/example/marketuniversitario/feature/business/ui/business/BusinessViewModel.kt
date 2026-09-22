package com.example.marketuniversitario.feature.business.ui.business

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.business.domain.usecases.ActivateBusinessUseCase
import com.example.marketuniversitario.feature.business.domain.usecases.GetBusinessStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BusinessViewModel @Inject constructor(
    private val getBusinessStatusUseCase: GetBusinessStatusUseCase,
    private val activateBusinessUseCase: ActivateBusinessUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BusinessState())
    val state = _state.asStateFlow()

    init {
        checkStatus()
    }

    fun onEvent(event: BusinessEvent) {
        when (event) {
            is BusinessEvent.CheckStatus -> checkStatus()
            is BusinessEvent.ActivateBusiness -> activateBusiness(event.name, event.description, event.category)
            is BusinessEvent.DismissDialog -> _state.update { it.copy(status = BusinessStatus.Idle) }
        }
    }

    private fun checkStatus() {
        _state.update { it.copy(status = BusinessStatus.Loading) }
        viewModelScope.launch {
            getBusinessStatusUseCase()
                .onSuccess { hasBusiness ->
                    val newStatus = if (hasBusiness) BusinessStatus.HasBusiness else BusinessStatus.NoBusiness
                    _state.update { it.copy(status = newStatus) }
                }
                .onFailure { error ->
                    _state.update { it.copy(status = BusinessStatus.Error(error.message ?: "Error al consultar estado del negocio")) }
                }
        }
    }

    private fun activateBusiness(name: String, description: String, category: String) {
        _state.update { it.copy(status = BusinessStatus.Loading) }
        viewModelScope.launch {
            activateBusinessUseCase(name = name, description = description, category = category)
                .onSuccess {
                    _state.update { it.copy(status = BusinessStatus.HasBusiness) }
                }
                .onFailure { error ->
                    _state.update { it.copy(status = BusinessStatus.Error(error.message ?: "Error al activar el negocio")) }
                }
        }
    }
}
