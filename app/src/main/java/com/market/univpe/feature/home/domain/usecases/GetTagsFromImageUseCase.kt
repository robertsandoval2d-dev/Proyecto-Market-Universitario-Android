package com.market.univpe.feature.home.domain.usecases

import android.net.Uri
import com.market.univpe.core.network.util.NetworkResult
import com.market.univpe.feature.home.domain.repository.VisionAiRepository
import javax.inject.Inject

class GetTagsFromImageUseCase @Inject constructor(
    private val visionAiRepository: VisionAiRepository
){
    suspend operator fun invoke(selectedImageUri: Uri): NetworkResult<List<String>> {
        return visionAiRepository.extractTagsFromImage(selectedImageUri)
    }
}
