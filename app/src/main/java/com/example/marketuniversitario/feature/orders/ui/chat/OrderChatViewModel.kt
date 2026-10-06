package com.example.marketuniversitario.feature.orders.ui.chat

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketuniversitario.feature.auth.domain.usecases.GetCurrentUserIdUseCase
import com.example.marketuniversitario.feature.orders.domain.repositories.AudioPlayer
import com.example.marketuniversitario.feature.orders.domain.repositories.AudioRecorder
import com.example.marketuniversitario.feature.orders.domain.usecases.GetMessagesForOrderUseCase
import com.example.marketuniversitario.feature.orders.domain.usecases.GetOrderUseCase
import com.example.marketuniversitario.feature.orders.domain.usecases.SendTextMessageUseCase
import com.example.marketuniversitario.feature.orders.domain.usecases.SendVoiceMessageUseCase
import com.example.marketuniversitario.feature.orders.domain.models.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class OrderChatViewModel @Inject constructor(
    private val getMessagesForOrderUseCase: GetMessagesForOrderUseCase,
    private val getOrderUseCase: GetOrderUseCase,
    private val sendTextMessageUseCase: SendTextMessageUseCase,
    private val sendVoiceMessageUseCase: SendVoiceMessageUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val audioRecorder: AudioRecorder,
    private val audioPlayer: AudioPlayer,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val orderId: String = checkNotNull(savedStateHandle["orderId"])
    private var currentAudioFile: File? = null
    private var recordingJob: Job? = null
    private var recordingStartTime = 0L

    private val _state = MutableStateFlow(OrderChatState())
    val state = _state.asStateFlow()

    init {
        loadCurrentUser()
        loadOrder()
        loadMessages()
    }

    private fun loadCurrentUser() {
        val userId = getCurrentUserIdUseCase()
        if (!userId.isNullOrBlank()) {
            _state.update { it.copy(currentUserId = userId) }
        }
    }

    private fun loadOrder() {
        viewModelScope.launch {
            getOrderUseCase(orderId).collect { order ->
                if (order != null) {
                    _state.update { it.copy(order = order) }
                }
            }
        }
    }

    fun onEvent(event: OrderChatEvent) {
        when (event) {
            is OrderChatEvent.MessageTextChanged -> {
                _state.update { it.copy(messageText = event.text) }
            }
            is OrderChatEvent.SendMessage -> {
                sendMessage()
            }
            is OrderChatEvent.SendVoiceMessage -> {
                sendVoiceMessage(event.audioUri)
            }
            is OrderChatEvent.StartRecording -> {
                onStartRecording()
            }
            is OrderChatEvent.StopRecording -> {
                onStopRecording()
            }
            is OrderChatEvent.PlayAudio -> {
                playAudio(event.url)
            }
            is OrderChatEvent.StopAudio -> {
                stopAudio()
            }
            is OrderChatEvent.BackClicked -> {
                // Manejado en la navegación de UI
            }
        }
    }

    private fun playAudio(url: String) {
        _state.update { it.copy(playingAudioUrl = url) }
        audioPlayer.play(url) {
            _state.update { it.copy(playingAudioUrl = null) }
        }
    }

    private fun stopAudio() {
        audioPlayer.stop()
        _state.update { it.copy(playingAudioUrl = null) }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
    }

    private fun onStartRecording() {
        if (_state.value.isRecording) return
        recordingStartTime = System.currentTimeMillis()
        currentAudioFile = audioRecorder.startRecording()
        if (currentAudioFile == null) return
        _state.update { it.copy(isRecording = true) }

        // Límite de 40 segundos para la grabación de voz
        recordingJob = viewModelScope.launch {
            delay(40_000L.milliseconds) // 40 segundos
            onStopRecording()
        }
    }

    private fun onStopRecording() {
        if (!_state.value.isRecording) return
        // Cancelamos el job del temporizador si el usuario soltó el botón antes de los 40s
        recordingJob?.cancel()
        recordingJob = null

        _state.update { it.copy(isRecording = false) }

        val elapsed = System.currentTimeMillis() - recordingStartTime
        val file = audioRecorder.stopRecording()

        // Si la grabación duró menos de 1 segundo (toque accidental), descartamos el archivo
        if (elapsed < 1000L) {
            file?.delete()
            currentAudioFile?.delete()
            currentAudioFile = null
            return
        }

        file?.let {
            if (it.exists() && it.length() > 0) {
                Log.d("OrderChatViewModel", "Archivo de audio generado: ${it.absolutePath}")
                onEvent(OrderChatEvent.SendVoiceMessage(Uri.fromFile(it)))
            }
        } ?: run {
            currentAudioFile?.let {
                if (it.exists() && it.length() > 0) {
                    Log.d("OrderChatViewModel", "Archivo de audio generado: ${it.absolutePath}")
                    onEvent(OrderChatEvent.SendVoiceMessage(Uri.fromFile(it)))
                }
            }
        }
        currentAudioFile = null
    }

    private fun loadMessages() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getMessagesForOrderUseCase(orderId).collect { messageList ->
                if (messageList.isNotEmpty()) {
                    _state.update { it.copy(messages = messageList, isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun sendMessage() {
        val text = _state.value.messageText.trim()
        if (text.isBlank()) return

        val orderId = _state.value.order.id
        val senderId = _state.value.currentUserId

        // Limpiamos el input de texto en el estado inmediatamente
        _state.update { it.copy(messageText = "") }

        viewModelScope.launch {
            sendTextMessageUseCase(orderId = orderId, senderId = senderId, text = text)
                .onFailure { error ->
                    _state.update { it.copy(error = error.message) }
                }
        }
    }

    private fun sendVoiceMessage(audioUri: Uri) {
        val orderId = _state.value.order.id
        val senderId = _state.value.currentUserId

        viewModelScope.launch {
            sendVoiceMessageUseCase(orderId = orderId, senderId = senderId, audioUri = audioUri)
                .onFailure { error ->
                    _state.update { it.copy(error = error.message) }
                }
        }
    }
}
