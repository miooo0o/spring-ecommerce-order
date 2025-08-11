package ecommerce

import ecommerce.BasicTestFixture.PETRA
import ecommerce.model.Cart
import ecommerce.repository.CartRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class CartEntityTest {
    @Autowired
    lateinit var repository: CartRepository

    @Test
    fun initCart() {
        val user = PETRA
        val cart = Cart(member = user)
        repository.save(cart)
    }
}
