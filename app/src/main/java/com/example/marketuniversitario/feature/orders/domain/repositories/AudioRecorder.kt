package com.example.marketuniversitario.feature.orders.domain.repositories

import java.io.File

interface AudioRecorder {
    fun startRecording(): File?
    fun stopRecording(): File?
}
