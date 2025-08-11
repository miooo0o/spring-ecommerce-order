package ecommerce

import ecommerce.BasicTestFixture.ADMIN
import ecommerce.BasicTestFixture.MINA
import ecommerce.BasicTestFixture.createAcrylics
import ecommerce.BasicTestFixture.createAdmin
import ecommerce.BasicTestFixture.createBrush
import ecommerce.BasicTestFixture.createCanvas
import ecommerce.BasicTestFixture.createMina
import ecommerce.BasicTestFixture.createPalette
import ecommerce.BasicTestFixture.createPen
import ecommerce.BasicTestFixture.createPencil
import ecommerce.BasicTestFixture.createPetra
import ecommerce.dto.CartItemRequest
import ecommerce.dto.TokenRequest
import ecommerce.model.Cart
import ecommerce.model.CartItem
import ecommerce.repository.CartItemRepository
import ecommerce.repository.CartRepository
import ecommerce.repository.MemberRepository
import ecommerce.repository.ProductRepository
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.ExtractableResponse
import io.restassured.response.Response
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StatisticsE2ETest {
    @Autowired
    private lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var cartRepository: CartRepository

    @Autowired
    private lateinit var cartItemRepository: CartItemRepository

    @LocalServerPort
    private var port: Int = 0

    val baseUrl get() = "http://localhost:$port"

    private lateinit var mostRecentCartItems: List<CartItem>

    @BeforeEach
    fun setUp() {
        val fortyDaysAgo = LocalDateTime.now().minusDays(40)

        val mina = memberRepository.save(createMina())
        val petra = memberRepository.save(createPetra())
        memberRepository.save(createAdmin())

        val minasCart = cartRepository.save(Cart(mina))
        val brush = productRepository.save(createBrush())
        val palette = productRepository.save(createPalette())
        val canvas = productRepository.save(createCanvas())
        val acrylics = productRepository.save(createAcrylics())
        val pen = productRepository.save(createPen())
        val pencil = productRepository.save(createPencil())

        val cartItem1 = cartItemRepository.save(CartItem(product = brush, cart = minasCart, quantity = 7, createdAt = LocalDateTime.now()))
        val cartItem2 =
            cartItemRepository.save(
                CartItem(product = palette, cart = minasCart, quantity = 6, createdAt = LocalDateTime.now()),
            )
        val cartItem3 = cartItemRepository.save(CartItem(product = canvas, cart = minasCart, quantity = 5, createdAt = LocalDateTime.now()))
        val cartItem4 = cartItemRepository.save(CartItem(product = pen, cart = minasCart, quantity = 4, createdAt = fortyDaysAgo))
        val cartItem5 = cartItemRepository.save(CartItem(product = acrylics, cart = minasCart, quantity = 3, createdAt = fortyDaysAgo))
        val cartItem6 = cartItemRepository.save(CartItem(product = pencil, cart = minasCart, quantity = 2, createdAt = fortyDaysAgo))

        cartItem1.updatedAt = LocalDateTime.now()
        cartItem2.updatedAt = LocalDateTime.now()
        cartItem3.updatedAt = LocalDateTime.now()
        cartItem4.updatedAt = fortyDaysAgo
        cartItem5.updatedAt = fortyDaysAgo
        cartItem6.updatedAt = fortyDaysAgo

        cartItemRepository.saveAll(
            listOf(
                cartItem1,
                cartItem2,
                cartItem3,
                cartItem4,
                cartItem5,
                cartItem6,
            ),
        )

        mostRecentCartItems = listOf(cartItem1, cartItem2, cartItem3)
    }

    @AfterEach
    fun tearDown() {
        cartItemRepository.deleteAll()
        cartRepository.deleteAll()
        memberRepository.deleteAll()
        productRepository.deleteAll()
    }

    private fun loginAS(
        email: String,
        password: String,
    ): String {
        val loginRequest = TokenRequest(email, password)
        val loginResponse =
            RestAssured
                .given()
                .baseUri(baseUrl)
                .body(loginRequest)
                .contentType(ContentType.JSON)
                .`when`()
                .post("/api/members/login")
                .then().log().all()
                .extract()
        val token = loginResponse.body().jsonPath().getString("token")
        return token
    }

    private fun addProductToCart(
        cartRequest: CartItemRequest,
        token: String,
    ) {
        RestAssured
            .given()
            .baseUri(baseUrl)
            .header("Authorization", "Bearer $token")
            .body(cartRequest).contentType(ContentType.JSON)
            .`when`()
            .post("/api/cart")
    }

    private fun getStatistics(
        token: String,
        path: String,
    ): ExtractableResponse<Response> =
        RestAssured
            .given()
            .baseUri(baseUrl)
            .log().all()
            .header("Authorization", "Bearer $token")
            .get(path)
            .then().log().all()
            .extract()

    @Test
    fun `should return top 5 most added products in the past 30 days for admin`() {
        val token = loginAS(ADMIN.email, ADMIN.password)

        val stats = getStatistics(token, "/admin/statistics/top-products")

        assertThat(stats.statusCode()).isEqualTo(HttpStatus.OK.value())
        val json = stats.body().jsonPath()
        val productNames = json.getList<String>("productName")
        val topProducts =
            listOf(mostRecentCartItems[0].product.name, mostRecentCartItems[1].product.name, mostRecentCartItems[2].product.name)
        assertThat(productNames).containsExactlyInAnyOrderElementsOf(topProducts)
    }

    @Test
    fun `should return active members in the past 7 days for admin`() {
        val token = loginAS(ADMIN.email, ADMIN.password)

        val stats = getStatistics(token, "/admin/statistics/active-members")

        assertThat(stats.statusCode()).isEqualTo(HttpStatus.OK.value())
        val json = stats.body().jsonPath()
        val emails = json.getList<String>("email")
        assertThat(emails).containsExactly(MINA.email)
    }

    @ParameterizedTest
    @ValueSource(strings = ["/admin/statistics/top-products", "/admin/statistics/active-members"])
    fun `should not return statistics for user`(path: String) {
        val token = loginAS(MINA.email, MINA.password)

        val stats = getStatistics(token, path)

        assertThat(stats.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value())
    }
}
