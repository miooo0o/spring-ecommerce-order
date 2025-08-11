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

    fun addItem(product: Product, option: Option, quantity: Int): CartItem {
        require(quantity > 0) { "Quantity must be greater than zero." }
        require(option.quantity >= quantity) { "Option stock is insufficient." }

        val existingItem = findItemByProduct(product)
        return when (existingItem) {
            null -> createNewItem(product, option, quantity)
            else -> updateItem(existingItem, option, quantity)
        }
    }

    fun removeItem(product: Product) {
        val item = findItemByProduct(product)
            ?: throw IllegalArgumentException("Item not found in cart.")
        items.remove(item)
    }

    private fun findItemByProduct(product: Product): CartItem? =
        items.find { it.product.name == product.name }

    private fun createNewItem(product: Product, option: Option, quantity: Int): CartItem {
        val newItem = CartItem(
            cart = this,
            product = product,
            option = option,
            quantity = quantity
        )
        items.add(newItem)
        return newItem
    }

    private fun updateItem(existingItem: CartItem, option: Option, quantity: Int): CartItem {
        return when (existingItem.option) {
            option -> existingItem.changeQuantityTo(quantity)
            else -> existingItem.overrideOptionWith(option, quantity)
        }
    }
}
