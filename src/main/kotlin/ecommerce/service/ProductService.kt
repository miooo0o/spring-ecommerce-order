package ecommerce.service

import ecommerce.dto.ProductRequest
import ecommerce.exception.ConflictException
import ecommerce.exception.NotFoundException
import ecommerce.model.Product
import ecommerce.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(private val productRepository: ProductRepository) {
    fun create(productRequest: ProductRequest): Long {
        if (productRepository.existsByName(productRequest.name)) {
            throw ConflictException("Product with name ${productRequest.name} already exists")
        }
        val id = productRepository.insertWithKeyHolder(productRequest.toProduct())
        return id
    }

    fun read(): List<Product> {
        val products = productRepository.findAllProducts()
        return products
    }

    fun upsert(
        updateRequest: ProductRequest,
        id: Long,
    ): Boolean {
        if (!productRepository.existsById(id)) {
            create(updateRequest)
            return true
        }
        if (!productRepository.update(updateRequest.toProduct(), id)) {
            throw NotFoundException()
        }
        return false
    }

    fun delete(id: Long) {
        if (!productRepository.delete(id)) {
            throw NotFoundException()
        }
    }
}
