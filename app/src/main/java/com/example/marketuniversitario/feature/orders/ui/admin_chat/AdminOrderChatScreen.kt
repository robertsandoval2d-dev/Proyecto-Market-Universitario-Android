package com.example.marketuniversitario.feature.orders.ui.admin_chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.marketuniversitario.core.theme.MarketUniversitarioTheme
import com.example.marketuniversitario.feature.orders.domain.models.Message
import com.example.marketuniversitario.feature.orders.domain.models.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminOrderChatRoute(
    orderId: String,
    viewModel: AdminOrderChatViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(orderId) {
        viewModel.onEvent(AdminOrderChatEvent.InitOrder(orderId))
    }

    AdminOrderChatScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderChatScreen(
    state: AdminOrderChatState,
    onEvent: (AdminOrderChatEvent) -> Unit,
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Historial de Chat (Admin)",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        state.order?.let {
                            Text(
                                text = "Orden: #${it.id.take(8)} - ${it.productName}",
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Text(
                    text = "Modo Solo Lectura para Administrador",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(12.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header con identificación clara de las partes
            /*state.order?.let { order ->
                Card(
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "👤 Comprador:",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = order.buyerName.ifBlank { "ID: ${order.buyerId.take(6)}" },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "🏪 Vendedor / Negocio:",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = order.businessName.ifBlank { "ID: ${order.sellerId.take(6)}" },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }*/

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = state.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else if (state.messages.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No hay mensajes registrados en este chat.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.messages) { message ->
                        val order = state.order
                        val isBuyer = order != null && message.senderId == order.buyerId
                        val isSeller = order != null && message.senderId == order.sellerId

                        val senderLabel = when {
                            isBuyer -> "Comprador (${order?.buyerName ?: "Cliente"})"
                            isSeller -> "Vendedor (${order?.businessName ?: "Negocio"})"
                            else -> "Usuario #${message.senderId.take(6)}"
                        }

                        AdminMessageBubble(
                            message = message,
                            senderLabel = senderLabel,
                            isBuyer = isBuyer,
                            playingAudioUrl = state.playingAudioUrl,
                            onPlayAudio = { url -> onEvent(AdminOrderChatEvent.PlayAudio(url)) },
                            onStopAudio = { onEvent(AdminOrderChatEvent.StopAudio) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminMessageBubble(
    message: Message,
    senderLabel: String,
    isBuyer: Boolean,
    playingAudioUrl: String?,
    onPlayAudio: (String) -> Unit,
    onStopAudio: () -> Unit
) {
    val alignment = if (isBuyer) Alignment.Start else Alignment.End
    val backgroundColor = if (isBuyer) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer
    val textColor = if (isBuyer) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer
    val labelColor = if (isBuyer) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary

    val isPlaying = playingAudioUrl == message.audioUrl

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Text(
            text = senderLabel,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = labelColor,
            modifier = Modifier.padding(bottom = 2.dp, start = 4.dp, end = 4.dp)
        )

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(color = backgroundColor, shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            if (message.type == "AUDIO" && !message.audioUrl.isNullOrEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (isPlaying) {
                                onStopAudio()
                            } else {
                                onPlayAudio(message.audioUrl)
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Detener audio" else "Reproducir audio",
                            tint = textColor
                        )
                    }
                    Text(
                        text = if (isPlaying) "Reproduciendo audio... 🔊" else "Nota de voz 🎙️",
                        fontSize = 14.sp,
                        color = textColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                message.text?.let {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = textColor,
                        lineHeight = 18.sp
                    )
                }
            }
        }
        Text(
            text = formatAdminTime(message.createdAt),
            fontSize = 11.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
        )
    }
}

private fun formatAdminTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    return sdf.format(Date(timestamp))
}

@Preview(showBackground = true)
@Composable
fun AdminOrderChatScreenPreview() {
    MarketUniversitarioTheme {
        AdminOrderChatScreen(
            state = AdminOrderChatState(
                order = Order(
                    id = "ord_12345",
                    buyerName = "Juan Pérez",
                    businessName = "Café Campus"
                ),
                messages = listOf(
                    Message(
                        id = "1",
                        senderId = "buyer_123",
                        text = "Buenas tardes, mi producto llegó en mal estado.",
                        createdAt = System.currentTimeMillis() - 600000
                    ),
                    Message(
                        id = "2",
                        senderId = "seller_456",
                        text = "Hola, nosotros entregamos el paquete sellado. Revisa tu recibo.",
                        createdAt = System.currentTimeMillis() - 300000
                    )
                ),
                isLoading = false
            ),
            onEvent = {}
        )
    }
}
