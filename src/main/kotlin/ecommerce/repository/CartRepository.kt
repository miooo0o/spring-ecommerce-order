package ecommerce.repository

import ecommerce.model.Cart
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CartRepository : JpaRepository<Cart, Long> {
    fun findCartByMemberId(memberId: Long): Cart?

    @Query("SELECT DISTINCT c FROM Cart c LEFT JOIN FETCH c.items")
    fun findAllWithItems(): List<Cart>
}
