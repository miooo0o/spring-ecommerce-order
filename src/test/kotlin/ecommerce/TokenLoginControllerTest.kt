package ecommerce

import ecommerce.config.DatabaseFixture.JIN
import ecommerce.config.DatabaseFixture.MINA
import ecommerce.config.DatabaseFixture.PETRA
import ecommerce.config.DatabaseFixture.createJin
import ecommerce.config.DatabaseFixture.createMina
import ecommerce.config.DatabaseFixture.createPetra
import ecommerce.dto.TokenRequest
import ecommerce.repository.MemberRepository
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.ExtractableResponse
import io.restassured.response.Response
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TokenLoginControllerTest {
    @Autowired
    private lateinit var memberRepository: MemberRepository

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
        val members =
            listOf(createMina(), createPetra(), createJin())
        memberRepository.saveAll(members)
    }

    @AfterEach
    fun tearDown() {
        memberRepository.deleteAll()
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
        val body = TokenRequest(JIN.email, JIN.password)
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
        val body = TokenRequest(email = MINA.email, password = MINA.password)
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
        val body = TokenRequest(email = MINA.email, password = "hfkjhwldjw")
        val response = loginRequest(body)
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value())
    }

    @Test
    fun `test request to findMyInfo() with valid token`() {
        val body = TokenRequest(email = PETRA.email, password = PETRA.password)
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
        val body = TokenRequest(email = PETRA.email, password = PETRA.password)
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
