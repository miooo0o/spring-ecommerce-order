package ecommerce.service

import ecommerce.dto.OrderRequest
import ecommerce.model.Order
import ecommerce.model.mapper.toPaymentRequest
import ecommerce.repository.OrderRepository
import ecommerce.service.validator.OrderValidator
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Transactional
@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val paymentService: PaymentService,
    private val cartService: CartService,
    private val orderValidator: OrderValidator,
) {
    fun processOrder(
        memberId: Long,
        request: OrderRequest,
    ) {
        val order = placeOrder(memberId, request)
        orderValidator.validateOrderOrThrow(order)
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
}
