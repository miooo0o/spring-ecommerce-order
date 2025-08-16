package ecommerce.service

import ecommerce.client.StripeClient
import ecommerce.dto.OrderRequest
import ecommerce.repository.CartRepository
import ecommerce.repository.OrderRepository
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository,
    private val stripeClient: StripeClient,
) {
    fun placeOrder(
        memberId: Long,
        request: OrderRequest,
    ) {
    }
}
