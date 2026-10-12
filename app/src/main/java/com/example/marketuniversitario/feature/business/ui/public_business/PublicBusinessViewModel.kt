package com.example.marketuniversitario.feature.business.ui.public_business

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.business.domain.usecases.GetBusinessByIdUseCase
import com.example.marketuniversitario.feature.business.domain.usecases.GetPublicProductsByBusinessUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PublicBusinessViewModel @Inject constructor(
    private val getPublicProductsByBusinessUseCase: GetPublicProductsByBusinessUseCase,
    private val getBusinessByIdUseCase: GetBusinessByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(PublicBusinessState())
    val state: StateFlow<PublicBusinessState> = _state.asStateFlow()

    init {
        val businessId = savedStateHandle.get<String>("businessId")
        if (!businessId.isNullOrBlank()) {
            loadBusiness(businessId)
        }
    }

    fun onEvent(event: PublicBusinessEvent) {
        when (event) {
            is PublicBusinessEvent.LoadBusiness -> loadBusiness(event.businessId)
        }
    }

    fun loadBusiness(businessId: String) {
        viewModelScope.launch {
            _state.update { it.copy(status = PublicBusinessStatus.Loading) }
            
            // Cargar datos del negocio y sus productos
            val businessResult = getBusinessByIdUseCase(businessId)
            val productsResult = getPublicProductsByBusinessUseCase(businessId)

            businessResult.onSuccess { business ->
                if (business != null) {
                    val productsList = productsResult.getOrDefault(emptyList())
                    _state.update {
                        it.copy(
                            business = business,
                            products = productsList,
                            status = PublicBusinessStatus.Success
                        )
                    }
                } else {
                    _state.update {
                        it.copy(status = PublicBusinessStatus.Error("Negocio no encontrado"))
                    }
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(status = PublicBusinessStatus.Error(error.message ?: "Error al cargar información del negocio"))
                }
            }
        }
    }
}
