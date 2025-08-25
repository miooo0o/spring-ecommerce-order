package ecommerce.service

import ecommerce.client.StripeClient
import ecommerce.dto.CheckoutRequest
import ecommerce.dto.CheckoutResponse
import ecommerce.dto.RegisteredMember
import ecommerce.model.Order
import ecommerce.model.Payment
import ecommerce.repository.PaymentRepository
import jakarta.transaction.Transactional
import org.apache.coyote.BadRequestException
import org.springframework.stereotype.Service

@Service
class CheckoutService(
    private val stripeClient: StripeClient,
    private val orderService: OrderService,
    private val paymentRepository: PaymentRepository,
    private val cartService: CartService,
) {
    @Transactional
    fun checkout(
        member: RegisteredMember,
        request: CheckoutRequest,
    ): CheckoutResponse {
        val order = processCheckout(member, request)
        val payment = executePayment(request)
        cartService.clearCart(member.id)

        return CheckoutResponse(
            orderId = order.id,
            paymentStatus = payment.paymentIntentStatus,
        )
    }

    fun executePayment(request: CheckoutRequest): Payment {
        val paymentIntent = stripeClient.makePayment(request)

        val payment =
            Payment(
                amount = paymentIntent.amount,
                paymentMethod = paymentIntent.paymentMethod,
                paymentIntentId = paymentIntent.id,
                clientSecret = paymentIntent.clientSecret,
                paymentIntentStatus = paymentIntent.status,
            )
        return paymentRepository.save(payment)
    }

    fun processCheckout(
        member: RegisteredMember,
        request: CheckoutRequest,
    ): Order {
        val order = orderService.createOrder(member.id)
        if (request.amount != order.totalMinor) throw BadRequestException("Invalid amount")

        order.items.forEach { it.option.decreaseStock(it.quantity) }
        return orderService.save(order)
    }
}
