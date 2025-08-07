package ecommerce.controller

import ecommerce.annotation.Admin
import ecommerce.dto.OptionResponse
import ecommerce.dto.ProductRequest
import ecommerce.dto.RegisteredMember
import ecommerce.dto.UpsertStatus
import ecommerce.model.Product
import ecommerce.service.ProductService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class ProductController(private val productService: ProductService) {
    @PostMapping("/api/products")
    fun create(
        @RequestBody @Valid product: ProductRequest,
        @Admin admin: RegisteredMember,
    ): ResponseEntity<Unit> {
        val id = productService.create(product)
        return ResponseEntity.created(URI.create("/api/products/$id")).build()
    }

    @GetMapping("/api/products")
    fun read(): ResponseEntity<List<Product>> {
        val products = productService.read()
        return ResponseEntity.ok().body(products)
    }

    @GetMapping("/api/products-page")
    fun asPages(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<Page<Product>> {
        val productPage = productService.getPages(page, size)
        val headers =
            HttpHeaders().apply {
                add("X-Page-Number", productPage.number.toString())
                add("X-Page-Size", productPage.size.toString())
            }

        return ResponseEntity.ok()
            .headers(headers)
            .body(productPage)
    }

    @PutMapping("/api/products/{id}")
    fun upsert(
        @Admin admin: RegisteredMember,
        @RequestBody @Valid newProduct: ProductRequest,
        @PathVariable id: Long,
    ): ResponseEntity<ProductResponse> {
        val response = productService.upsert(newProduct, id)
        return when (response.upsertStatus) {
            UpsertStatus.CREATED -> ResponseEntity.created(URI.create("/api/products/$id")).build()
            UpsertStatus.UPDATED -> ResponseEntity.ok().build()
        }
    }

    @GetMapping("/api/products/{id}/options")
    fun getOptions(
        @PathVariable id: Long,
    ): ResponseEntity<List<OptionResponse>> {
        val response = productService.findOptions(id)
        return ResponseEntity.ok().body(response)
    }

    @DeleteMapping("/api/products/{id}")
    fun delete(
        @PathVariable id: Long,
        @Admin admin: RegisteredMember,
    ): ResponseEntity<Unit> {
        productService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
