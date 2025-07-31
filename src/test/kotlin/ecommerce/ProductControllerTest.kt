package ecommerce

import ecommerce.dto.ProductRequest
import ecommerce.dto.TokenRequest
import ecommerce.repository.ProductRepository
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.DirtiesContext

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ProductControllerTest {
    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    private lateinit var adminToken: String

    @BeforeEach
    fun setUp() {
        productRepository = ProductRepository(jdbcTemplate)

        jdbcTemplate.execute("DROP TABLE IF EXISTS cart_items")
        jdbcTemplate.execute("DROP TABLE IF EXISTS carts")
        jdbcTemplate.execute("DROP TABLE products IF EXISTS")
        jdbcTemplate.execute(
            "CREATE TABLE products(" + "id SERIAL, name VARCHAR(100) DEFAULT '', price DECIMAL(10,2), image_url VARCHAR(500))",
        )

        val splitUpAttributes: List<Array<String>> =
            listOf(
                "cola 2 http//cola",
                "fanta 3 http//fanta",
                "coffee 4 http//coffee",
                "tea 4 http//tea",
            ).map { name -> name.split(" ").toTypedArray() }.toList()
        jdbcTemplate.batchUpdate("INSERT INTO products(name, price, image_url) VALUES (?,?,?)", splitUpAttributes)
        jdbcTemplate.update(
            "INSERT INTO members(email, name, password, role) VALUES (?,?,?,?)",
            "admin@email.com",
            "admin",
            "AdminPassword",
            "ADMIN",
        )
        adminToken = loginAsAdmin()
    }

    private fun loginAsAdmin(): String {
        val loginRequest = TokenRequest("admin@email.com", "AdminPassword")
        val loginResponse =
            RestAssured.given().log().all().body(loginRequest).contentType(ContentType.JSON).`when`()
                .post("/api/members/login").then().log().all().extract()
        val token = loginResponse.body().jsonPath().getString("token")
        return token
    }

    @Test
    fun create() {
        val response =
            RestAssured.given().log().all()
                .header("Authorization", "Bearer $adminToken")
                .body(
                    ProductRequest(
                        name = "iced latte",
                        price = 4.5,
                        imageUrl = "https://cola.jpg",
                    ),
                )
                .contentType(ContentType.JSON).`when`().post("/api/products").then().log().all().extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value())
        assertThat(response.headers().toString()).contains("/api/products/5")
    }

    @Test
    fun read() {
        val products = productRepository.findAllProducts()
        assertThat(products.size).isEqualTo(4)
    }

    @Test
    fun `update existing product`() {
        val response =
            RestAssured
                .given().log().all()
                .header("Authorization", "Bearer $adminToken")
                .body(ProductRequest(name = "fanta", price = 5.6, imageUrl = "https://fanta.jpg"))
                .contentType(ContentType.JSON).`when`().put("/api/products/1")
                .then().log().all()
                .extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
    }

    @Test
    fun delete() {
        val response =
            RestAssured
                .given().log().all()
                .header("Authorization", "Bearer $adminToken")
                .`when`().delete("/api/products/1")
                .then().log().all()
                .extract()
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value())
    }
}
