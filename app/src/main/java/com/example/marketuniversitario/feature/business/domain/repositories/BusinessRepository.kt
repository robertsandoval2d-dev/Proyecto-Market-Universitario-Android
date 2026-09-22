package com.example.marketuniversitario.feature.business.domain.repositories

import com.example.marketuniversitario.feature.business.domain.models.Business

interface BusinessRepository {
    suspend fun createBusiness(business: Business): Result<Business>
    suspend fun getBusiness(businessId: String): Result<Business?>
}
