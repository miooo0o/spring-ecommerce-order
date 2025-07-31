package ecommerce.config

import ecommerce.annotation.AdminArgumentResolver
import ecommerce.annotation.LoginMemberArgumentResolver
import ecommerce.infrastructure.AuthorizationExtractor
import ecommerce.infrastructure.JwtTokenProvider
import ecommerce.service.AuthService
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfiguration(
    private val authService: AuthService,
    private val authExtractor: AuthorizationExtractor,
    private val jwtTokenProvider: JwtTokenProvider,
) : WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver?>) {
        resolvers.add(LoginMemberArgumentResolver(authService))
        resolvers.add(AdminArgumentResolver(authService))
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(AuthInterceptor(authExtractor, jwtTokenProvider))
            .addPathPatterns("/admin/**")
            .addPathPatterns("/api/**")
            .excludePathPatterns("/api/members/login", "/api/members/register")
    }
}
