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

@Entity
@Table(name = "carts")
class Cart(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", unique = true)
    val member: Member,
    @OneToMany(
        mappedBy = "cart",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    val items: MutableList<CartItem> = mutableListOf(),
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
) {
    fun addItem(
        product: Product,
        quantity: Int,
    ): CartItem {
        require(quantity > 0) { "Item quantity must be greater than zero." }
        val existingItem = items.find { it.product.id == product.id }
        return when (existingItem) {
            null -> addNewItem(product, quantity)
            else -> updateExistingItem(existingItem, quantity)
        }
    }

    private fun addNewItem(
        product: Product,
        quantity: Int,
    ): CartItem {
        val newItem =
            CartItem(
                product = product,
                cart = this,
                quantity = quantity,
            )
        items.add(newItem)
        return newItem
    }

    private fun updateExistingItem(
        existingItem: CartItem,
        quantity: Int,
    ): CartItem {
        existingItem.changeQuantityTo(quantity)
        existingItem.markAsUpdated()
        return existingItem
    }

    fun removeItem(product: Product) {
        val existingItem =
            items.find { it.product.id == product.id }
                ?: throw IllegalArgumentException("Item not found.")
        items.remove(existingItem)
    }
}
