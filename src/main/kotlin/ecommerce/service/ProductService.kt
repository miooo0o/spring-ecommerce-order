package ecommerce.service

import ecommerce.dto.OptionResponse
import ecommerce.dto.PendingOption
import ecommerce.dto.PendingProduct
import ecommerce.dto.ProductRequest
import ecommerce.dto.ProductResponse
import ecommerce.dto.UpsertStatus.CREATED
import ecommerce.dto.UpsertStatus.UPDATED
import ecommerce.exception.ConflictException
import ecommerce.exception.DuplicateOptionNameException
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
class ProductService(
    private val productRepository: ProductRepository,
) {
    fun create(request: ProductRequest): Long {
        require(!request.options.isNullOrEmpty()) { "options must not be empty" }

        if (productRepository.existsByName(request.name)) {
            throw ConflictException("Product with name ${request.name} already exists")
        }

        val product = createProductWith(request)
        return productRepository.save(product).id
    }

    private fun createProductWith(request: ProductRequest): Product {

        require(!request.options.isNullOrEmpty()) { "Product must have at least one option" }
        require(request.options.distinct().size == request.options.size) {
            throw DuplicateOptionNameException("Duplicate option found in new options")
        }

        val pendingOptions = PendingOption.from(request)
        val pendingProduct = PendingProduct.from(request)
        return Product.withOption(pendingProduct, pendingOptions)
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
