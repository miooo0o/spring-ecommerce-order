package ecommerce.service

import ecommerce.client.StripeClient
import ecommerce.dto.CheckoutRequest
import ecommerce.dto.CheckoutResponse
import ecommerce.dto.RegisteredMember
import ecommerce.model.Payment
import ecommerce.repository.PaymentRepository
import jakarta.transaction.Transactional
import org.apache.coyote.BadRequestException
import org.springframework.stereotype.Service

@Service
class PaymentService(
    private val stripeClient: StripeClient,
    private val orderService: OrderService,
    private val paymentRepository: PaymentRepository,
) {
    @Transactional
    fun checkout(
        member: RegisteredMember,
        request: CheckoutRequest,
    ): CheckoutResponse {
        val order = orderService.createOrder(member.id)
        if (request.amount != order.totalMinor) throw BadRequestException("Invalid amount")

        order.items.forEach { it.option.decreaseStock(it.quantity) }
        val payment = executePayment(request)

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
                currency = "eur",
                paymentMethod = paymentIntent.paymentMethod,
                paymentIntentId = paymentIntent.id,
                clientSecret = paymentIntent.clientSecret,
                paymentIntentStatus = paymentIntent.status,
            )
        return paymentRepository.save(payment)
    }
}
