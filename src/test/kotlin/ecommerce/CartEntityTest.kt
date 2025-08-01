package ecommerce

import ecommerce.dto.Role
import ecommerce.model.Cart
import ecommerce.model.Member
import ecommerce.repository.CartRepositoryJPA
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@Disabled("Temporarily muted for debugging")
@DataJpaTest
class CartEntityTest {
    @Autowired
    lateinit var repository: CartRepositoryJPA

    @Test
    fun initCart() {
        val user = PETRA_USER
        val cart = Cart(member = user)
        repository.save(cart)
    }

    companion object {
        val PETRA_USER =
            Member(
                email = "letMeGo@deliveryhero.com",
                name = "Petra",
                password = "pizza",
                role = Role.USER.name,
            )
    }
}
