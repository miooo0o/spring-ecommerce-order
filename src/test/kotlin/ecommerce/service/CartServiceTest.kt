package ecommerce.service

import ecommerce.MemberTestFixture.BRUSH
import ecommerce.MemberTestFixture.PAINTING_SAD_HUMAN
import ecommerce.MemberTestFixture.createAdmin
import ecommerce.MemberTestFixture.createMina
import ecommerce.MemberTestFixture.createPaintingHappyHuman
import ecommerce.MemberTestFixture.createPaintingSadHuman
import ecommerce.MemberTestFixture.createPetra
import ecommerce.dto.CartItemRequest
import ecommerce.model.Option
import ecommerce.repository.MemberRepository
import ecommerce.repository.OptionRepository
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

    @Autowired
    private lateinit var optionRepository: OptionRepository

    @Test
    fun `adding item does not throw exception`() {
        assertDoesNotThrow {
            val member = memberRepository.save(createPetra())
            val product = productRepository.save(BRUSH)
            val option = optionRepository.save(Option(name = "test"))
            option.product = product
            val request = CartItemRequest(product.id, option.id, 1)

            val cartItem = cartService.addItem(member.id!!, request)
            cartItem.cart!!
        }
    }

    @Test
    fun `adding item returns the correct cart item`() {
        val member = memberRepository.save(createMina())
        val product = productRepository.save(createPaintingSadHuman())
        val option = optionRepository.save(Option(name = "test"))

        val request = CartItemRequest(product.id, option.id, 1)

        val cartItem = cartService.addItem(member.id!!, request)
        assertThat(cartItem.product.name).isEqualTo(PAINTING_SAD_HUMAN.name)
    }

    @Test
    fun `delete item does not throw an exception`() {
        val member = memberRepository.save(createAdmin())
        val product = productRepository.save(createPaintingHappyHuman())
        val addRequest = CartItemRequest(product.id, 1)
        val cartItem = cartService.addItem(member.id, addRequest)

        val deleteRequest = CartItemRequest(cartItem.product.id!!, 1)

        assertDoesNotThrow {
            cartService.deleteItem(member.id!!, deleteRequest)
        }
    }
}
