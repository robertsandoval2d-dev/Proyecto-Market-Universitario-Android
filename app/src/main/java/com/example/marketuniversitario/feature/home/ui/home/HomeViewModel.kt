package com.example.marketuniversitario.feature.home.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.auth.domain.usecases.GetCurrentUserIdUseCase
import com.example.marketuniversitario.feature.business.domain.usecases.GetPublicProductUseCase
import com.example.marketuniversitario.feature.business.domain.usecases.IncrementProductViewUseCase
import com.example.marketuniversitario.feature.home.domain.models.FeedProduct
import com.example.marketuniversitario.feature.home.domain.usecases.GetFeedProductsUseCase
import com.example.marketuniversitario.feature.home.domain.usecases.GetUserSummaryUseCase
import com.example.marketuniversitario.feature.home.domain.usecases.SearchProductsUseCase
import com.example.marketuniversitario.feature.recomendation.domain.models.InteractionType
import com.example.marketuniversitario.feature.recomendation.domain.usecases.GetPersonalizedFeedUseCase
import com.example.marketuniversitario.feature.recomendation.domain.usecases.TrackInteractionUseCase
import com.example.marketuniversitario.feature.user.domain.repositories.UserRepository
import com.example.marketuniversitario.feature.user.domain.usecases.AddRecentSearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserSummaryUseCase: GetUserSummaryUseCase,
    private val getFeedProductsUseCase: GetFeedProductsUseCase,
    private val getPersonalizedFeedUseCase: GetPersonalizedFeedUseCase,
    private val getPublicProductUseCase: GetPublicProductUseCase,
    private val incrementProductViewUseCase: IncrementProductViewUseCase,
    private val trackInteractionUseCase: TrackInteractionUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val addRecentSearchUseCase: AddRecentSearchUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    // Feed completo (ya ordenado por la IA). state.products es lo que se muestra.
    private var allProducts: List<FeedProduct> = emptyList()

    init {
        loadUser()
        loadProducts()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadFeed, is HomeEvent.LoadProducts -> loadProducts()
            is HomeEvent.Refresh -> refresh()

            is HomeEvent.LoadUser -> loadUser()
            is HomeEvent.SelectProductQuickView -> {
                _state.update { it.copy(selectedProduct = event.product) }
                if (event.product != null) {
                    viewModelScope.launch {
                        incrementProductViewUseCase(event.product.id)
                            .onSuccess {
                                Log.d("HomeViewModel", "Vista de producto incrementada en QuickView: ${event.product.id}")
                            }
                            .onFailure { error ->
                                Log.e("HomeViewModel", "Fallo al incrementar vista en QuickView: ${error.message}", error)
                            }

                        val uid = getCurrentUserIdUseCase()
                        if (!uid.isNullOrBlank()) {
                            val product = getPublicProductUseCase(event.product.id).getOrNull()
                            if (product != null) {
                                trackInteractionUseCase(uid, product, InteractionType.VIEW)
                                    .onSuccess {
                                        Log.d("HomeViewModel", "Interacción VIEW registrada en QuickView para usuario: $uid")
                                    }
                                    .onFailure { error ->
                                        Log.e("HomeViewModel", "Fallo al registrar interacción VIEW en QuickView: ${error.message}", error)
                                    }
                            }
                        }
                    }
                }
            }
            is HomeEvent.RequestProductSelected -> _state.update { it.copy(requestProductId = event.productId) }

            is HomeEvent.QueryChanged -> onQueryChanged(event.query)
            is HomeEvent.Search -> search()
            is HomeEvent.CategorySelected -> _state.update { it.copy(selectedCategory = event.categoryId) }
            is HomeEvent.TagSelected -> _state.update { it.copy(selectedTagId = event.tagId) }

            is HomeEvent.ShowSearchOptions -> _state.update { it.copy(showSearchOptions = true) }
            is HomeEvent.DismissSearchOptions -> _state.update { it.copy(showSearchOptions = false) }
            is HomeEvent.SelectImage -> _state.update { it.copy(selectedImage = event.uri) }

            is HomeEvent.ShowNotification -> _state.update { it.copy(showNotification = true) }
            is HomeEvent.DismissNotification -> _state.update { it.copy(showNotification = false) }
        }
    }

    // ---------------------------------------------------------------- Búsqueda

    /** Mientras se escribe solo cambia el texto; si queda vacío, se restaura el feed. */
    private fun onQueryChanged(query: String) {
        _state.update {
            if (query.isBlank() && it.activeQuery != null) {
                it.copy(query = query, activeQuery = null, products = allProducts)
            } else {
                it.copy(query = query)
            }
        }
    }

    /** Se ejecuta al enviar la búsqueda: filtra y guarda la frase en recent searches. */
    private fun search() {
        val query = _state.value.query.orEmpty().trim()
        if (query.isBlank()) {
            _state.update { it.copy(activeQuery = null, products = allProducts) }
            return
        }

        _state.update {
            it.copy(
                activeQuery = query,
                products = searchProductsUseCase(allProducts, query)
            )
        }
        saveRecentSearch(query)
    }

    private fun saveRecentSearch(query: String) {
        viewModelScope.launch {
            val uid = getCurrentUserIdUseCase()
            if (uid.isNullOrBlank()) return@launch
            addRecentSearchUseCase(uid, query)
                .onSuccess { Log.d("HomeViewModel", "Búsqueda guardada: $query") }
                .onFailure { Log.e("HomeViewModel", "No se pudo guardar la búsqueda: ${it.message}", it) }
        }
    }

    /** Lista a mostrar según la búsqueda activa. */
    private fun visibleProducts(activeQuery: String?): List<FeedProduct> =
        if (activeQuery.isNullOrBlank()) allProducts
        else searchProductsUseCase(allProducts, activeQuery)

    // ---------------------------------------------------------------- Carga

    private fun loadUser() {
        viewModelScope.launch {
            try {
                val userInfo = getUserSummaryUseCase()
                _state.update { it.copy(userSummary = userInfo) }
            } catch (_: Exception) {
            }
        }
    }

    private var productsJob: Job? = null
    private fun loadProducts() {
        productsJob?.cancel()
        productsJob = viewModelScope.launch {
            _state.update { it.copy(status = HomeStatus.Loading) }
            try {
                /*
                --- Recomendación por IA desactivada temporalmente ---
                val uid = getCurrentUserIdUseCase()
                val user = uid?.let { userRepository.getUser(uid).getOrNull() }

                val rankedIds = if (user != null) {
                    getPersonalizedFeedUseCase(user).getOrDefault(emptyList())
                } else {
                    emptyList()
                }
                */
                val rankedIds = emptyList<String>()

                getFeedProductsUseCase()
                    .catch { e -> _state.update { it.copy(status = HomeStatus.Error(e.message ?: "Error al cargar la lista de productos")) } }
                    .onEach { products ->
                        val sortedProducts = if (rankedIds.isNotEmpty()) {
                            val rankMap = rankedIds.withIndex().associate { it.value to it.index }
                            products.sortedBy { product -> rankMap[product.id] ?: Int.MAX_VALUE }
                        } else {
                            products
                        }

                        allProducts = sortedProducts
                        _state.update {
                            it.copy(
                                products = visibleProducts(it.activeQuery),
                                status = HomeStatus.Success
                            )
                        }
                    }
                    .launchIn(this) // antes: viewModelScope (ver nota abajo)
            } catch (_: Exception) {
                // Fallback a feed normal si ocurre algún error con la IA
                getFeedProductsUseCase()
                    .catch { err -> _state.update { it.copy(status = HomeStatus.Error(err.message ?: "Error al cargar la lista de productos")) } }
                    .onEach { products ->
                        allProducts = products
                        _state.update {
                            it.copy(
                                products = visibleProducts(it.activeQuery),
                                status = HomeStatus.Success
                            )
                        }
                    }
                    .launchIn(this)
            }
        }
    }

    private fun refresh() {
        loadUser()
        loadProducts()
    }
}
