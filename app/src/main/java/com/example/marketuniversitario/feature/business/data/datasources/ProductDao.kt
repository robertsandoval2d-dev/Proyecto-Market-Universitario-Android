package com.example.marketuniversitario.feature.business.data.datasources

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.marketuniversitario.feature.business.data.models.ProductRoomEntity

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE businessId = :businessId")
    suspend fun getProductsByBusiness(businessId: String): List<ProductRoomEntity>

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: String): ProductRoomEntity?

    @Query("SELECT * FROM products WHERE isSynced = 0")
    suspend fun getUnsyncedProducts(): List<ProductRoomEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductRoomEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductRoomEntity>)

    @Query("UPDATE products SET isSynced = 1 WHERE id = :productId")
    suspend fun markAsSynced(productId: String)

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProduct(productId: String)
}