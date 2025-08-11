package ecommerce.service

import ecommerce.client.StripeClient
import ecommerce.dto.OrderRequest
import ecommerce.dto.OrderResponse
import ecommerce.exception.NotFoundException
import ecommerce.model.Cart
import ecommerce.model.Order
import ecommerce.model.OrderItem
import ecommerce.model.mapper.OrderItemMapper
import ecommerce.repository.CartItemRepository
import ecommerce.repository.CartRepository
import ecommerce.repository.OrderRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository,
    private val cartItemRepository: CartItemRepository,
    private val stripeClient: StripeClient,
) {
    @Transactional
    fun createOrder(
        memberId: Long,
        request: OrderRequest,
    ): OrderResponse {
        val cart = findCartWith(memberId)
        val order = Order.from(cart, request)
        val orderItems = snapshotOrderItems(order, cart)
        orderRepository.save(order)

        return OrderResponse(
            orderId = order.id,
            orderItems = OrderItemMapper.toOrderItemResponse(order),
            currency = order.currency
        )
    }

    private fun findCartWith(memberId: Long): Cart =
        cartRepository.findCartWithAllByMemberId(memberId)
            ?.also { require(it.items.isNotEmpty()) { "Cart is empty" } }
            ?: throw NotFoundException("Cart not found")

    private fun snapshotOrderItems(order: Order, cart: Cart): List<OrderItem> {
        require(order.orderItems.isNotEmpty()) { "Order items must have at least one item" }
        return cart.items.map { item ->
            require(item.product.name.isNotEmpty() && item.option.name.isNotEmpty()) { "names can not be empty" }

            val snapshotName = "${item.product.name}: ${item.option.name}"

            OrderItem(
                order = order,
                option = item.option,
                quantity = item.quantity,
                unitPrice = (item.product.price * 100).toLong(),
                itemName = snapshotName,
            )
        }
    }
}
