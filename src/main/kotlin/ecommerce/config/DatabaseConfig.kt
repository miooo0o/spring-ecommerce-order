package ecommerce.config

import ecommerce.config.DatabaseFixture.ACRYLICS
import ecommerce.config.DatabaseFixture.ADMIN
import ecommerce.config.DatabaseFixture.BRUSH
import ecommerce.config.DatabaseFixture.CANVAS
import ecommerce.config.DatabaseFixture.MINA
import ecommerce.config.DatabaseFixture.PALETTE
import ecommerce.config.DatabaseFixture.PETRA
import ecommerce.repository.CartRepository
import ecommerce.repository.MemberRepositoryJPA
import ecommerce.repository.ProductRepositoryJPA
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DatabaseConfig(
    private val memberRepository: MemberRepositoryJPA,
    private val productRepository: ProductRepositoryJPA,
    private val cartRepository: CartRepository,
//    private val cartItemRepo: CartItemRepositoryJPA,
) {
    @Bean
    fun databaseInit(): CommandLineRunner =
        CommandLineRunner {
            val products =
                listOf(
                    BRUSH,
                    CANVAS,
                    PALETTE,
                    ACRYLICS,
                )
            productRepository.saveAll(products)

            val members = listOf(MINA, PETRA, ADMIN)
            memberRepository.saveAll(members)
        }
}
