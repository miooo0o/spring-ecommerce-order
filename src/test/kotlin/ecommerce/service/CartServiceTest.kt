package ecommerce.service

import ecommerce.config.DatabaseFixture.BRUSH
import ecommerce.config.DatabaseFixture.PAINTING_SAD_HUMAN
import ecommerce.config.DatabaseFixture.createAdmin
import ecommerce.config.DatabaseFixture.createMina
import ecommerce.config.DatabaseFixture.createPaintingHappyHuman
import ecommerce.config.DatabaseFixture.createPaintingSadHuman
import ecommerce.config.DatabaseFixture.createPetra
import ecommerce.dto.CartItemRequest
import ecommerce.repository.MemberRepository
import ecommerce.repository.ProductRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import

@DataJpaTest
@Import(CartService::class)
class CartServiceTest {
    @Autowired
    private lateinit var cartService: CartService

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Test
    fun `x throw`() {
        assertDoesNotThrow {
            val member = memberRepository.save(createPetra())
            val product = productRepository.save(BRUSH)
            val request = CartItemRequest(product.id!!, 1)

            val cartItem = cartService.addItem(member.id!!, request)
            cartItem.cart!!
        }
    }

    @Test
    fun `return if`() {
        val member = memberRepository.save(createMina())
        val product = productRepository.save(createPaintingSadHuman())
        val request = CartItemRequest(product.id!!, 1)

        val cartItem = cartService.addItem(member.id!!, request)
        assertThat(cartItem.product.name).isEqualTo(PAINTING_SAD_HUMAN.name)
    }

    @Test
    fun `delete item`() {
        val member = memberRepository.save(createAdmin())
        val product = productRepository.save(createPaintingHappyHuman())
        val addRequest = CartItemRequest(product.id!!, 1)
        val cartItem = cartService.addItem(member.id!!, addRequest)

        val deleteRequest = CartItemRequest(cartItem.product.id!!, 1)

        assertDoesNotThrow {
            cartService.deleteItem(member.id!!, deleteRequest)
        }
    }
}
