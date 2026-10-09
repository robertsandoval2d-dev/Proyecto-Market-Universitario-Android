package com.example.marketuniversitario.feature.reviews.data.repositories

import com.example.marketuniversitario.feature.reviews.data.datasources.ReviewRemoteDataSource
import com.example.marketuniversitario.feature.reviews.data.models.toDomain
import com.example.marketuniversitario.feature.reviews.data.models.toEntity
import com.example.marketuniversitario.feature.reviews.domain.models.Review
import com.example.marketuniversitario.feature.reviews.domain.repositories.ReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val remoteDataSource: ReviewRemoteDataSource
) : ReviewRepository {

    override fun getReviews(productId: String): Flow<List<Review>> {
        return remoteDataSource.getReviews(productId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getReview(productId: String, studentId: String): Result<Review?> {
        return try {
            val entity = remoteDataSource.getReview(productId, studentId)
            Result.success(entity?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveReview(productId: String, review: Review): Result<Unit> {
        return try {
            remoteDataSource.saveReview(productId, review.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
