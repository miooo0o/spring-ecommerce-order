package ecommerce.model

import ecommerce.dto.CartItemResponse
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

/**
 * Table cart_items {
 *   cart_id bigint [ref: > carts.id]
 *   product_id bigint [ref: > products.id]
 *   quantity int [default: 1]
 *   created_at timestamp [default: current_timestamp]
 *   id bigint [pk, increment]
 * }
 *
 * CartItem is child of Cart
 */
@Entity
@Table(name = "cart_items")
class CartItem(
    @JoinColumn(name = "cart_id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var cart: Cart,
    @JoinColumn(name = "product_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    var product: Product,
    @Column(nullable = false)
    var quantity: Int = 1,
    @Column(name = "created_at", updatable = false, insertable = false)
    val createdAt: LocalDateTime? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) {
    fun toResponse(): CartItemResponse {
        return CartItemResponse(
            quantity = quantity.toLong(),
            productId = product.id ?: 0,
            productName = product.name,
            productPrice = product.price,
            productImageUrl = product.imageUrl,
        )
    }
}
