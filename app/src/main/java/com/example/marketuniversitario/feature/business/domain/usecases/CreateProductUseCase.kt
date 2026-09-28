package com.example.marketuniversitario.feature.business.domain.usecases

import com.example.marketuniversitario.feature.business.domain.models.Product
import com.example.marketuniversitario.feature.business.domain.repositories.ProductRepository
import javax.inject.Inject

class CreateProductUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val getMyBusinessUseCase: GetMyBusinessUseCase
) {
    suspend operator fun invoke(product: Product): Result<Product> {
        if (product.name.isBlank()) {
            return Result.failure(Exception("El nombre es obligatorio"))
        }
        if (product.price <= 0) {
            return Result.failure(Exception("El precio debe ser mayor que 0"))
        }

        // Aseguramos que el producto se cree bajo el negocio correcto
        val business = getMyBusinessUseCase().getOrElse { return Result.failure(it) }
            ?: return Result.failure(Exception("No se encontró el negocio asociado"))

        val productToCreate = product.copy(
            businessId = business.id,
            businessName = business.name
        )

        return productRepository.createProduct(productToCreate)
    }
}
