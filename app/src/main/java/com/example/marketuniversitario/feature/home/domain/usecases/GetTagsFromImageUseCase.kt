package com.example.marketuniversitario.feature.home.domain.usecases

import android.util.Log
import com.example.marketuniversitario.feature.home.domain.repository.VisionAiRepository
import javax.inject.Inject

class GetTagsFromImageUseCase @Inject constructor(
    private val visionAiRepository: VisionAiRepository
){
    suspend operator fun invoke(selectedImageUriString: String): List<String> {
        return visionAiRepository.extractTagsFromImage(selectedImageUriString)
    }
}
