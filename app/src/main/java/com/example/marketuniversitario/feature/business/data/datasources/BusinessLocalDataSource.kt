package com.example.marketuniversitario.feature.business.data.datasources

import com.example.marketuniversitario.feature.business.data.models.BusinessRoomEntity
import javax.inject.Inject

class BusinessLocalDataSource @Inject constructor(
    private val businessDao: BusinessDao
) {
    suspend fun getBusinessById(businessId: String): BusinessRoomEntity? {
        return businessDao.getBusinessById(businessId)
    }

    suspend fun getBusinessesByOwner(ownerId: String): List<BusinessRoomEntity> {
        return businessDao.getBusinessesByOwner(ownerId)
    }

    suspend fun getUnsyncedBusinesses(): List<BusinessRoomEntity> {
        return businessDao.getUnsyncedBusinesses()
    }

    suspend fun saveBusiness(business: BusinessRoomEntity) {
        businessDao.insertBusiness(business)
    }

    suspend fun markAsSynced(businessId: String) {
        businessDao.markAsSynced(businessId)
    }
    
    suspend fun deleteBusiness(businessId: String) {
        businessDao.deleteBusiness(businessId)
    }
}
