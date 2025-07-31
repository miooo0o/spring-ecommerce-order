package ecommerce.model

import ecommerce.dto.CartItemResponse
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import java.io.Serializable
import java.time.LocalDateTime

@Embeddable
data class CartItemId(
    @Column(name = "cart_id")
    val cartId: Long = 0,

    @Column(name = "product_id")
    val productId: Long = 0
) : Serializable

@Entity
@Table(name = "cart_items")
class CartItem(

    @EmbeddedId
    val id: CartItemId = CartItemId(),

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cartId")
    @JoinColumn(name = "cart_id")
    val cart: Cart,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    val product: Product,

    @Column(nullable = false)
    var quantity: Int = 1,

    @Column(name = "created_at", updatable = false, insertable = false)
    val createdAt: LocalDateTime? = null

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
