package ecommerce.controller

import ecommerce.BasicTestFixture
import ecommerce.annotation.LoginMemberArgumentResolver
import ecommerce.config.WebMvcConfiguration
import ecommerce.dto.RegisteredMember
import ecommerce.dto.Role
import ecommerce.infrastructure.AuthorizationExtractor
import ecommerce.infrastructure.JwtTokenProvider
import ecommerce.service.ProductService
import org.assertj.core.api.Assertions.assertThat
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(
    controllers = [ProductController::class],
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = [WebMvcConfiguration::class],
        ),
    ],
)
@Import(TestWebConfig::class)
class ProductControllerMockTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
    ) {
        @MockitoBean
        private lateinit var productService: ProductService

        @MockitoBean
        private lateinit var authorizationExtractor: AuthorizationExtractor

        @MockitoBean
        private lateinit var jwtTokenProvider: JwtTokenProvider

        @Autowired
        private lateinit var loginMemberArgumentResolver: LoginMemberArgumentResolver

        lateinit var mockMember: RegisteredMember

        fun setMockMemberTo(type: Role) {
            mockMember =
                when (type) {
                    Role.ADMIN -> {
                        RegisteredMember(id = 2L, email = "admin@email.com", role = Role.ADMIN)
                    }
                    Role.USER -> {
                        RegisteredMember(id = 1L, email = "user@email.com", role = Role.USER)
                    }
                }
        }

        @BeforeEach
        fun setup() {
            whenever(loginMemberArgumentResolver.supportsParameter(any())).thenReturn(true)
            whenever(loginMemberArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenAnswer { mockMember }
        }

        @Test
        fun `test getPages`() {
            setMockMemberTo(Role.USER)

            val testProduct = BasicTestFixture.createPaintingSadHuman()
            whenever(productService.getPages(any(), any()))
                .thenReturn(PageImpl(listOf(testProduct), PageRequest.of(0, 10), 20))

            val result =
                mockMvc.perform(get("/api/products-page"))
                    .andExpect(status().isOk)
                    .andReturn()

            val jsonObject = JSONObject(result.response.contentAsString)

            assertThat(jsonObject.get("totalPages")).isEqualTo(2)
            assertThat(jsonObject.get("totalElements")).isEqualTo(20)
            assertThat(jsonObject.get("number")).isEqualTo(0)
            assertThat(jsonObject.get("size")).isEqualTo(10)
            assertThat(jsonObject.get("content")).isNotNull()
        }
    }
