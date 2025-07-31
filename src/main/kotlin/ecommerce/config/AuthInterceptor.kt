package ecommerce.config

import ecommerce.infrastructure.AuthorizationExtractor
import ecommerce.infrastructure.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AuthInterceptor(
    private val authExtractor: AuthorizationExtractor,
    private val jwtTokenProvider: JwtTokenProvider,
) : HandlerInterceptor {
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        val token = authExtractor.extract(request)
        if (token.isEmpty()) {
            response.status = HttpStatus.UNAUTHORIZED.value()
            return false
        }
        if (!jwtTokenProvider.validateToken(token)) {
            response.status = HttpStatus.UNAUTHORIZED.value()
            return false
        }
        val email = jwtTokenProvider.getPayload(token)
        request.setAttribute("email", email)
        return true
    }
}
