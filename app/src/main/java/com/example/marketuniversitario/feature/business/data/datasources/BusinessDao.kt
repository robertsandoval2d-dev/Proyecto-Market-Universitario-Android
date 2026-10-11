package com.example.marketuniversitario.feature.business.data.datasources

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.marketuniversitario.feature.business.data.models.BusinessRoomEntity

@Dao
interface BusinessDao {
    @Query("SELECT * FROM businesses WHERE id = :businessId")
    suspend fun getBusinessById(businessId: String): BusinessRoomEntity?

    @Query("SELECT * FROM businesses WHERE ownerId = :ownerId")
    suspend fun getBusinessesByOwner(ownerId: String): List<BusinessRoomEntity>

    @Query("SELECT * FROM businesses WHERE isSynced = 0")
    suspend fun getUnsyncedBusinesses(): List<BusinessRoomEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusiness(business: BusinessRoomEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusinesses(businesses: List<BusinessRoomEntity>)

    @Query("UPDATE businesses SET isSynced = 1 WHERE id = :businessId")
    suspend fun markAsSynced(businessId: String)
    
    @Query("DELETE FROM businesses WHERE id = :businessId")
    suspend fun deleteBusiness(businessId: String)
}
