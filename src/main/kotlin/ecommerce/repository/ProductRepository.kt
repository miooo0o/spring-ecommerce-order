package ecommerce.repository

import ecommerce.model.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    fun existsByName(name: String): Boolean

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.options")
    fun findAllWithOptions(): List<Product>
}
