package ecommerce.repository

import ecommerce.model.CartItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface CartItemRepository : JpaRepository<CartItem, Long> {
    fun findDistinctByCreatedAtAfter(dateThreshold: LocalDateTime): List<CartItem>

    fun findDistinctByUpdatedAtAfter(dateThreshold: LocalDateTime): List<CartItem>
}
