package com.market.univpe.feature.business.domain.repositories

import com.market.univpe.feature.business.domain.models.Business
import kotlinx.coroutines.flow.Flow

interface BusinessRepository {
    suspend fun createBusiness(business: Business): Result<Business>
    suspend fun getBusiness(businessId: String): Result<Business?>
    suspend fun getPublicBusinessInfo(businessId: String): Result<Business?>
    suspend fun updateBusiness(business: Business): Result<Unit>
    suspend fun updateBusinessRemote(business: Business): Result<Unit>
    fun getAllBusinesses(): Flow<List<Business>>
}
