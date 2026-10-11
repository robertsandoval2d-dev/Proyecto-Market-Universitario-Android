package com.example.marketuniversitario.feature.business.domain.usecases

import com.example.marketuniversitario.feature.business.domain.models.Business
import com.example.marketuniversitario.feature.business.domain.repositories.BusinessRepository
import javax.inject.Inject

class GetBusinessByIdUseCase @Inject constructor(
    private val businessRepository: BusinessRepository
) {
    suspend operator fun invoke(id: String): Result<Business?> {
        return businessRepository.getBusiness(id)
    }
}
