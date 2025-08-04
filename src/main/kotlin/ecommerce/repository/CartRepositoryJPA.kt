package ecommerce.repository

import ecommerce.model.Cart
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CartRepositoryJPA : JpaRepository<Cart, Long> {
    fun findCartByMemberId(memberId: Long): Cart?
}
