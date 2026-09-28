package com.example.marketuniversitario.feature.business.ui.business

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.business.domain.models.ItemType
import com.example.marketuniversitario.feature.business.domain.usecases.ActivateBusinessUseCase
import com.example.marketuniversitario.feature.business.domain.usecases.GetMyBusinessUseCase
import com.example.marketuniversitario.feature.business.domain.usecases.GetProductsByBusinessUseCase
import com.example.marketuniversitario.feature.business.domain.usecases.UpdateBusinessUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BusinessViewModel @Inject constructor(
    private val getMyBusinessUseCase: GetMyBusinessUseCase,
    private val activateBusinessUseCase: ActivateBusinessUseCase,
    private val updateBusinessUseCase: UpdateBusinessUseCase,
    private val getProductsByBusinessUseCase: GetProductsByBusinessUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BusinessState())
    val state = _state.asStateFlow()

    init {
        checkStatus()
    }

    fun onEvent(event: BusinessEvent) {
        when (event) {
            is BusinessEvent.CheckStatus -> checkStatus()
            is BusinessEvent.ActivateBusiness -> activateBusiness(event.name, event.description, event.categories)
            is BusinessEvent.NameChanged -> _state.update { it.copy(name = event.name) }
            is BusinessEvent.DescriptionChanged -> _state.update { it.copy(description = event.description) }
            is BusinessEvent.IsEditing -> _state.update { it.copy(isEditing = true) }
            is BusinessEvent.SaveBusiness -> saveBusiness()
            is BusinessEvent.ShowAddCategoryDialog -> _state.update { it.copy(showAddCategoryDialog = true) }
            is BusinessEvent.DismissAddCategoryDialog -> _state.update { it.copy(showAddCategoryDialog = false) }
            is BusinessEvent.SelectCategoryToEdit -> _state.update { it.copy(selectedCategoryToEdit = event.categoryId) }
            is BusinessEvent.DismissEditCategoryDialog -> _state.update { it.copy(selectedCategoryToEdit = null) }
            is BusinessEvent.AddCategory -> addCategory(event.categoryId)
            is BusinessEvent.ReplaceCategory -> replaceCategory(event.oldCategoryId, event.newCategoryId)
            is BusinessEvent.RemoveCategory -> removeCategory(event.categoryId)
        }
    }

    private fun checkStatus() {
        _state.update { it.copy(status = BusinessStatus.Loading) }
        viewModelScope.launch {
            getMyBusinessUseCase()
                .onSuccess { business ->
                    if (business != null) {
                        _state.update {
                            it.copy(
                                status = BusinessStatus.HasBusiness(business),
                                name = business.name,
                                description = business.description,
                                categories = business.categories
                            )
                        }
                        loadBusinessItemCounts(business.id)
                    } else {
                        _state.update { it.copy(status = BusinessStatus.NoBusiness) }
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(status = BusinessStatus.Error(error.message ?: "Error al consultar estado del negocio")) }
                }
        }
    }

    private fun activateBusiness(name: String, description: String, categories: List<String>) {
        _state.update { it.copy(status = BusinessStatus.Loading) }
        viewModelScope.launch {
            activateBusinessUseCase(name = name, description = description, categories = categories)
                .onSuccess { createdBusiness ->
                    _state.update {
                        it.copy(
                            status = BusinessStatus.HasBusiness(createdBusiness),
                            name = createdBusiness.name,
                            description = createdBusiness.description,
                            categories = createdBusiness.categories
                        )
                    }
                    loadBusinessItemCounts(createdBusiness.id)
                }
                .onFailure { error ->
                    _state.update { it.copy(status = BusinessStatus.Error(error.message ?: "Error al activar el negocio")) }
                }
        }
    }

    private fun loadBusinessItemCounts(businessId: String) {
        viewModelScope.launch {
            getProductsByBusinessUseCase(businessId)
                .onSuccess { items ->
                    val products = items.count { it.type == ItemType.PRODUCT.name }
                    val services = items.count { it.type == ItemType.SERVICE.name }
                    _state.update {
                        it.copy(
                            productCount = products,
                            serviceCount = services
                        )
                    }
                }
        }
    }

    private fun saveBusiness() {
        val currentStatus = _state.value.status
        val currentBusiness = (currentStatus as? BusinessStatus.HasBusiness)?.business ?: return

        val updatedBusiness = currentBusiness.copy(
            name = _state.value.name.trim(),
            description = _state.value.description.trim()
        )

        _state.update { it.copy(status = BusinessStatus.Loading) }

        viewModelScope.launch {
            updateBusinessUseCase(updatedBusiness)
                .onSuccess {
                    _state.update {
                        it.copy(
                            status = BusinessStatus.HasBusiness(updatedBusiness),
                            isEditing = false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            status = BusinessStatus.Error(error.message ?: "Error al actualizar negocio")
                        )
                    }
                }
        }
    }

    private fun addCategory(categoryId: String) {
        val currentCategories = _state.value.categories.toMutableList()
        if (!currentCategories.contains(categoryId)) {
            currentCategories.add(categoryId)
            _state.update { it.copy(categories = currentCategories, showAddCategoryDialog = false) }
            saveCategoriesToBusiness(currentCategories)
        }
    }

    private fun replaceCategory(oldCategoryId: String, newCategoryId: String) {
        val currentCategories = _state.value.categories.toMutableList()
        val index = currentCategories.indexOf(oldCategoryId)
        if (index != -1) {
            currentCategories[index] = newCategoryId
            _state.update {
                it.copy(
                    categories = currentCategories,
                    selectedCategoryToEdit = null,
                    showAddCategoryDialog = false
                )
            }
            saveCategoriesToBusiness(currentCategories)
        }
    }

    private fun removeCategory(categoryId: String) {
        val currentCategories = _state.value.categories.toMutableList()
        if (currentCategories.remove(categoryId)) {
            _state.update { it.copy(categories = currentCategories, selectedCategoryToEdit = null) }
            saveCategoriesToBusiness(currentCategories)
        }
    }

    private fun saveCategoriesToBusiness(categories: List<String>) {
        val currentStatus = _state.value.status
        val currentBusiness = (currentStatus as? BusinessStatus.HasBusiness)?.business ?: return
        val updatedBusiness = currentBusiness.copy(categories = categories)

        viewModelScope.launch {
            updateBusinessUseCase(updatedBusiness)
                .onSuccess {
                    _state.update { it.copy(status = BusinessStatus.HasBusiness(updatedBusiness)) }
                }
        }
    }
}
