package ecommerce.service

import ecommerce.exception.StockConflictException
import ecommerce.model.Order
import ecommerce.model.OrderItem
import ecommerce.repository.OrderRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val cartService: CartService,
) {
    @Transactional
    fun createOrder(memberId: Long): Order {
        val cart = cartService.getCartForOrder(memberId)
        val order = Order.fromCart(cart)

        validateStockForPayment(order.items)
        return orderRepository.save(order)
    }

    fun validateStockForPayment(orderItems: List<OrderItem>) {
        val errors = validateStock(orderItems)
        if (errors.isNotEmpty()) {
            throw StockConflictException("Stock changed during payment processing", errors)
        }
    }

    private fun validateStock(orderItems: List<OrderItem>): List<String> {
        val errors = mutableListOf<String>()

        orderItems.forEachIndexed { index, item ->
            val itemPrefix = "Item #${index + 1} (${item.productName})"

            if (item.option.availableStock < item.quantity) {
                errors.add(
                    "$itemPrefix: Insufficient stock. " +
                        "Available: ${item.option.availableStock}, Requested: ${item.quantity}",
                )
            }
        }

        return errors
    }
}
