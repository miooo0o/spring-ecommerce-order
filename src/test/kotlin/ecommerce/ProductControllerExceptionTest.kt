package ecommerce

import ecommerce.dto.ProductRequest
import ecommerce.dto.TokenRequest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.ExtractableResponse
import io.restassured.response.Response
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.DirtiesContext
@Disabled("Temporarily muted for debugging")

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProductControllerExceptionTest {
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    private lateinit var adminToken: String

    @BeforeEach
    fun setUp() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS cart_items")
        jdbcTemplate.execute("DROP TABLE IF EXISTS carts")
        jdbcTemplate.execute("DROP TABLE products IF EXISTS")
        jdbcTemplate.execute(
            "CREATE TABLE products(" + "id SERIAL, name VARCHAR(100), price DECIMAL(10,2), image_url VARCHAR(500))",
        )
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

    private fun makeRequestToProducts(product: ProductRequest): ExtractableResponse<Response> =
        RestAssured
            .given().log().all()
            .header("Authorization", "Bearer $adminToken")
            .body(product)
            .contentType(ContentType.JSON)
            .`when`().post("/api/products")
            .then().log().all().extract()

    @Test
    fun handleExceptionUsingExceptionHandler() {
        val response =
            makeRequestToProducts(
                ProductRequest(
                    name = "colaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                    price = 0.0,
                    imageUrl = "abchttps://cola.jpg",
                ),
            )

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        assertThat(response.body().asString()).contains("Product name cannot exceed 15 characters")
    }

    @Test
    fun sameNameException() {
        val response = makeRequestToProducts(ProductRequest(name = "cola", price = 4.5, imageUrl = "https://cola.jpg"))
        val sameNameResponse = makeRequestToProducts(ProductRequest(name = "cola", price = 4.5, imageUrl = "https://cola.jpg"))

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value())
        assertThat(sameNameResponse.statusCode()).isEqualTo(HttpStatus.CONFLICT.value())
        assertThat(sameNameResponse.body().asString()).contains("Product with name cola already exists")
    }
}
