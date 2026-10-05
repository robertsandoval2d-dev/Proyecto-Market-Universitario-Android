package com.example.marketuniversitario.feature.home.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import com.example.marketuniversitario.feature.home.ui.components.ProductQuickView.ProductQuickViewBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.example.marketuniversitario.core.theme.MarketUniversitarioTheme
import com.example.marketuniversitario.feature.home.domain.models.FeedProduct
import com.example.marketuniversitario.feature.home.domain.models.OrderRequest
import com.example.marketuniversitario.feature.home.domain.models.UserSummary
import com.example.marketuniversitario.feature.home.ui.components.OrderRequestDialog.OrderRequestDialog
import com.example.marketuniversitario.feature.home.ui.components.OrderRequestDialog.OrderRequestErrorDialog
import com.example.marketuniversitario.feature.home.ui.components.OrderRequestDialog.OrderRequestStatus
import com.example.marketuniversitario.feature.home.ui.components.OrderRequestDialog.OrderRequestSuccessDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeState,
    onNavigateToItem: (String) -> Unit,
    onEvent: (HomeEvent) -> Unit,
) {
    val selectedProduct = state.selectedProduct
    val requestProduct = state.requestProduct
    if (selectedProduct != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ProductQuickViewBottomSheet(
            product = selectedProduct,
            sheetState = sheetState,
            onDismiss = { onEvent(HomeEvent.SelectProductQuickView(null)) },
            onRequestOrderClick = { product ->
                onEvent(HomeEvent.RequestProductView(product))
                onEvent(HomeEvent.SelectProductQuickView(null))
            },
            onNavigateToBusiness = { businessId ->
                onEvent(HomeEvent.SelectProductQuickView(null))
                // TODO: Navegar al perfil del negocio
            },
            onNavigateToFullDetail = { productId ->
                onEvent(HomeEvent.SelectProductQuickView(null))
                onNavigateToItem(productId)
            }
        )
    }
    if (requestProduct != null) {
        OrderRequestDialog(
            product = requestProduct,
            onDismiss = {
                onEvent(
                    HomeEvent.RequestProductView(null)
                )
            },

            onConfirmOrder = { quantity, note ->
                onEvent(HomeEvent.RequestOrder(
                    OrderRequest(
                        product = requestProduct,
                        quantity = quantity,
                        note = note
                    )
                ))
            }
        )
    }
    when (val status = state.orderRequestStatus) {
        OrderRequestStatus.Idle -> Unit
        OrderRequestStatus.Loading -> {
            // Opcional: mostrar loading
        }

        OrderRequestStatus.Success -> {
                OrderRequestSuccessDialog(
                    onDismiss = {
                        onEvent(HomeEvent.DismissOrderRequestStatus)
                    }
                )
        }

        is OrderRequestStatus.Error -> {
                OrderRequestErrorDialog(
                    message = status.message,
                    onDismiss = {
                        onEvent(HomeEvent.DismissOrderRequestStatus)
                    }
                )
        }
    }

    Column(
        Modifier.fillMaxSize()
    ) {
        HomeHeader(
            userSummary = state.userSummary,
            query = state.query,
            onQueryChange = { onEvent(HomeEvent.QueryChanged(it)) },
            onSearchWithImageClick = { onEvent(HomeEvent.ShowSearchOptions) },
            onSearchClick = { onEvent(HomeEvent.Search) },
            onNotificationClick = { onEvent(HomeEvent.ShowNotification)}
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ){
            val status = state.status
            when {
                status is HomeStatus.Loading && state.products.isEmpty() ->
                    CircularProgressIndicator(Modifier.align(Alignment.Center))

                status is HomeStatus.Error ->
                    Text(text = status.message)
                status is HomeStatus.Success && state.products.isEmpty() ->
                    Text("No encontramos productos", Modifier.align(Alignment.Center))
                else -> HomeContent(
                    products = state.products,
                    onProductClick = { product ->
                        onEvent(HomeEvent.SelectProductQuickView(product))
                    }
                )
            }
        }
    }
}

@Composable
fun HomeHeader(
    userSummary: UserSummary?,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchWithImageClick : () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(bottomEnd = 24.dp)
            )
            .padding(top = 30.dp, start = 15.dp, end = 15.dp, bottom = 18.dp)
    ) {
        Column {
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    // Avatar con Inicial del Usuario
                    val initial = userSummary?.firstName?.trim()?.firstOrNull()?.uppercaseChar()?.toString() ?: "U"
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initial,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Bienvenido 👋", color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 12.sp)
                        Text(text = userSummary?.firstName?.ifBlank { "Usuario" } ?: "Usuario", color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Notificaciones
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSecondary)
                        .clickable { onNotificationClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notificaciones", tint = MaterialTheme.colorScheme.secondary)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Barra de Búsqueda
            Surface (
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 7.5.dp)
                        ,
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            color = MaterialTheme.colorScheme.primaryContainer
                        ),
                        decorationBox = { innerTextField ->
                            if (query.isEmpty()) {
                                Text(
                                    "Buscar productos o servicios",
                                    color = MaterialTheme.colorScheme.primaryContainer
                                )
                            }
                            innerTextField()
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Search
                        )

                    )
                    // Ícono de Cámara
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimaryContainer)
                            .clickable { onSearchWithImageClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Buscar por imagen",
                            tint = MaterialTheme.colorScheme.primaryContainer,
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // Ícono de Búsqueda
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable { onSearchClick() },
                        contentAlignment = Alignment.Center
                    ){
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeContent(
    products: List<FeedProduct>,
    onProductClick: (FeedProduct) -> Unit,
    modifier: Modifier = Modifier
){
    LazyVerticalGrid (
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "Productos recomendados",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        // Item
        items(items = products, key = { it.id }) { product ->
            ProductCard(
                product = product,
                onClick = { onProductClick(product) }
            )
        }
    }
}

@Composable
fun ProductCard(
    product: FeedProduct,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
    ) {
        Column {
            // Imagen del producto con Coil
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                val imageUrl = product.imageUrl
                if (imageUrl.isNotBlank()) {
                    SubcomposeAsyncImage(
                        model = imageUrl,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        loading = {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                            }
                        },
                        error = {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Sin imagen",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Sin imagen",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = product.businessName.ifBlank { "Negocio universitario" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "S/ %.2f".format(product.price),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToItem: (String) -> Unit = {}
)
{
    val state by viewModel.state.collectAsState()

    HomeScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateToItem = onNavigateToItem
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun HomeScreenPreview() {
    MarketUniversitarioTheme() {
        HomeScreen(
            state = HomeState(),
            onEvent = {},
            onNavigateToItem = {}
        )
    }
}