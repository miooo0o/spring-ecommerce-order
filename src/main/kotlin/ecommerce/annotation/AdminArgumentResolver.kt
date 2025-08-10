package ecommerce.annotation

import ecommerce.exception.NotFoundException
import ecommerce.exception.UnauthorizedException
import ecommerce.service.AuthService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class AdminArgumentResolver(
    private val authService: AuthService,
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(Admin::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any {
        val request =
            webRequest.getNativeRequest(HttpServletRequest::class.java)
                ?: throw IllegalStateException("HttpServletRequest not available")

        val email =
            request.getAttribute("email")?.toString()
                ?: throw IllegalStateException("Email attribute  missing")

        return try {
            authService.findAdminMember(email)
        } catch (e: NotFoundException) {
            throw UnauthorizedException()
        }
    }
}
