package ecommerce.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
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
    @JoinColumn(name = "product_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    var product: Product,
    @JoinColumn(name = "cart_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    var cart: Cart,
    @Column(nullable = false)
    var quantity: Int = 1,
    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = createdAt,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
)
