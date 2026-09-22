package com.example.marketuniversitario.feature.business.ui.business

sealed interface BusinessStatus {
    object Idle : BusinessStatus
    object Loading : BusinessStatus
    object NoBusiness : BusinessStatus
    object HasBusiness : BusinessStatus
    data class Error(val message: String) : BusinessStatus
}

data class BusinessState(
    val status: BusinessStatus = BusinessStatus.Idle
)

sealed interface BusinessEvent {
    object CheckStatus : BusinessEvent
    data class ActivateBusiness(
        val name: String = "Mi Emprendimiento",
        val description: String = "",
        val category: String = ""
    ) : BusinessEvent
    object DismissDialog : BusinessEvent
}
