package com.example.marketuniversitario.feature.business.ui.business

import com.example.marketuniversitario.feature.business.domain.models.Business

sealed interface BusinessStatus {
    object Idle : BusinessStatus
    object Loading : BusinessStatus
    object NoBusiness : BusinessStatus
    data class HasBusiness(val business: Business) : BusinessStatus
    data class Error(val message: String) : BusinessStatus
}

data class BusinessState(
    val businessId: String = "",
    val name: String = "",
    val description: String = "",
    val categories: List<String> = emptyList(),
    val bannerUrl: String? = null,
    val productCount: Int = 0,
    val serviceCount: Int = 0,
    val isEditing: Boolean = false,
    val showAddCategoryDialog: Boolean = false,
    val selectedCategoryToEdit: String? = null,
    val status: BusinessStatus = BusinessStatus.Idle
)

sealed interface BusinessEvent {
    object CheckStatus : BusinessEvent
    data class ActivateBusiness(
        val name: String = "Mi Emprendimiento",
        val description: String = "",
        val categories: List<String> = emptyList()
    ) : BusinessEvent
    data class NameChanged(val name: String) : BusinessEvent
    data class DescriptionChanged(val description: String) : BusinessEvent
    data class BannerSelected(val uri: String) : BusinessEvent
    object IsEditing : BusinessEvent
    object SaveBusiness : BusinessEvent
    object ShowAddCategoryDialog : BusinessEvent
    object DismissAddCategoryDialog : BusinessEvent
    data class AddCategory(val categoryId: String) : BusinessEvent
    data class SelectCategoryToEdit(val categoryId: String) : BusinessEvent
    object DismissEditCategoryDialog : BusinessEvent
    data class ReplaceCategory(val oldCategoryId: String, val newCategoryId: String) : BusinessEvent
    data class RemoveCategory(val categoryId: String) : BusinessEvent
}
