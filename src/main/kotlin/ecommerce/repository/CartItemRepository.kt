package ecommerce.repository

import ecommerce.model.Cart
import ecommerce.model.CartItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface CartItemRepository : JpaRepository<CartItem, Long> {
    fun findDistinctByCreatedAtAfter(dateThreshold: LocalDateTime): List<CartItem>

    fun findDistinctByUpdatedAtAfter(dateThreshold: LocalDateTime): List<CartItem>

    @Query("SELECT DISTINCT c FROM Cart c LEFT JOIN FETCH c.items")
    fun findAllWithItems(): List<Cart>
}
