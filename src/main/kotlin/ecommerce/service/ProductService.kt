package ecommerce.service

import ecommerce.dto.OptionResponse
import ecommerce.dto.ProductRequest
import ecommerce.dto.ProductResponse
import ecommerce.dto.UpsertStatus.CREATED
import ecommerce.dto.UpsertStatus.UPDATED
import ecommerce.exception.ConflictException
import ecommerce.model.Product
import ecommerce.model.mapper.OptionMapper
import ecommerce.repository.ProductRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.min

@Transactional
@Service
class ProductService(private val productRepository: ProductRepository) {
    fun create(productRequest: ProductRequest): Long {
        require(!productRequest.options.isNullOrEmpty()) { "options must not be empty" }

        if (productRepository.existsByName(productRequest.name)) {
            throw ConflictException("Product with name ${productRequest.name} already exists")
        }
        val product = productRepository.save(productRequest.toProduct())
        return product.id
    }

    @Transactional(readOnly = true)
    fun read(): List<Product> {
        val products = productRepository.findAllWithOptions()
        return products
    }

    fun upsert(
        updateRequest: ProductRequest,
        id: Long,
    ): ProductResponse {
        val upsertStatus = if (!productRepository.existsById(id)) CREATED else UPDATED
        val entity = productRepository.save(updateRequest.toProduct(id))

        return ProductResponse(
            productId = entity.id,
            price = entity.price,
            imageUrl = entity.imageUrl,
            optionNames = entity.options.map { it.name },
            upsertStatus = upsertStatus,
        )
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

    fun findOptions(id: Long): List<OptionResponse> {
        val product = productRepository.findById(id).get()
        return product.options.map { OptionMapper.toOptionResponse(it) }
    }
}

// TODO: add at service layer - Option.kt
// if (product.options.isNotEmpty()) {
//    require(product.options.all { it.name != this.name }) { "duplicate name ${product.name} found" }
// }
