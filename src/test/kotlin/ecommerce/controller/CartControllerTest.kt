package ecommerce.controller

import ecommerce.annotation.LoginMemberArgumentResolver
import ecommerce.config.AuthInterceptor
import ecommerce.config.WebMvcConfiguration
import ecommerce.dto.CartItemRequest
import ecommerce.dto.RegisteredMember
import ecommerce.dto.Role
import ecommerce.infrastructure.AuthorizationExtractor
import ecommerce.model.Cart
import ecommerce.model.CartItem
import ecommerce.model.Member
import ecommerce.model.Product
import ecommerce.service.AuthService
import ecommerce.service.CartService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.LocalDateTime

@TestConfiguration
class TestWebConfig : WebMvcConfigurer {
    @Bean
    fun loginMemberArgumentResolver(): LoginMemberArgumentResolver {
        return mock(LoginMemberArgumentResolver::class.java)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(loginMemberArgumentResolver())
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
    }
}

@WebMvcTest(
    controllers = [CartController::class],
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = [WebMvcConfiguration::class],
        ),
    ],
)
@Import(TestWebConfig::class)
class CartControllerTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
    ) {
        @MockitoBean
        private lateinit var authorizationExtractor: AuthorizationExtractor

        @MockitoBean
        private lateinit var authInterceptor: AuthInterceptor

        @MockitoBean
        private lateinit var cartService: CartService

        @MockitoBean
        private lateinit var authService: AuthService

        @Autowired
        private lateinit var loginMemberArgumentResolver: LoginMemberArgumentResolver

        @BeforeEach
        fun setup() {
            val mockCart = mock(Cart::class.java)
            whenever(loginMemberArgumentResolver.supportsParameter(any())).thenReturn(true)
            whenever(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(
                    RegisteredMember(
                        id = 1L,
                        email = "guri@email.com",
                        role = Role.USER,
                    ),
                )
        }

        @Test
        fun `should return cart items as JSON`() {
            val memberGuri =
                Member(
                    id = 1L,
                    name = "Guri",
                    email = "guri@email.com",
                    password = "very_cute_dog",
                    role = Role.USER.name,
                )

            val mockCartItem =
                CartItem(
                    product = Product(101L, "Who Hate Test", 9999.99, "https://example.com/who_hate_test.jpg"),
                    cart = Cart(member = memberGuri),
                    quantity = 2,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now(),
                    id = 1L,
                )
            val mockCart =
                Cart(id = 1L, member = memberGuri).apply {
                    items.add(mockCartItem)
                }

            whenever(cartService.findCart(memberGuri.id!!)).thenReturn(mockCart)

            mockMvc.get("/api/cart") {
                requestAttr("email", "guri@email.com")
            }
                .andExpect { status { isOk() } }
            jsonPath("$.productId").value(101)
            jsonPath("$.quantity").value(2)
        }

        @Test
        fun `should add item to cart`() {
            val memberGuri =
                Member(
                    id = 1L,
                    name = "Guri",
                    email = "guri@email.com",
                    password = "very_cute_dog",
                    role = Role.USER.name,
                )

            val registeredMember =
                RegisteredMember(
                    id = 1L,
                    email = "guri@email.com",
                    role = Role.USER,
                )

            val product = Product(102L, "Lonely Dog Walk", 1000.0, "https://dog-walking-alone-not-funny-sometime.com")
            val request = CartItemRequest(productId = product.id!!, quantity = 2)
            val cart =
                Cart(
                    id = 1L,
                    member = memberGuri,
                )
            val mockCartItem = CartItem(product = product, cart = cart, quantity = 2, id = 1L)

            whenever(cartService.addItem(registeredMember.id, request)).thenReturn(mockCartItem)
            whenever(cartService.addItem(eq(1L), any())).thenReturn(mockCartItem)

            mockMvc.post("/api/cart") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"productId":102,"quantity":2}"""
            }
                .andExpect {
                    status { isCreated() }
                    jsonPath("$.productId").value(102)
                    jsonPath("$.quantity").value(2)
                }
        }

        @Test
        fun `should delete item from cart`() {
            val member = RegisteredMember(id = 1L, email = "guri@email.com", role = Role.USER)
            val request = CartItemRequest(productId = 101L, quantity = 1)

            doNothing().whenever(cartService).deleteItem(member.id, request)

            mockMvc.delete("/api/cart") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"productId":101,"quantity":1}"""
            }
                .andExpect {
                    status { isNoContent() }
                }
        }
    }
