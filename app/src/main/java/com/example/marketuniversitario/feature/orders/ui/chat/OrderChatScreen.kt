package com.example.marketuniversitario.feature.orders.ui.chat

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.marketuniversitario.core.theme.MarketUniversitarioTheme
import com.example.marketuniversitario.feature.orders.domain.models.Order
import com.example.marketuniversitario.feature.orders.domain.models.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Mock de Order
val mockOrder = Order(
    id = "order_1001",
    buyerId = "user_jack",
    buyerName = "Jack",
    sellerId = "seller_gadget",
    businessId = "bus_gadget",
    businessName = "La Esquina del Gadget",
    productName = "Audífono Bluetooth",
    productPrice = 89.90,
    quantity = 1,
    totalPrice = 89.90
)

// Mock de lista de mensajes usando la entidad Message
val mockMessages = listOf(
    Message(
        id = "1",
        orderId = mockOrder.id,
        senderId = mockOrder.sellerId,
        text = "Hola Jack, ¡estamos listos para ayudarte con tu emprendimiento!",
        createdAt = 1716130860000L // 10:01 AM
    ),
    Message(
        id = "2",
        orderId = mockOrder.id,
        senderId = mockOrder.buyerId,
        text = "¡Hola! Vi sus nuevos dispositivos. ¿Tienen el audífono en negro?",
        createdAt = 1716130920000L // 10:02 AM
    ),
    Message(
        id = "3",
        orderId = mockOrder.id,
        senderId = mockOrder.sellerId,
        text = "¡Claro que sí! Tenemos en stock. ¿Te gustaría ordenar uno?",
        createdAt = 1716130980000L // 10:03 AM
    ),
    Message(
        id = "4",
        orderId = mockOrder.id,
        senderId = mockOrder.buyerId,
        type = "AUDIO",
        audioUrl = "aa",
        createdAt = 1716130980000L // 10:03 AM
    )
)

@Composable
fun OrderChatRoute(
    viewModel: OrderChatViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    OrderChatScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderChatScreen(
    state: OrderChatState,
    onEvent: (OrderChatEvent) -> Unit,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onEvent(OrderChatEvent.StartRecording)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.order.businessName,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onEvent(OrderChatEvent.BackClicked)
                        onBackClick()
                    }) {
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
            ChatInputBar(
                messageText = state.messageText,
                isRecording = state.isRecording,
                onTextChanged = { onEvent(OrderChatEvent.MessageTextChanged(it)) },
                onSendMessage = { onEvent(OrderChatEvent.SendMessage) },
                onStartRecording = {
                    val permissionCheck = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    )
                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                        onEvent(OrderChatEvent.StartRecording)
                    } else {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                onStopRecording = {
                    onEvent(OrderChatEvent.StopRecording)
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.messages) { message ->
                val isFromMe = message.senderId == state.currentUserId
                MessageBubble(
                    message = message,
                    isFromMe = isFromMe,
                    playingAudioUrl = state.playingAudioUrl,
                    onPlayAudio = { url -> onEvent(OrderChatEvent.PlayAudio(url)) },
                    onStopAudio = { onEvent(OrderChatEvent.StopAudio) }
                )
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: Message,
    isFromMe: Boolean,
    playingAudioUrl: String?,
    onPlayAudio: (String) -> Unit,
    onStopAudio: () -> Unit
) {
    val alignment = if (isFromMe) Alignment.End else Alignment.Start
    val backgroundColor = if (isFromMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary
    val textColor = if (isFromMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.inverseSurface

    val shape = RoundedCornerShape(16.dp)
    val isPlaying = playingAudioUrl == message.audioUrl

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(color = backgroundColor, shape = shape)
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
                        text = if (isPlaying) "Reproduciendo... 🔊" else "Nota de voz 🎙️",
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
            text = formatTime(message.createdAt),
            fontSize = 11.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
        )
    }
}

@Composable
fun ChatInputBar(
    messageText: String,
    isRecording: Boolean,
    onTextChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit
) {
    val micTint = if (isRecording) Color.Red else MaterialTheme.colorScheme.primary
    val haptic = LocalHapticFeedback.current

    // Animaciones para que el cambio sea suave
    val micScale by animateFloatAsState(
        targetValue = if (isRecording) 1.5f else 1f,
        label = "micScale"
    )
    val micColor by animateColorAsState(
        targetValue = if (isRecording) Color.Red else Color.Gray, // Puedes cambiar Red por tu BrandPrimary
        label = "micColor"
    )
    val micBgColor by animateColorAsState(
        targetValue = if (isRecording) Color.Red.copy(alpha = 0.1f) else Color.Transparent,
        label = "micBgColor"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color = micBgColor, shape = CircleShape) // Fondo sutil al presionar
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            // 1. Cuando el usuario toca (Mantiene presionado)
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress) // Pequeña vibración
                            onStartRecording()

                            try {
                                // Espera hasta que el usuario suelte el dedo
                                awaitRelease()
                            } finally {
                                // 2. Cuando el usuario suelta o cancela el gesto
                                onStopRecording()
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Grabar audio",
                tint = micColor, // Cambia de gris a rojo
                modifier = Modifier
                    .size(24.dp)
                    .scale(micScale) // Crece un 50% al mantener presionado
            )
        }

        OutlinedTextField(
            value = messageText,
            onValueChange = onTextChanged,
            placeholder = { Text("Escribe tu mensaje...", color = Color.Gray, fontSize = 14.sp) },
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 44.dp)
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.scrim,
                unfocusedTextColor = MaterialTheme.colorScheme.scrim,
                cursorColor = MaterialTheme.colorScheme.outline,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5)
            ),
            singleLine = true
        )

        IconButton(onClick = {
            if (messageText.isNotBlank()) {
                onSendMessage()
            }
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Enviar",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    return sdf.format(Date(timestamp))
}

@Preview(showBackground = true)
@Composable
fun OrderChatScreenPreview() {
    MarketUniversitarioTheme(darkTheme = false) {
        OrderChatScreen(
            state = OrderChatState(),
            onEvent = {}
        )
    }
}
