package com.example.marketuniversitario.feature.home.domain.usecases

import android.net.Uri
import android.util.Log
import com.example.marketuniversitario.core.network.util.NetworkResult
import com.example.marketuniversitario.feature.home.domain.repository.VisionAiRepository
import javax.inject.Inject

class GetTagsFromImageUseCase @Inject constructor(
    private val visionAiRepository: VisionAiRepository
){
    suspend operator fun invoke(selectedImageUri: Uri): NetworkResult<List<String>> {
        return visionAiRepository.extractTagsFromImage(selectedImageUri)
    }
}
