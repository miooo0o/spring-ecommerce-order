package ecommerce.repository

import ecommerce.model.CartItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CartItemRepositoryJPA : JpaRepository<CartItem, Long>
