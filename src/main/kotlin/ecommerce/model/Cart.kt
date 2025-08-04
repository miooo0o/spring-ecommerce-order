package ecommerce.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

/**
 * Table carts {
 *   member_id bigint [ref: > members.id]
 *   id bigint [pk, increment]
 * }
 */
@Entity
@Table(name = "carts")
class Cart(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", unique = true)
    val member: Member,
    @OneToMany(
        mappedBy = "cart",
        cascade = [CascadeType.PERSIST, CascadeType.REMOVE],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    val items: MutableList<CartItem> = mutableListOf(),
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) {
    fun addItem(product: Product, quantity: Int) {
        require (quantity > 0) { "Item quantity must be greater than zero." }
        val existingItem = items.find { it.product == product }
        if (existingItem != null) {
            existingItem.quantity += quantity
        } else {
            val newItem = CartItem(product = product, cart = this, quantity = quantity)
            items.add(newItem)
        }
    }

    fun removeItem(product: Product, quantity: Int) {
        require (quantity > 0) { "Item quantity must be greater than zero." }
        val existingItem = items.find { it.product == product }
        require (existingItem != null) { "Item not found." }

        var quantityToRemove = quantity
        if ((existingItem.quantity - quantity) < 0) quantityToRemove = existingItem.quantity
        existingItem.quantity -= quantityToRemove
    }
}
