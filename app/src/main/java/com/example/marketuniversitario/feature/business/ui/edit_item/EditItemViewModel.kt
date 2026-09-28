package com.example.marketuniversitario.feature.business.ui.edit_item

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.business.domain.models.ItemType
import com.example.marketuniversitario.feature.business.domain.models.Product
import com.example.marketuniversitario.feature.business.domain.usecases.CreateProductUseCase
import com.example.marketuniversitario.feature.business.domain.usecases.GetProductUseCase
import com.example.marketuniversitario.feature.business.domain.usecases.UpdateProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditItemViewModel @Inject constructor(
    private val getProductUseCase: GetProductUseCase,
    private val createProductUseCase: CreateProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val itemTypeArg: String = savedStateHandle.get<String>("itemType") ?: ItemType.PRODUCT.name
    private val productIdArg: String? = savedStateHandle.get<String>("productId")

    private val initialItemType = try {
        ItemType.valueOf(itemTypeArg)
    } catch (e: Exception) {
        ItemType.PRODUCT
    }

    private val _state = MutableStateFlow(
        EditItemState(
            itemType = initialItemType,
            productId = productIdArg,
            isEditMode = !productIdArg.isNullOrBlank()
        )
    )
    val state = _state.asStateFlow()

    init {
        if (!productIdArg.isNullOrBlank()) {
            loadProduct(productIdArg)
        }
    }

    fun onEvent(event: EditItemEvent) {
        when (event) {
            is EditItemEvent.NameChanged -> _state.update { it.copy(name = event.name) }
            is EditItemEvent.DescriptionChanged -> _state.update { it.copy(description = event.description) }
            is EditItemEvent.PriceChanged -> {
                val newPrice = event.price.filter { it.isDigit() || it == '.' }
                if (newPrice.count { it == '.' } <= 1) {
                    _state.update { it.copy(price = newPrice) }
                }
            }
            is EditItemEvent.StockChanged -> {
                val newStock = event.stock.filter { it.isDigit() } // Solo números
                _state.update { it.copy(stock = newStock) }
            }
            is EditItemEvent.CategoryChanged -> _state.update { it.copy(category = event.category) }
            is EditItemEvent.PhotoChanged -> _state.update { it.copy(photoUrl = event.url) }
            is EditItemEvent.SaveItem -> saveItem()
            is EditItemEvent.ResetStatus -> _state.update { it.copy(status = EditItemStatus.Idle) }
        }
    }

    private fun loadProduct(productId: String) {
        _state.update { it.copy(status = EditItemStatus.Loading) }
        viewModelScope.launch {
            getProductUseCase(productId)
                .onSuccess { product ->
                    if (product != null) {
                        _state.update {
                            it.copy(
                                name = product.name,
                                description = product.description,
                                price = product.price.toString(),
                                stock = product.stock?.toString() ?: "", // Convertimos Int a String
                                category = product.category, // Ya guarda el ID en DB (ej. "Technology")
                                photoUrl = product.images.firstOrNull(),
                                itemType = try { ItemType.valueOf(product.type) } catch (e: Exception) { ItemType.PRODUCT },
                                status = EditItemStatus.Idle
                            )
                        }
                    } else {
                        _state.update { it.copy(status = EditItemStatus.Error("Producto no encontrado")) }
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(status = EditItemStatus.Error(error.message ?: "Error al cargar producto")) }
                }
        }
    }

    private fun saveItem() {
        val currentState = _state.value
        
        if (currentState.name.isBlank() || currentState.price.isBlank()) {
            _state.update { it.copy(status = EditItemStatus.Error("Nombre y precio son obligatorios")) }
            return
        }

        val parsedPrice = currentState.price.toDoubleOrNull()
        if (parsedPrice == null) {
            _state.update { it.copy(status = EditItemStatus.Error("Precio inválido")) }
            return
        }
        
        val parsedStock = currentState.stock.toIntOrNull()
        if (currentState.itemType == ItemType.PRODUCT && parsedStock == null) {
            _state.update { it.copy(status = EditItemStatus.Error("Cantidad inválida")) }
            return
        }

        _state.update { it.copy(status = EditItemStatus.Loading) }

        viewModelScope.launch {
            val product = Product(
                id = currentState.productId ?: "", 
                businessId = "", 
                businessName = "", 
                name = currentState.name.trim(),
                description = currentState.description.trim(),
                price = parsedPrice,
                type = currentState.itemType.name,
                category = currentState.category, // Ya es el ID (ej. "Technology")
                images = currentState.photoUrl?.let { listOf(it) } ?: emptyList(),
                stock = if (currentState.itemType == ItemType.PRODUCT) parsedStock else null,
                isAvailable = true
            )

            val result = if (currentState.isEditMode) {
                updateProductUseCase(product)
            } else {
                createProductUseCase(product).map { } 
            }

            result
                .onSuccess {
                    _state.update { it.copy(status = EditItemStatus.Success) }
                }
                .onFailure { error ->
                    _state.update { it.copy(status = EditItemStatus.Error(error.message ?: "Error al guardar elemento")) }
                }
        }
    }
}
