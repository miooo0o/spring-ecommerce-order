package ecommerce.service

import ecommerce.dto.RegisteredMember
import ecommerce.dto.Role
import ecommerce.dto.TokenRequest
import ecommerce.dto.TokenResponse
import ecommerce.exception.ConflictException
import ecommerce.exception.ForbiddenException
import ecommerce.exception.NotFoundException
import ecommerce.exception.UnauthorizedException
import ecommerce.infrastructure.JwtTokenProvider
import ecommerce.model.Member
import ecommerce.repository.MemberRepositoryJPA
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val jwtTokenProvider: JwtTokenProvider,
    private val memberRepository: MemberRepositoryJPA,
) {
    fun findMember(payload: String): RegisteredMember {
        val member = memberRepository.findByEmail(payload)
        if (member == null) {
            throw NotFoundException("Member not found")
        }
        return member.toRegisteredMember()
    }

    fun findAdminMember(email: String): RegisteredMember {
        val member = findMember(email)
        return member.takeIf { it.role == Role.ADMIN }
            ?: throw ForbiddenException()
    }

    fun findMemberByToken(token: String): RegisteredMember {
        if (!jwtTokenProvider.validateToken(token)) {
            throw UnauthorizedException("Invalid token")
        }
        val payload = jwtTokenProvider.getPayload(token)
        return findMember(payload)
    }

    fun register(request: TokenRequest): TokenResponse {
        if (memberRepository.existsByEmail(request.email)) {
            throw ConflictException("Account with email already exists")
        }
        memberRepository.save(Member(email = request.email, password = request.password))
        return createToken(request)
    }

    fun login(request: TokenRequest): TokenResponse {
        val member =
            memberRepository.findByEmail(request.email)
                ?: throw UnauthorizedException("No account with email exists")
        member.validatePassword(request.password)
        return createToken(request)
    }

    fun createToken(tokenRequest: TokenRequest): TokenResponse {
        val accessToken = jwtTokenProvider.createToken(tokenRequest.email)
        return TokenResponse(accessToken)
    }
}
