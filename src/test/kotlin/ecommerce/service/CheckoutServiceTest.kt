package ecommerce.service

import ecommerce.client.StripeClient
import ecommerce.dto.CheckoutRequest
import ecommerce.dto.PaymentIntent
import ecommerce.dto.RegisteredMember
import ecommerce.dto.Role
import ecommerce.model.Member
import ecommerce.model.Option
import ecommerce.model.Order
import ecommerce.model.OrderItem
import ecommerce.model.Payment
import ecommerce.repository.MemberRepository
import ecommerce.repository.PaymentRepository
import org.apache.coyote.BadRequestException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean

@SpringBootTest
class CheckoutServiceTest {
    @MockitoBean
    private lateinit var stripeClient: StripeClient

    @MockitoBean
    private lateinit var orderService: OrderService

    @MockitoBean
    private lateinit var paymentRepository: PaymentRepository

    @Autowired
    private lateinit var checkoutService: CheckoutService

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Test
    fun `successfully checkout `() {
        val memberDto = RegisteredMember(1L, "test@email.com", Role.USER)
        val request = CheckoutRequest(1L, 1000L, "eur", "pm_card_visa")

        val member =
            memberRepository.save(
                Member(
                    email = "test@email.com",
                    name = "test",
                    password = "guri_I_hate_test!",
                    role = Role.USER.name,
                ),
            )

        val mockOrder =
            mock<Order> {
                on { id } doReturn 1L
                on { totalMinor } doReturn 1000L
                on { items } doReturn mutableListOf()
            }
        val paymentIntent = PaymentIntent("pi_guri_cute_id", 1000L, "succeeded", "eur", "pm_card_visa", "secret", 123L)
        val payment = Payment(1000L, "eur", "pm_card_visa", "pi_guri_cute_id", "secret", "succeeded")

        whenever(orderService.createOrder(memberDto.id)).thenReturn(mockOrder)
        whenever(orderService.save(mockOrder)).thenReturn(mockOrder)
        whenever(stripeClient.makePayment(request)).thenReturn(paymentIntent)
        whenever(paymentRepository.save(any<Payment>())).thenReturn(payment)

        val result = checkoutService.checkout(memberDto, request)

        assertThat(result.orderId).isEqualTo(1L)
        assertThat(result.paymentStatus).isEqualTo("succeeded")
    }

    @Test
    fun `failure if order amount and request amount are different`() {
        val member = RegisteredMember(1L, "test@email.com", Role.USER)
        val request = CheckoutRequest(1L, 2000L, "eur", "pm_card_visa")

        val mockOrder =
            mock<Order> {
                on { totalMinor } doReturn 1000L
            }
        whenever(orderService.createOrder(member.id)).thenReturn(mockOrder)

        assertThatThrownBy {
            checkoutService.checkout(member, request)
        }.isInstanceOf(BadRequestException::class.java)
    }

    @Test
    fun `successfully executePayment`() {
        val request = CheckoutRequest(1L, 1500L, "eur", "pm_card_mastercard")
        val paymentIntent =
            PaymentIntent(
                "pi_guri_cute_id",
                1500L,
                "requires_confirmation",
                "eur",
                "pm_card_mastercard",
                "secret",
                16648303L,
            )
        val savedPayment =
            Payment(1500L, "eur", "pm_card_mastercard", "pi_guri_cute_id", "secret", "requires_confirmation", id = 1L)

        whenever(stripeClient.makePayment(request)).thenReturn(paymentIntent)
        whenever(paymentRepository.save(any<Payment>())).thenReturn(savedPayment)

        val result = checkoutService.executePayment(request)

        assertThat(result.amount).isEqualTo(1500L)
        assertThat(result.paymentMethod).isEqualTo("pm_card_mastercard")
        assertThat(result.paymentIntentId).isEqualTo("pi_guri_cute_id")
    }

    @Test
    fun `processCheckout decrease stock`() {
        val member = RegisteredMember(1L, "test@email.com", Role.USER)
        val request = CheckoutRequest(1L, 1000L, "eur", "pm_card_visa")

        val mockOption =
            mock<Option> {
                on { decreaseStock(2) } doReturn mock()
            }
        val mockOrderItem =
            mock<OrderItem> {
                on { option } doReturn mockOption
                on { quantity } doReturn 2
            }
        val mockOrder =
            mock<Order> {
                on { id } doReturn 1L
                on { totalMinor } doReturn 1000L
                on { items } doReturn mutableListOf(mockOrderItem)
            }

        whenever(orderService.createOrder(member.id)).thenReturn(mockOrder)
        whenever(orderService.save(mockOrder)).thenReturn(mockOrder)

        val result = checkoutService.processCheckout(member, request)

        assertThat(result).isEqualTo(mockOrder)
        verify(mockOption).decreaseStock(2)
    }
}
