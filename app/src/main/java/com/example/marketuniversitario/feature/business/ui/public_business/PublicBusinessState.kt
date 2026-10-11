package com.example.marketuniversitario.feature.business.ui.public_business

import com.example.marketuniversitario.feature.business.domain.models.Business
import com.example.marketuniversitario.feature.business.domain.models.Product

sealed interface PublicBusinessStatus {
    object Idle : PublicBusinessStatus
    object Loading : PublicBusinessStatus
    object Success : PublicBusinessStatus
    data class Error(val message: String) : PublicBusinessStatus
}

data class PublicBusinessState(
    val business: Business? = null,
    val products: List<Product> = emptyList(),
    val status: PublicBusinessStatus = PublicBusinessStatus.Idle
)

sealed interface PublicBusinessEvent {
    data class LoadBusiness(val businessId: String) : PublicBusinessEvent
}
