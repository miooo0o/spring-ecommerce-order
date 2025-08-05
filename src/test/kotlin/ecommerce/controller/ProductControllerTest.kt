package ecommerce.controller

import ecommerce.config.DatabaseFixture.ADMIN
import ecommerce.config.DatabaseFixture.BRUSH
import ecommerce.config.DatabaseFixture.createAcrylics
import ecommerce.config.DatabaseFixture.createAdmin
import ecommerce.config.DatabaseFixture.createBrush
import ecommerce.config.DatabaseFixture.createBrushWithOptions
import ecommerce.config.DatabaseFixture.createCanvas
import ecommerce.config.DatabaseFixture.createPalette
import ecommerce.dto.OptionRequest
import ecommerce.dto.ProductRequest
import ecommerce.dto.TokenRequest
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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerTest {
    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    private lateinit var adminToken: String

    @LocalServerPort
    private var port: Int = 0

    val baseUrl get() = "http://localhost:$port"

    @BeforeEach
    fun setUp() {
        val products = listOf(createBrush(), createCanvas(), createPalette(), createAcrylics())
        productRepository.saveAll(products)
        memberRepository.save(createAdmin())
        adminToken = loginAsAdmin()
    }

    @AfterEach
    fun tearDown() {
        productRepository.deleteAll()
        memberRepository.deleteAll()
    }

    private fun loginAsAdmin(): String {
        val loginRequest = TokenRequest(ADMIN.email, ADMIN.password)
        val loginResponse =
            RestAssured.given().log().all().baseUri(baseUrl).body(loginRequest).contentType(ContentType.JSON).`when`()
                .post("/api/members/login").then().log().all().extract()
        val token = loginResponse.body().jsonPath().getString("token")
        return token
    }

    private fun addProductRequest(product: ProductRequest): ExtractableResponse<Response> =
        RestAssured
            .given().log().all()
            .baseUri(baseUrl)
            .header("Authorization", "Bearer $adminToken")
            .body(product)
            .contentType(ContentType.JSON)
            .`when`().post("/api/products")
            .then().log().all().extract()

    @Test
    fun create() {
        val response =
            RestAssured.given().log().all()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer $adminToken")
                .body(
                    ProductRequest(
                        name = "iced latte",
                        price = 4.5,
                        imageUrl = "https://cola.jpg",
                        options = listOf(OptionRequest("option", 3)),
                    ),
                )
                .contentType(ContentType.JSON).`when`().post("/api/products").then().log().all().extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value())
        assertThat(response.headers().toString()).contains("/api/products/5")
    }

    @Test
    fun read() {
        val response =
            RestAssured
                .given().log().all()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer $adminToken")
                .contentType(ContentType.JSON).`when`().get("/api/products")
                .then().log().all()
                .extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        val products: List<Any> = response.body().jsonPath().getList("")
        assertThat(products.size).isEqualTo(4)
    }

    @Test
    fun getOptions() {
        val product = createBrushWithOptions()
        val productId = productRepository.save(product).id
        val response =
            RestAssured
                .given().log().all()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer $adminToken")
                .contentType(ContentType.JSON).`when`().get("/api/products/${productId}/options")
                .then().log().all()
                .extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        val options: List<Any> = response.body().jsonPath().getList("")
        assertThat(options.size).isEqualTo(product.options.size)
    }

    @Test
    fun `update existing product`() {
        val getProductsResponse =
            RestAssured
                .given().log().all()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer $adminToken")
                .contentType(ContentType.JSON).`when`().get("/api/products")
                .then().log().all()
                .extract()

        val productId: Long = getProductsResponse.body().jsonPath().get("[0].id")
        val response =
            RestAssured
                .given().log().all()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer $adminToken")
                .body(ProductRequest(name = BRUSH.name, price = 5.6, imageUrl = BRUSH.imageUrl))
                .contentType(ContentType.JSON).`when`().put("/api/products/$productId")
                .then().log().all()
                .extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
    }

    @Test
    fun delete() {
        val response =
            RestAssured
                .given().log().all()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer $adminToken")
                .`when`().delete("/api/products/1")
                .then().log().all()
                .extract()
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value())
    }

    @Test
    fun handleExceptionUsingExceptionHandler() {
        val response =
            addProductRequest(
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
        val response =
            addProductRequest(
                ProductRequest(name = "cola", price = 4.5, imageUrl = "https://cola.jpg", options = listOf(OptionRequest("option", 3))),
            )
        val sameNameResponse =
            addProductRequest(
                ProductRequest(name = "cola", price = 4.5, imageUrl = "https://cola.jpg", options = listOf(OptionRequest("option", 3))),
            )

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value())
        assertThat(sameNameResponse.statusCode()).isEqualTo(HttpStatus.CONFLICT.value())
        assertThat(sameNameResponse.body().asString()).contains("Product with name cola already exists")
    }
}
