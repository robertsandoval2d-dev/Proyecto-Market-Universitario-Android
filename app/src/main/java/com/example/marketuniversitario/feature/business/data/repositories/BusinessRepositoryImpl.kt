package com.example.marketuniversitario.feature.business.data.repositories

import com.example.marketuniversitario.feature.business.data.datasources.BusinessRemoteDataSource
import com.example.marketuniversitario.feature.business.data.models.toDomain
import com.example.marketuniversitario.feature.business.data.models.toEntity
import com.example.marketuniversitario.feature.business.domain.models.Business
import com.example.marketuniversitario.feature.business.domain.repositories.BusinessRepository
import javax.inject.Inject

class BusinessRepositoryImpl @Inject constructor(
    private val remoteDataSource: BusinessRemoteDataSource
) : BusinessRepository {

    override suspend fun createBusiness(business: Business): Result<Business> {
        return try {
            val entity = business.toEntity()
            val generatedId = remoteDataSource.createBusiness(entity)
            Result.success(business.copy(id = generatedId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBusiness(businessId: String): Result<Business?> {
        return try {
            val entity = remoteDataSource.getBusiness(businessId)
            Result.success(entity?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
