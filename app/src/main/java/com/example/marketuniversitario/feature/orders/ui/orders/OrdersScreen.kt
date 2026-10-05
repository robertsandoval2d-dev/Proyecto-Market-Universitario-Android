package com.example.marketuniversitario.feature.orders.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.marketuniversitario.feature.orders.domain.model.Order
import com.example.marketuniversitario.feature.orders.domain.model.OrderStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrdersRoute(
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    OrdersScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun OrdersScreen(
    state: OrdersState,
    onEvent: (OrdersEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Pestañas (Tabs) superiores
        TabRow(
            selectedTabIndex = state.selectedTab.ordinal,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedTab.ordinal]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            OrderTab.entries.forEach { tab ->
                Tab(
                    selected = state.selectedTab == tab,
                    onClick = { onEvent(OrdersEvent.TabChanged(tab)) },
                    text = {
                        Text(
                            text = tab.title,
                            fontWeight = if (state.selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Lista de Pedidos
        if (state.orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No tienes pedidos aquí aún.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.orders) { order ->
                    OrderCard(order = order, onEvent = onEvent)
                }
            }
        }
    }
}

@Composable
fun OrderCard(
    order: Order,
    onEvent: (OrdersEvent) -> Unit
) {
    // Determinar colores del "Chip" de estado
    val (statusBgColor, statusTextColor) = when (order.status) {
        OrderStatus.PENDING -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        OrderStatus.PREPARING -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        OrderStatus.READY_FOR_PICKUP -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        OrderStatus.COMPLETED -> Color(0xFFE8F5E9) to Color(0xFF2E7D32) // Verde genérico para éxito
        OrderStatus.REJECTED -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.US).format(Date(order.createdAt))
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Chip de Estado
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusBgColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = order.status.displayName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Imagen del Producto
                if (order.productImage.isNotBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(order.productImage)
                            .crossfade(true)
                            .build(),
                        contentDescription = order.productName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Sin Img", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Detalles del Producto
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.productName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Cantidad: ${order.quantity}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Total: S/ ${"%.2f".format(order.productPrice * order.quantity)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // --- ACCIONES DEPENDIENDO DEL ROL Y ESTADO ---
            // Solo si somos el vendedor (Mis Ventas) y está Pendiente
            if (order.isSale && order.status == OrderStatus.PENDING) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = { onEvent(OrdersEvent.RejectOrder(order.id)) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                    ) {
                        Text("Rechazar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onEvent(OrdersEvent.AcceptOrder(order.id)) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                    ) {
                        Text("Aceptar Pedido")
                    }
                }
            }

            // Si somos compradores y está aceptado, podríamos mostrar un botón de "Coordinar Entrega"
            if (!order.isSale && (order.status == OrderStatus.PREPARING || order.status == OrderStatus.READY_FOR_PICKUP)) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /* Navegar al Chat (Siguiente fase del proyecto) */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Text("Coordinar Entrega (Chat)")
                }
            }
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true, name = "1. Mis Ventas (Vendedor)")
@Composable
fun OrdersScreenSalesPreview() {
    MarketUniversitarioTheme {
        OrdersScreen(
            state = OrdersState(
                selectedTab = OrderTab.SALES,
                orders = listOf(
                    Order(
                        id = "1",
                        productName = "Brownie de Chocolate",
                        productImage = "",
                        productPrice = 4.5,
                        quantity = 3,
                        status = OrderStatus.PENDING,
                        isSale = true
                    )
                )
            ),
            onEvent = {}
        )
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true, name = "2. Mis Compras (Cliente)")
@Composable
fun OrdersScreenPurchasesPreview() {
    MarketUniversitarioTheme {
        OrdersScreen(
            state = OrdersState(
                selectedTab = OrderTab.PURCHASES,
                orders = listOf(
                    Order(
                        id = "2",
                        productName = "Menú Almuerzo - FISI",
                        productImage = "",
                        productPrice = 12.0,
                        quantity = 2,
                        status = OrderStatus.PREPARING,
                        isSale = false
                    ),
                    Order(
                        id = "3",
                        productName = "Calculadora Científica Casio",
                        productImage = "",
                        productPrice = 45.0,
                        quantity = 1,
                        status = OrderStatus.READY_FOR_PICKUP,
                        isSale = false
                    )
                )
            ),
            onEvent = {}
        )
    }
}