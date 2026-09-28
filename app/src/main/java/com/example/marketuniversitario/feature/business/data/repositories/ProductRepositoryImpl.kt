package com.example.marketuniversitario.feature.business.data.repositories

import com.example.marketuniversitario.feature.business.data.datasources.ProductRemoteDataSource
import com.example.marketuniversitario.feature.business.data.models.toDomain
import com.example.marketuniversitario.feature.business.data.models.toEntity
import com.example.marketuniversitario.feature.business.domain.models.Product
import com.example.marketuniversitario.feature.business.domain.repositories.ProductRepository
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProductRemoteDataSource
) : ProductRepository {

    override suspend fun createProduct(product: Product): Result<Product> {
        return try {
            val entity = product.toEntity()
            val generatedId = remoteDataSource.createProduct(entity)
            Result.success(product.copy(id = generatedId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProduct(productId: String): Result<Product?> {
        return try {
            val entity = remoteDataSource.getProduct(productId)
            Result.success(entity?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductsByBusiness(businessId: String): Result<List<Product>> {
        return try {
            val entities = remoteDataSource.getProductsByBusiness(businessId)
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProducts(): Result<List<Product>> {
        return try {
            val entities = remoteDataSource.getProducts()
            Result.success(entities.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            remoteDataSource.updateProduct(product.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            remoteDataSource.deleteProduct(productId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
