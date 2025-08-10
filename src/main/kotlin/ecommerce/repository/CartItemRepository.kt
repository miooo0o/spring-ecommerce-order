package ecommerce.repository

import ecommerce.model.CartItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface CartItemRepository : JpaRepository<CartItem, Long> {
    @EntityGraph(attributePaths = ["product"])
    fun findByCartMemberId(
        memberId: Long,
        pageable: Pageable,
    ): Page<CartItem>

    fun findDistinctByCreatedAtAfter(dateThreshold: LocalDateTime): List<CartItem>

    fun findDistinctByUpdatedAtAfter(dateThreshold: LocalDateTime): List<CartItem>
}
