package ecommerce

import ecommerce.dto.TokenRequest
import ecommerce.repository.MemberRepositoryJPA
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.ExtractableResponse
import io.restassured.response.Response
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.DirtiesContext

// @Disabled("Temporarily muted for debugging")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TokenLoginControllerTest {
    @Autowired
    private lateinit var productRepository: MemberRepositoryJPA

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    companion object {
        @JvmStatic
        fun invalidRegisterRequests(): List<TokenRequest> =
            listOf(
                // invalid mail
                TokenRequest("@", "abcdef1234"),
                // invalid password
                TokenRequest("a@mail.com", "abcd"),
                // invalid mail
                TokenRequest("", "abcdef1234"),
                // invalid password
                TokenRequest("a@mail.com", ""),
            )
    }

    @BeforeEach
    fun setUp() {
//        productRepository = ProductRepositoryJDBC(jdbcTemplate)

        jdbcTemplate.execute("DROP TABLE IF EXISTS cart_items")
        jdbcTemplate.execute("DROP TABLE IF EXISTS carts")
        jdbcTemplate.execute("DROP TABLE members IF EXISTS")
        jdbcTemplate.execute(
            "CREATE TABLE members(" + " id SERIAL, email VARCHAR(20) " +
                "UNIQUE, name VARCHAR(100) DEFAULT '', password VARCHAR(50), role VARCHAR(10))",
        )

        val splitUpAttributes: List<Array<String>> =
            listOf(
                "sandra@email.com MyPassword USER",
                "simon@email.com Hello1234 USER",
                "sara@email.com 1234567! USER",
                "sam@email.com abcdefghijkl USER",
            ).map { name -> name.split(" ").toTypedArray() }.toList()
        jdbcTemplate.batchUpdate("INSERT INTO members(email, password, role) VALUES (?,?,?)", splitUpAttributes)
    }

    private fun loginRequest(body: TokenRequest): ExtractableResponse<Response> =
        RestAssured
            .given().log().all()
            .body(body).contentType(ContentType.JSON)
            .`when`()
            .post("/api/members/login")
            .then().log().all()
            .extract()

    private fun registerRequest(body: TokenRequest): ExtractableResponse<Response> =
        RestAssured
            .given().log().all()
            .body(body).contentType(ContentType.JSON)
            .`when`()
            .post("/api/members/register")
            .then().log().all()
            .extract()

    @Test
    fun `test registering valid member`() {
        val body = TokenRequest(email = "newmember@gmail.com", password = "abcdef1234")
        val response = registerRequest(body)
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value())
    }

    @Test
    fun `test registering already existent member`() {
        val body = TokenRequest("sandra@email.com", "MyPassword")
        val response = registerRequest(body)
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value())
    }

    @ParameterizedTest
    @MethodSource("invalidRegisterRequests")
    fun `test registering invalid members`(body: TokenRequest) {
        val response = registerRequest(body)
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun `test valid logins`() {
        val body = TokenRequest(email = "simon@email.com", password = "Hello1234")
        val response = loginRequest(body)
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
    }

    @Test
    fun `test login with non-registered member`() {
        val body = TokenRequest(email = "member@email.com", password = "MyPassword#")
        val response = loginRequest(body)
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value())
        assertThat(response.body().asString()).contains("No account with email exists")
    }

    @Test
    fun `test login with incorrect password`() {
        val body = TokenRequest(email = "sam@email.com", password = "MyPassword#")
        val response = loginRequest(body)
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value())
    }

    @Test
    fun `test request with valid token`() {
        val body = TokenRequest(email = "sam@email.com", password = "abcdefghijkl")
        val loginResponse = loginRequest(body)

        assertThat(loginResponse.statusCode()).isEqualTo(HttpStatus.OK.value())

        val token = loginResponse.body().jsonPath().getString("token")
        val tokenResponse =
            RestAssured.given().log().all()
                .header("Authorization", "Bearer $token")
                .`when`()
                .get("/api/members/me/token")
                .then().log().all()
                .extract()

        assertThat(tokenResponse.statusCode()).isEqualTo(HttpStatus.OK.value())
    }

    @Test
    fun `test request with invalid token`() {
        val token = "ndwndwoljdwpfkwkdsq.DNlwfk3wld'wamclwfjkepojfo3jf"
        val response =
            RestAssured.given().log().all()
                .header("Authorization", "Bearer $token")
                .`when`()
                .get("/api/members/me/token")
                .then().log().all()
                .extract()

        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value())
    }

    @Test
    fun `test request without 'Authorization' header`() {
        val body = TokenRequest(email = "sam@email.com", password = "abcdefghijkl")
        val loginResponse = loginRequest(body)

        assertThat(loginResponse.statusCode()).isEqualTo(HttpStatus.OK.value())

        val token = loginResponse.body().jsonPath().getString("token")
        val tokenResponse =
            RestAssured.given().log().all()
                .header("Location", "Bearer $token")
                .`when`()
                .get("/api/members/me/token")
                .then().log().all()
                .extract()

        assertThat(tokenResponse.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value())
    }
}
