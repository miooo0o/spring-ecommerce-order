package ecommerce.service

import ecommerce.dto.ProductRequest
import ecommerce.exception.ConflictException
import ecommerce.exception.NotFoundException
import ecommerce.model.Product
import ecommerce.repository.ProductRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import kotlin.math.min

@Service
class ProductService(private val productRepository: ProductRepository) {
    fun create(productRequest: ProductRequest): Long {
        if (productRepository.existsByName(productRequest.name)) {
            throw ConflictException("Product with name ${productRequest.name} already exists")
        }
        val product = productRepository.save(productRequest.toProduct())
        return product.id
            ?: throw NotFoundException("Product with name ${productRequest.name} not found")
    }

    fun read(): List<Product> {
        val products = productRepository.findAll()
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
        productRepository.save(updateRequest.toProduct(id))
        return false
    }

    fun delete(id: Long) {
        productRepository.deleteById(id)
    }

    fun getPages(
        page: Int,
        size: Int,
    ): PageImpl<Product> {
        val products = productRepository.findAll()
        val pageRequest = PageRequest.of(page, size)
        val start = pageRequest.offset.toInt()
        val end = min(start + pageRequest.pageSize, products.size)

        val pageContent = products.subList(start, end)
        return PageImpl<Product>(pageContent, pageRequest, products.size.toLong())
    }
}
