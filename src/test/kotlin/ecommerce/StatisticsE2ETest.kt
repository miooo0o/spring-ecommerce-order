package ecommerce

import ecommerce.config.DatabaseFixture.ADMIN
import ecommerce.config.DatabaseFixture.MINA
import ecommerce.config.DatabaseFixture.createAcrylics
import ecommerce.config.DatabaseFixture.createBrush
import ecommerce.config.DatabaseFixture.createCanvas
import ecommerce.config.DatabaseFixture.createMina
import ecommerce.config.DatabaseFixture.createPalette
import ecommerce.config.DatabaseFixture.createPen
import ecommerce.config.DatabaseFixture.createPencil
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus

@Disabled("Temporarily muted for debugging")
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

    @BeforeEach
    fun setUp() {
        val mina = memberRepository.save(createMina())
        productRepository.saveAll(listOf(createBrush(), createPalette(), createCanvas(), createAcrylics(), createPen(), createPencil()))
        val minasCart = cartRepository.save(Cart(mina))
        val cartItem1 = CartItem()
        addItemsToCartWithDelayForUser(
            loginAS(MINA.email, MINA.password),
        )
    }

    private fun addItemsToCartWithDelayForUser(userToken: String) {
        val productIds = (1L..7L).toList()
        productIds.take(2).forEach {
            addProductToCart(CartItemRequest(it, 1), userToken)
        }
        Thread.sleep(2000)
        productIds.drop(2).forEach {
            addProductToCart(CartItemRequest(it, 1), userToken)
        }
    }

//    private fun createProductsTable() {
//        jdbcTemplate.execute(
//            "CREATE TABLE products(" + "id SERIAL, name VARCHAR(100), price DECIMAL(10,2), image_url VARCHAR(500))",
//        )
//
//        val splitUpAttributes: List<Array<String>> =
//            listOf(
//                "cola 2 http//cola",
//                "fanta 3 http//fanta",
//                "coffee 4 http//coffee",
//                "tea 4 http//coffee",
//                "milk 2.3 http//coffee",
//                "water 1.5 http//coffee",
//                "soda 2.0 http//coffee",
//            ).map { name -> name.split(" ").toTypedArray() }.toList()
//        jdbcTemplate.batchUpdate("INSERT INTO products(name, price, image_url) VALUES (?,?,?)", splitUpAttributes)
//    }

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
        assertThat(productNames).containsExactly("soda", "water", "milk", "tea", "coffee")
    }

    @Test
    fun `should return active members in the past 7 days for admin`() {
        val token = loginAS(ADMIN.email, ADMIN.password)

        val stats = getStatistics(token, "/admin/statistics/active-members")

        assertThat(stats.statusCode()).isEqualTo(HttpStatus.OK.value())
        val json = stats.body().jsonPath()
        val emails = json.getList<String>("email")
        assertThat(emails).containsExactly(USER1_MAIL)
    }

    @ParameterizedTest
    @ValueSource(strings = ["/admin/statistics/top-products", "/admin/statistics/active-members"])
    fun `should not return statistics for user`(path: String) {
        val token = loginAS(MINA.email, MINA.password)

        val stats = getStatistics(token, path)

        assertThat(stats.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value())
    }
}
