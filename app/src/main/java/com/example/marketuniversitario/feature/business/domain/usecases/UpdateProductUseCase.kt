package com.example.marketuniversitario.feature.business.domain.usecases

import com.example.marketuniversitario.feature.business.domain.models.Product
import com.example.marketuniversitario.feature.business.domain.repositories.ProductRepository
import javax.inject.Inject

class UpdateProductUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val getMyBusinessUseCase: GetMyBusinessUseCase
) {
    suspend operator fun invoke(product: Product): Result<Unit> {
        if (product.id.isBlank()) {
            return Result.failure(Exception("ID de producto inválido"))
        }
        if (product.name.isBlank()) {
            return Result.failure(Exception("El nombre es obligatorio"))
        }
        if (product.price <= 0) {
            return Result.failure(Exception("El precio debe ser mayor que 0"))
        }

        // Validación de seguridad: el usuario solo puede actualizar productos de su propio negocio
        val business = getMyBusinessUseCase().getOrElse { return Result.failure(it) }
            ?: return Result.failure(Exception("No se encontró el negocio asociado"))

        // Verificamos que el producto exista y pertenezca al negocio del usuario
        val existingProduct = productRepository.getProduct(product.id).getOrElse { null }
            ?: return Result.failure(Exception("El producto no existe"))

        if (existingProduct.businessId != business.id) {
            return Result.failure(Exception("No tienes permisos para modificar este producto"))
        }

        // Aseguramos que el businessId y businessName sean correctos
        val productToUpdate = product.copy(
            businessId = business.id,
            businessName = business.name
        )

        return productRepository.updateProduct(productToUpdate)
    }
}
