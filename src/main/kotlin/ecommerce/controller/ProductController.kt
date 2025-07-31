package ecommerce.controller

import ecommerce.annotation.Admin
import ecommerce.dto.ProductRequest
import ecommerce.dto.RegisteredMember
import ecommerce.model.Product
import ecommerce.service.ProductService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
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
    fun read(
        @Admin admin: RegisteredMember,
    ): ResponseEntity<List<Product>> {
        val products = productService.read()
        return ResponseEntity.ok().body(products)
    }

    @PutMapping("/api/products/{id}")
    fun upsert(
        @RequestBody @Valid newProduct: ProductRequest,
        @PathVariable id: Long,
        @Admin admin: RegisteredMember,
    ): ResponseEntity<Unit> {
        val created = productService.upsert(newProduct, id)
        return if (created) {
            return ResponseEntity.created(URI.create("/api/products/$id")).build()
        } else {
            ResponseEntity.ok().build()
        }
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
