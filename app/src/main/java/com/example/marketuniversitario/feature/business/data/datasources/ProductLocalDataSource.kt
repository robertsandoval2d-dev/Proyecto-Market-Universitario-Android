package com.example.marketuniversitario.feature.business.data.datasources

import com.example.marketuniversitario.feature.business.data.models.ProductRoomEntity
import javax.inject.Inject

class ProductLocalDataSource @Inject constructor(
    private val productDao: ProductDao
) {
    suspend fun getProductsByBusiness(businessId: String): List<ProductRoomEntity> {
        return productDao.getProductsByBusiness(businessId)
    }

    suspend fun getProductById(productId: String): ProductRoomEntity? {
        return productDao.getProductById(productId)
    }

    suspend fun getUnsyncedProducts(): List<ProductRoomEntity> {
        return productDao.getUnsyncedProducts()
    }

    suspend fun updateBusinessNameInProducts(businessId: String, newBusinessName: String) {
        productDao.updateBusinessNameInProducts(businessId, newBusinessName)
    }

    suspend fun updateProductImageUrl(productId: String, imageUrl: String) {
        productDao.updateProductImageUrl(productId, imageUrl)
    }

    suspend fun incrementSalesCount(productId: String) {
        productDao.incrementSalesCount(productId)
    }

    suspend fun incrementViews(productId: String) {
        productDao.incrementViews(productId)
    }

    suspend fun saveProduct(product: ProductRoomEntity) {
        productDao.insertProduct(product)
    }

    suspend fun markAsSynced(productId: String) {
        productDao.markAsSynced(productId)
    }

    suspend fun deleteProduct(productId: String) {
        productDao.deleteProduct(productId)
    }
}
