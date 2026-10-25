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

    @Query("UPDATE products SET businessName = :newBusinessName WHERE businessId = :businessId")
    suspend fun updateBusinessNameInProducts(businessId: String, newBusinessName: String)

    @Query("UPDATE products SET images = :imageUrl WHERE id = :productId")
    suspend fun updateProductImageUrl(productId: String, imageUrl: String)

    @Query("UPDATE products SET salesCount = salesCount + 1, isSynced = 0 WHERE id = :productId")
    suspend fun incrementSalesCount(productId: String)

    @Query("UPDATE products SET views = views + 1, isSynced = 0 WHERE id = :productId")
    suspend fun incrementViews(productId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductRoomEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductRoomEntity>)

    @Query("UPDATE products SET isSynced = 1 WHERE id = :productId")
    suspend fun markAsSynced(productId: String)

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProduct(productId: String)
}
