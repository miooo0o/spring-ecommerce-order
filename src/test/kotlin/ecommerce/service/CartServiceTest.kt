package ecommerce.service

import ecommerce.config.DatabaseFixture.ADMIN
import ecommerce.config.DatabaseFixture.BRUSH
import ecommerce.config.DatabaseFixture.MINA
import ecommerce.config.DatabaseFixture.PAINTING_HAPPY_HUMAN
import ecommerce.config.DatabaseFixture.PAINTING_SAD_HUMAN
import ecommerce.config.DatabaseFixture.PETRA
import ecommerce.dto.CartItemRequest
import ecommerce.repository.MemberRepositoryJPA
import ecommerce.repository.ProductRepositoryJPA
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
    private lateinit var productRepository: ProductRepositoryJPA

    @Autowired
    private lateinit var memberRepository: MemberRepositoryJPA

    @Test
    fun `x throw`() {
        assertDoesNotThrow {
            val member = memberRepository.save(PETRA)
            val product = productRepository.save(BRUSH)
            val request = CartItemRequest(product.id!!, 1)

            val cartItem = cartService.addItem(member.id!!, request)
            cartItem.cart!!
        }
    }

    @Test
    fun `return if`() {
        val member = memberRepository.save(MINA)
        val product = productRepository.save(PAINTING_SAD_HUMAN)
        val request = CartItemRequest(product.id!!, 1)

        val cartItem = cartService.addItem(member.id!!, request)
        assertThat(cartItem.product.name).isEqualTo(PAINTING_SAD_HUMAN.name)
    }

    @Test
    fun `delete item`() {
        val member = memberRepository.save(ADMIN)
        val product = productRepository.save(PAINTING_HAPPY_HUMAN)
        val addRequest = CartItemRequest(product.id!!, 1)
        val cartItem = cartService.addItem(member.id!!, addRequest)

        val deleteRequest = CartItemRequest(cartItem.product.id!!, 1)

        assertDoesNotThrow {
            cartService.deleteItem(member.id!!, deleteRequest)
        }
    }
}
