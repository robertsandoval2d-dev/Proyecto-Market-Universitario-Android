package com.market.univpe.feature.business.domain.usecases

import com.market.univpe.feature.business.domain.models.Business
import com.market.univpe.feature.business.domain.repositories.BusinessRepository
import javax.inject.Inject

class GetBusinessByIdUseCase @Inject constructor(
    private val businessRepository: BusinessRepository
) {
    suspend operator fun invoke(id: String): Result<Business?> {
        return businessRepository.getPublicBusinessInfo(id)
    }
}
