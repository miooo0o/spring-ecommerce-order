package ecommerce

import ecommerce.dto.Role
import ecommerce.model.Member
import ecommerce.model.Product

object TestFixture {
    val PETRA_USER =
        Member(
            email = "letMeGo@deliveryhero.com",
            name = "Petra",
            password = "pizza",
            role = Role.USER.name,
        )

    val PAINTING_SAD_HUMAN =
        Product(
            name = "Sad human and Cute dog",
            price = 3000.00,
            imageUrl = "https://picsum.d-o-go",
        )

    val PAINTING_HAPPY_HUMAN =
        Product(
            name = "Happy human and Cute dog",
            price = 19000.00,
            imageUrl = "https://picsum.d-o-go",
        )
}
