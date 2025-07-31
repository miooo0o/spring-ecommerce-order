package ecommerce.controller

import ecommerce.annotation.LoginMember
import ecommerce.dto.RegisteredMember
import ecommerce.dto.TokenRequest
import ecommerce.dto.TokenResponse
import ecommerce.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/members")
class TokenLoginController(private val authService: AuthService) {
    @PostMapping("/register")
    fun registerMember(
        @Valid @RequestBody request: TokenRequest,
    ): ResponseEntity<TokenResponse> {
        val tokenResponse = authService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(tokenResponse)
    }

    @PostMapping("/login")
    fun loginMember(
        @Valid @RequestBody request: TokenRequest,
    ): ResponseEntity<TokenResponse> {
        val tokenResponse = authService.login(request)
        return ResponseEntity.ok(tokenResponse)
    }

    /**
     * ex) request sample
     *
     * GET /api/members/me/token HTTP/1.1
     * authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbWFpbEBlbWFpbC5jb20iLCJpYXQiOjE2MTAzNzY2NzIsImV4cCI6MTYxMDM4MDI3Mn0.Gy4g5RwK1Nr7bKT1TOFS4Da6wxWh8l97gmMQDgF8c1E
     * accept: application/json
     */
    @GetMapping("me/token")
    fun findMyInfo(
        @LoginMember member: RegisteredMember,
    ): ResponseEntity<RegisteredMember> {
        return ResponseEntity.ok().body(member)
    }
}
