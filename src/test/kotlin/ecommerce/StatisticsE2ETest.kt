package ecommerce

import ecommerce.dto.CartItemRequest
import ecommerce.dto.TokenRequest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.ExtractableResponse
import io.restassured.response.Response
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.DirtiesContext

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class StatisticsE2ETest {
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @BeforeEach
    fun setUp() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS cart_items")
        jdbcTemplate.execute("DROP TABLE IF EXISTS carts")
        jdbcTemplate.execute("DROP TABLE IF EXISTS products")
        jdbcTemplate.execute("DROP TABLE members IF EXISTS")

        createMembersTable()
        createProductsTable()
        createCartTable()
        createCartItemsTable()

        addItemsToCartWithDelayForUser(
            loginAS(USER1_MAIL, USER1_PASSWORD),
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

    private fun createCartTable() {
        jdbcTemplate.execute(
            "CREATE TABLE carts(" + " cart_id SERIAL, user_id INT UNIQUE)",
        )
    }

    private fun createCartItemsTable() {
        jdbcTemplate.execute(
            """
            CREATE TABLE cart_items (
                cart_id INT,
                product_id INT,
                quantity INT DEFAULT 1,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                PRIMARY KEY (cart_id, product_id)
            )
            """.trimIndent(),
        )
    }

    private fun createMembersTable() {
        jdbcTemplate.execute(
            "CREATE TABLE members(" + " id SERIAL, email VARCHAR(20) " +
                "UNIQUE, name VARCHAR(100) DEFAULT '', password VARCHAR(50), role VARCHAR(10))",
        )

        val splitUpAttributes: List<Array<String>> =
            listOf(
                "$USER1_MAIL Anna $USER1_PASSWORD $USER",
                "$USER2_MAIL Anna $USER2_PASSWORD $USER",
                "$ADMIN_MAIL Anna $ADMIN_PASSWORD $ADMIN",
            ).map { name -> name.split(" ").toTypedArray() }.toList()
        jdbcTemplate.batchUpdate("INSERT INTO members(email, name, password, role) VALUES (?,?,?,?)", splitUpAttributes)
    }

    private fun createProductsTable() {
        jdbcTemplate.execute(
            "CREATE TABLE products(" + "id SERIAL, name VARCHAR(100), price DECIMAL(10,2), image_url VARCHAR(500))",
        )

        val splitUpAttributes: List<Array<String>> =
            listOf(
                "cola 2 http//cola",
                "fanta 3 http//fanta",
                "coffee 4 http//coffee",
                "tea 4 http//coffee",
                "milk 2.3 http//coffee",
                "water 1.5 http//coffee",
                "soda 2.0 http//coffee",
            ).map { name -> name.split(" ").toTypedArray() }.toList()
        jdbcTemplate.batchUpdate("INSERT INTO products(name, price, image_url) VALUES (?,?,?)", splitUpAttributes)
    }

    private fun loginAS(
        email: String,
        password: String,
    ): String {
        val loginRequest = TokenRequest(email, password)
        val loginResponse =
            RestAssured.given().log().all().body(loginRequest).contentType(ContentType.JSON).`when`()
                .post("/api/members/login").then().log().all().extract()
        val token = loginResponse.body().jsonPath().getString("token")
        return token
    }

    private fun addProductToCart(
        cartRequest: CartItemRequest,
        token: String,
    ) {
        RestAssured.given()
            .header("Authorization", "Bearer $token")
            .body(cartRequest).contentType(ContentType.JSON)
            .`when`()
            .post("/api/cart")
            .then()
            .extract()
    }

    private fun getStatistics(
        token: String,
        path: String,
    ): ExtractableResponse<Response> =
        RestAssured.given()
            .log().all()
            .header("Authorization", "Bearer $token")
            .get(path)
            .then().log().all()
            .extract()

    @Test
    fun `should return top 5 most added products in the past 30 days for admin`() {
        val token = loginAS(ADMIN_MAIL, ADMIN_PASSWORD)

        val stats = getStatistics(token, "/admin/statistics/top-products")

        assertThat(stats.statusCode()).isEqualTo(HttpStatus.OK.value())
        val json = stats.body().jsonPath()
        val productNames = json.getList<String>("productName")
        assertThat(productNames).containsExactly("soda", "water", "milk", "tea", "coffee")
    }

    @Test
    fun `should return active members in the past 7 days for admin`() {
        val token = loginAS("admin@email.com", "AdminPassword")

        val stats = getStatistics(token, "/admin/statistics/active-members")

        assertThat(stats.statusCode()).isEqualTo(HttpStatus.OK.value())
        val json = stats.body().jsonPath()
        val emails = json.getList<String>("email")
        assertThat(emails).containsExactly(USER1_MAIL)
    }

    @ParameterizedTest
    @ValueSource(strings = ["/admin/statistics/top-products", "/admin/statistics/active-members"])
    fun `should not return statistics for user`(path: String) {
        val token = loginAS(USER1_MAIL, USER1_PASSWORD)

        val stats = getStatistics(token, path)

        assertThat(stats.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value())
    }

    companion object {
        private const val USER1_MAIL = "user@email.com"
        private const val USER1_PASSWORD = "User1Password"
        private const val USER2_MAIL = "user2@email.com"
        private const val USER2_PASSWORD = "User2Password"
        private const val ADMIN_MAIL = "admin@email.com"
        private const val ADMIN_PASSWORD = "AdminPassword"
        private const val ADMIN = "ADMIN"
        private const val USER = "USER"
    }
}
