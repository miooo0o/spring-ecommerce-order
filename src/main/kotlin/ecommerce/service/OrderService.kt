package ecommerce.service

import ecommerce.client.StripeClient
import ecommerce.dto.OrderRequest
import ecommerce.dto.OrderResponse
import ecommerce.exception.NotFoundException
import ecommerce.model.Cart
import ecommerce.model.Currency
import ecommerce.model.Order
import ecommerce.model.mapper.OrderItemMapper
import ecommerce.repository.CartItemRepository
import ecommerce.repository.CartRepository
import ecommerce.repository.OrderRepository
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
        val order = Order(
            member = cart.member,
            currency = Currency.fromCode(request.currency.uppercase()),
        )
        order.snapshotOrderItemFrom(cart)

        orderRepository.save(order)

        return OrderResponse(
            orderId = order.id,
            orderItems = OrderItemMapper.toOrderItemResponse(order),
            currency = order.currency.name
        )
    }

    private fun findCartWith(memberId: Long): Cart =
        cartRepository.findCartWithAllByMemberId(memberId)
            ?.also { require(it.items.isNotEmpty()) { "Cart is empty" } }
            ?: throw NotFoundException("Cart not found")
}

