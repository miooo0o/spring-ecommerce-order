package ecommerce.service.validator

import ecommerce.exception.OrderProcessingException
import ecommerce.model.Order
import ecommerce.model.OrderItem
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class OrderValidator {
    fun validateOrderOrThrow(order: Order) {
        val errors = validateOrder(order)
        if (errors.isNotEmpty()) {
            throw OrderProcessingException("cart validation failed", errors)
        }
    }

    private fun validateOrder(order: Order): List<String> {
        val errors = mutableListOf<String>()

        if (order.items.isEmpty()) {
            errors.add("order must contain at least one item")
            return errors
        }
        validateMinimumOrderAmount(order, errors)
        errors.addAll(validateOrderItems(order.items))

        return errors
    }

    private fun validateMinimumOrderAmount(
        order: Order,
        errors: MutableList<String>,
    ) {
        if (order.totalMajor <= Order.MIN_AMOUNT_BIG_DECIMAL) {
            val minAmount = "${Order.MIN_AMOUNT_BIG_DECIMAL} ${order.currency}"
            errors.add("order total (${order.totalMajor} ${order.currency}) must be greater than $minAmount")
        }
    }

    private fun validateOrderItems(orderItems: List<OrderItem>): List<String> {
        val errors = mutableListOf<String>()

        orderItems.forEachIndexed { index, item ->
            val itemErrors = validateSingleOrderItem(item, index + 1)
            errors.addAll(itemErrors)
        }

        return errors
    }

    private fun validateSingleOrderItem(
        item: OrderItem,
        itemNumber: Int,
    ): List<String> {
        val errors = mutableListOf<String>()
        val itemPrefix = "item #$itemNumber (${item.productName})"

        when {
            item.quantity <= 0 -> {
                errors.add("$itemPrefix: Quantity must be greater than 0, but got ${item.quantity}")
            }

            item.quantity > MAX_QUANTITY_PER_ITEM -> {
                errors.add("$itemPrefix: Quantity cannot exceed $MAX_QUANTITY_PER_ITEM, but got ${item.quantity}")
            }
        }

        if (item.unitPrice <= BigDecimal.ZERO) {
            errors.add("$itemPrefix: Unit price must be greater than 0")
        }
        return errors
    }

    companion object {
        private const val MAX_QUANTITY_PER_ITEM = 999
    }
}
