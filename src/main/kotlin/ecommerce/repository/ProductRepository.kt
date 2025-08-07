package ecommerce.repository

import ecommerce.model.Product
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    fun existsByName(name: String): Boolean

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.options")
    fun findAllWithOptions(): List<Product>

    @Query("SELECT p.id FROM Product p")
    fun findIds(pageable: Pageable): List<Long>

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.options WHERE p.id IN :ids")
    fun findAllWithOptionsByIds(
        @Param("ids") ids: List<Long>,
    ): List<Product>
}
