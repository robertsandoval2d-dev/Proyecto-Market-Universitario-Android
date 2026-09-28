package com.example.marketuniversitario.feature.business.ui.manage_item

import com.example.marketuniversitario.feature.business.domain.models.ItemType
import com.example.marketuniversitario.feature.business.domain.models.Product

sealed interface ManageItemStatus {
    object Idle : ManageItemStatus
    object Loading : ManageItemStatus
    data class Success(val items: List<Product>) : ManageItemStatus
    data class Error(val message: String) : ManageItemStatus
}

data class ManageItemState(
    val searchQuery: String = "",
    val selectedItemType: ItemType = ItemType.PRODUCT,
    val status: ManageItemStatus = ManageItemStatus.Idle
)

sealed interface ManageItemEvent {
    object LoadItems : ManageItemEvent
    data class SearchQueryChanged(val query: String) : ManageItemEvent
    data class DeleteItem(val productId: String) : ManageItemEvent
}
