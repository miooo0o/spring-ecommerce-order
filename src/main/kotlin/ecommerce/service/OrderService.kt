package ecommerce.service

import ecommerce.dto.OrderRequest
import ecommerce.exception.OrderProcessingException
import ecommerce.model.Order
import ecommerce.model.OrderItem
import ecommerce.model.mapper.toPaymentRequest
import ecommerce.repository.OrderRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Transactional
@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val paymentService: PaymentService,
    private val cartService: CartService,
) {

    fun processOrder(memberId: Long, request: OrderRequest) {
        val order = placeOrder(memberId, request)
        validateOrderOrThrow(order) //TODO: should I validate here or after stock hold before payment?
        val paymentRequest = order.toPaymentRequest(request.paymentMethod)

        // TODO: implement payment process here
        //  and maybe logic will be...
        //  inventoryService.holdStock(order.items)
        //  val paymentResponse = paymentService.makePayment(paymentRequest)

    }

    private fun placeOrder(
        memberId: Long,
        request: OrderRequest,
    ): Order {
        val cart = cartService.getCartForOrder(memberId)
        val order = Order.fromCart(cart, request.currency)
        return orderRepository.save(order)
    }

    private fun validateOrderOrThrow(order: Order) {
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

    private fun validateMinimumOrderAmount(order: Order, errors: MutableList<String>) {
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

    private fun validateSingleOrderItem(item: OrderItem, itemNumber: Int): List<String> {
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

        if (item.option.availableStock < item.quantity) {
            errors.add("$itemPrefix: Insufficient stock. Available: ${item.option.availableStock}, Requested: ${item.quantity}")
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
