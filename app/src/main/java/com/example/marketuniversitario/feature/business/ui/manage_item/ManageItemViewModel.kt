package com.example.marketuniversitario.feature.business.ui.manage_item

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.business.domain.models.ItemType
import com.example.marketuniversitario.feature.business.domain.usecases.DeleteProductUseCase
import com.example.marketuniversitario.feature.business.domain.usecases.GetProductsByBusinessUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageItemViewModel @Inject constructor(
    private val getProductsByBusinessUseCase: GetProductsByBusinessUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val itemTypeArg: String = savedStateHandle.get<String>("itemType") ?: ItemType.PRODUCT.name
    private val initialItemType = try {
        ItemType.valueOf(itemTypeArg)
    } catch (e: Exception) {
        ItemType.PRODUCT
    }

    private val _state = MutableStateFlow(ManageItemState(selectedItemType = initialItemType))
    val state = _state.asStateFlow()

    init {
        loadItems()
    }

    fun onEvent(event: ManageItemEvent) {
        when (event) {
            is ManageItemEvent.LoadItems -> loadItems()
            is ManageItemEvent.SearchQueryChanged -> _state.update { it.copy(searchQuery = event.query) }
            is ManageItemEvent.DeleteItem -> deleteItem(event.productId)
        }
    }

    private fun loadItems() {
        _state.update { it.copy(status = ManageItemStatus.Loading) }
        viewModelScope.launch {
            getProductsByBusinessUseCase()
                .onSuccess { allItems ->
                    val filtered = allItems.filter { it.type == _state.value.selectedItemType.name }
                    _state.update {
                        it.copy(status = ManageItemStatus.Success(filtered))
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(status = ManageItemStatus.Error(error.message ?: "Error al cargar elementos"))
                    }
                }
        }
    }

    private fun deleteItem(productId: String) {
        viewModelScope.launch {
            deleteProductUseCase(productId)
                .onSuccess {
                    loadItems()
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(status = ManageItemStatus.Error(error.message ?: "Error al eliminar el elemento"))
                    }
                }
        }
    }
}
