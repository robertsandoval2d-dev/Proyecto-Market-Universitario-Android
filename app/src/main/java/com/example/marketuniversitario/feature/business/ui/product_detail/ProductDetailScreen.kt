package com.example.marketuniversitario.feature.business.ui.product_detail

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.example.marketuniversitario.feature.business.domain.models.Product
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProductDetailRoute(
    viewModel: ProductDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToBusiness: (businessId: String) -> Unit = {},
    onRequestOrderClick: (productId: String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    ProductDetailScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
        onNavigateToBusiness = onNavigateToBusiness,
        onRequestOrderClick = onRequestOrderClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    state: ProductDetailState,
    onEvent: (ProductDetailEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToBusiness: (businessId: String) -> Unit,
    onRequestOrderClick: (String) -> Unit
) {
    val product = state.product

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = product?.name ?: "Detalle de Producto",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { onEvent(ProductDetailEvent.ToggleFavorite) }) {
                        Icon(
                            imageVector = if (state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (state.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            if (product != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Precio Total",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "S/ %.2f".format(product.price),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Button(
                            onClick = { onRequestOrderClick(product.id) },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Solicitar Pedido", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (state.status) {
                is ProductDetailStatus.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ProductDetailStatus.Error -> {
                    Text(
                        text = (state.status as ProductDetailStatus.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    if (product != null) {
                        ProductDetailContent(
                            product = product,
                            comments = state.comments,
                            newCommentText = state.newCommentText,
                            newCommentRating = state.newCommentRating,
                            onEvent = onEvent,
                            onNavigateToBusiness = onNavigateToBusiness
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductDetailContent(
    product: Product,
    comments: List<Comment>,
    newCommentText: String,
    newCommentRating: Int,
    onEvent: (ProductDetailEvent) -> Unit,
    onNavigateToBusiness: (businessId: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Imagen Principal
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                val imageUrl = product.images.firstOrNull()
                if (!imageUrl.isNullOrBlank()) {
                    SubcomposeAsyncImage(
                        model = imageUrl,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }

        // Título y Precio
        item {
            Column {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "S/ %.2f".format(product.price),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Card del Negocio (Clickeable)
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToBusiness(product.businessId) }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = product.businessName.ifBlank { "Ver Negocio" },
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "Toca para ir a la tienda del emprendedor",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Ir al negocio",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Descripción
        item {
            Column {
                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = product.description.ifBlank { "Sin descripción detallada disponible." },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }

        // Sección: Comentarios y Reseñas
        item {
            Column {
                Text(
                    text = "Opiniones de la Comunidad (${comments.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Input para agregar nuevo comentario
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Deja tu opinión o consulta:",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Calificación con estrellas
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            (1..5).forEach { star ->
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Estrella $star",
                                    tint = if (star <= newCommentRating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable { onEvent(ProductDetailEvent.NewRatingChanged(star)) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = newCommentText,
                                onValueChange = { onEvent(ProductDetailEvent.NewCommentChanged(it)) },
                                placeholder = { Text("Escribe una reseña...") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { onEvent(ProductDetailEvent.AddComment) },
                                enabled = newCommentText.isNotBlank()
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Enviar comentario",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Lista de Comentarios de los usuarios
        items(
            items = comments,
            key = { comment -> if (comment.id.isNotBlank()) comment.id else comment.hashCode().toString() }
        ) { comment ->
            CommentCard(comment = comment)
        }
    }
}

@Composable
fun CommentCard(comment: Comment) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = comment.userName.take(1).uppercase(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = comment.userName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Estrellas
                Row {
                    (1..5).forEach { star ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (star <= comment.rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            val formattedDate = rememberDate(comment.timestamp)
            Text(
                text = formattedDate,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun rememberDate(timestamp: Long): String {
    return remember(timestamp) {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        sdf.format(Date(timestamp))
    }
}
