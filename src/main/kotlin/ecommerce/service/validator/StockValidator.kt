package ecommerce.service.validator

import ecommerce.exception.InsufficientStockException
import ecommerce.exception.StockConflictException
import ecommerce.model.OrderItem
import org.springframework.stereotype.Component

@Component
class StockValidator {
    fun validateStockForOrder(orderItems: List<OrderItem>) {
        val errors = validateStock(orderItems)
        if (errors.isNotEmpty()) {
            throw InsufficientStockException("Stock validation failed", errors)
        }
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
