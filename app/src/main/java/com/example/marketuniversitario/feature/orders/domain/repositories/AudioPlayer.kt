package com.example.marketuniversitario.feature.orders.domain.repositories

interface AudioPlayer {
    fun play(url: String, onCompletion: () -> Unit)
    fun stop()
}
