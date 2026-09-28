package com.example.marketuniversitario.feature.business.ui.edit_item

import com.example.marketuniversitario.feature.business.domain.models.ItemType
import com.example.marketuniversitario.feature.business.domain.models.ProductCategory

sealed interface EditItemStatus {
    object Idle : EditItemStatus
    object Loading : EditItemStatus
    object Success : EditItemStatus
    data class Error(val message: String) : EditItemStatus
}

data class EditItemState(
    val productId: String? = null,
    val itemType: ItemType = ItemType.PRODUCT,
    
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val stock: String = "",
    val category: String = "",
    val photoUrl: String? = null,
    
    val isEditMode: Boolean = false,
    val status: EditItemStatus = EditItemStatus.Idle
) {
    val selectedCategoryDisplayName: String
        get() = ProductCategory.fromId(category).displayName
}

sealed interface EditItemEvent {
    data class NameChanged(val name: String) : EditItemEvent
    data class DescriptionChanged(val description: String) : EditItemEvent
    data class PriceChanged(val price: String) : EditItemEvent
    data class StockChanged(val stock: String) : EditItemEvent
    data class CategoryChanged(val category: String) : EditItemEvent
    data class PhotoChanged(val url: String?) : EditItemEvent
    
    object SaveItem : EditItemEvent
    object ResetStatus : EditItemEvent
}
