package com.example.marketuniversitario.feature.business.ui.manage_item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.marketuniversitario.core.theme.MarketUniversitarioTheme
import com.example.marketuniversitario.feature.business.domain.models.ItemType
import com.example.marketuniversitario.feature.business.domain.models.Product

@Composable
fun ManageItemScreen(
    viewModel: ManageItemViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToAddItem: (ItemType) -> Unit = {},
    onNavigateToEditItem: (Product) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(ManageItemEvent.LoadItems)
    }

    ManageItemContent(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
        onNavigateToAddItem = onNavigateToAddItem,
        onNavigateToEditItem = onNavigateToEditItem
    )
}

@Composable
fun ManageItemContent(
    state: ManageItemState,
    onEvent: (ManageItemEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToAddItem: (ItemType) -> Unit,
    onNavigateToEditItem: (Product) -> Unit
) {
    val items = (state.status as? ManageItemStatus.Success)?.items ?: emptyList()

    val titleText = if (state.selectedItemType == ItemType.PRODUCT) {
        "Gestión de Productos (${items.size})"
    } else {
        "Gestión de Servicios (${items.size})"
    }

    val addButtonText = if (state.selectedItemType == ItemType.PRODUCT) {
        "Añadir Nuevo Producto +"
    } else {
        "Añadir Nuevo Servicio +"
    }

    val filteredItems = items.filter {
        it.name.contains(state.searchQuery, ignoreCase = true) ||
                it.description.contains(state.searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título de la sección
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = titleText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }

        // Barra de búsqueda
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { onEvent(ManageItemEvent.SearchQueryChanged(it)) },
            placeholder = { Text("Buscar...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.LightGray,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Botón de Añadir Nuevo Elemento
        Button(
            onClick = { onNavigateToAddItem(state.selectedItemType) },
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = addButtonText,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contenido según Estado
        Box(modifier = Modifier.fillMaxSize()) {
            when (val status = state.status) {
                is ManageItemStatus.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ManageItemStatus.Error -> {
                    Text(
                        text = status.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    if (filteredItems.isEmpty()) {
                        Text(
                            text = "No se encontraron elementos.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredItems) { item ->
                                ItemCard(
                                    product = item,
                                    onEdit = { onNavigateToEditItem(item) },
                                    onDelete = { onEvent(ManageItemEvent.DeleteItem(item.id)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemCard(
    product: Product,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del producto/servicio
            val imageUrl = product.images.firstOrNull()
            if (!imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Sin Img", fontSize = 10.sp, color = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Información
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Precio: $${product.price}",
                    style = MaterialTheme.typography.labelLarge
                )
                product.stock?.let { stock ->
                    Text(
                        text = "Stock: $stock",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                    if (product.description.isNotEmpty()) {
                    Text(
                        text = product.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.inverseSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Botones de acción
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onEdit,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(32.dp)
                        .width(70.dp)
                ) {
                    Text(
                        text = "Editar",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Button(
                    onClick = onDelete,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(32.dp)
                        .width(70.dp)
                ) {
                    Text(
                        text = "Eliminar",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ManageItemScreenPreview(){
    MarketUniversitarioTheme {
        ManageItemContent(
            state = ManageItemState(
                selectedItemType = ItemType.PRODUCT,
                status = ManageItemStatus.Success(
                    listOf(
                        Product(id = "1", name = "Smartphone X", price = 699.99, stock = 15, description = "Última generación..."),
                        Product(id = "2", name = "Auriculares Pro", price = 149.50, stock = 42, description = "Cancelación de ruido...")
                    )
                )
            ),
            onEvent = {},
            onNavigateBack = {},
            onNavigateToAddItem = {},
            onNavigateToEditItem = {}
        )
    }
}
