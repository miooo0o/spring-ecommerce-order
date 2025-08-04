package ecommerce.repository

import ecommerce.model.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepositoryJPA : JpaRepository<Product, Long> {
    fun existsByName(name: String): Boolean
}

interface ProductRepository {
    fun findAll(): List<Product>

    fun save(entity: Product): Product

    fun deleteById(id: Long): Boolean

    fun existsByName(name: String): Boolean

    fun existsById(id: Long): Boolean

    fun findById(id: Long): Product?
}
