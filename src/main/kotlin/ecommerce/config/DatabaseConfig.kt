package ecommerce.config

import ecommerce.dto.Role
import ecommerce.model.Member
import ecommerce.model.Product
import ecommerce.repository.CartRepositoryJPA
import ecommerce.repository.MemberRepositoryJPA
import ecommerce.repository.ProductRepositoryJPA
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DatabaseConfig(
    private val memberRepository: MemberRepositoryJPA,
    private val productRepository: ProductRepositoryJPA,
    private val cartRepository: CartRepositoryJPA,
//    private val cartItemRepo: CartItemRepositoryJPA,
) {
    @Bean
    fun databaseInit(): CommandLineRunner =
        CommandLineRunner {
            val products = listOf(
                BRUSH,
                CANVAS,
                PALETTE,
                ACRYLICS
            )
            productRepository.saveAll(products)

            val members = listOf(
                MINA,
                PETRA,
                ADMIN
            )
            memberRepository.saveAll(members)
        }

    companion object {
        val MINA = Member(
            email = "mina@mail.com",
            name = "Mina Kim",
            password = "ILoveMyDog!",
            role = Role.USER.name,
        )
        val PETRA = Member(
            email = "petra@mail.com",
            name = "Petra Bencze",
            password = "MyPasswordIsLong123",
            role = Role.USER.name,
        )

        val ADMIN = Member(
            email = "admin@mail.com",
            name = "Boss",
            password = "IAmAdmin!",
            role = Role.ADMIN.name,
        )

        val BRUSH = Product(
            name = "Brush",
            price = 5.99,
            imageUrl = "https://example.com/images/brush.jpg"
        )

        val CANVAS = Product(
            name = "Canvas",
            price = 8.50,
            imageUrl = "https://example.com/images/canvas.jpg"
        )

        val PALETTE = Product(
            name = "Palette",
            price = 6.25,
            imageUrl = "https://example.com/images/palette.jpg"
        )

        val ACRYLICS = Product(
            name = "Acrylics",
            price = 15.00,
            imageUrl = "https://example.com/images/acrylics.jpg"
        )
    }
}
