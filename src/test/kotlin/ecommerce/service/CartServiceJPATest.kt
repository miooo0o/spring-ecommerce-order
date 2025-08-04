package ecommerce.service

import ecommerce.config.DatabaseFixture.PAINTING_SAD_HUMAN
import ecommerce.config.DatabaseFixture.PETRA
import ecommerce.dto.CartItemRequest
import ecommerce.repository.MemberRepositoryJPA
import ecommerce.repository.ProductRepositoryJPA
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import

@DataJpaTest
@Import(CartServiceJPA::class)
class CartServiceJPATest {
    @Autowired
    private lateinit var cartService: CartServiceJPA

    @Autowired
    private lateinit var productRepository: ProductRepositoryJPA

    @Autowired
    private lateinit var memberRepository: MemberRepositoryJPA

    @Test
    fun addItem() {
        val member = memberRepository.save(PETRA)
        val product = productRepository.save(PAINTING_SAD_HUMAN)
        val request = CartItemRequest(product.id!!, 1)

        val cartItem = cartService.addItem(member.id!!, request)
//        assertThat(cartItem.quantity).isEqualTo(request.quantity)
    }
}
