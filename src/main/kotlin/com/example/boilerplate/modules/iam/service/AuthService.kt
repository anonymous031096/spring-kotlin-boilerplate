package com.example.boilerplate.modules.iam.service

import com.example.boilerplate.common.exception.BusinessException
import com.example.boilerplate.common.exception.UnauthorizedException
import com.example.boilerplate.common.exception.ValidationException
import com.example.boilerplate.modules.iam.dto.RefreshTokenRequest
import com.example.boilerplate.modules.iam.dto.request.SigninRequest
import com.example.boilerplate.modules.iam.dto.response.AuthResponse
import com.example.boilerplate.modules.iam.entity.User
import com.example.boilerplate.modules.iam.entity.UserSession
import com.example.boilerplate.modules.iam.entity.UserStatus
import com.example.boilerplate.modules.iam.repository.RoleRepository
import com.example.boilerplate.modules.iam.repository.UserRepository
import com.example.boilerplate.modules.iam.repository.UserSessionRepository
import com.example.boilerplate.modules.iam.security.JwtService
import com.example.boilerplate.modules.iam.security.SessionCache
import jakarta.servlet.http.HttpServletRequest
import jakarta.transaction.Transactional
import java.util.Date
import java.util.UUID
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
        private val userRepository: UserRepository,
        private val roleRepository: RoleRepository,
        private val passwordEncoder: PasswordEncoder,
        private val jwtService: JwtService,
        private val userSessionRepository: UserSessionRepository,
        private val sessionCache: SessionCache,
        private val sessionService: SessionService
) {

    fun signup(username: String, password: String, name: String) {
        if (userRepository.findByUsername(username) != null) {
            throw ValidationException(mapOf("username" to "Username already exists"))
        }

        val encodedPassword = passwordEncoder.encode(password)
        if (encodedPassword == null) {
            throw ValidationException(mapOf("password" to "Password cannot be empty"))
        }

        val userRole =
                roleRepository.findByName("USER")
                        ?: throw BusinessException("Default role USER not found")
        val user = User(username = username, password = encodedPassword, name = name)
        user.roles.add(userRole)
        userRepository.save(user)
    }

    @Transactional
    fun signin(request: SigninRequest, httpRequest: HttpServletRequest): AuthResponse {

        val user = userRepository.findByUsername(request.username)

        if (user == null || user.id == null) {
            throw BusinessException("Invalid username or password")
        }

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw BusinessException("Invalid username or password")
        }
        if (user.status != UserStatus.ACTIVE) {
            throw BusinessException("Invalid username or password")
        }

        val deviceId = httpRequest.getHeader("X-Device-Id") ?: "ALL_DEVICES"
        return createSessionAndTokens(user, deviceId, httpRequest)
    }

    @Transactional
    fun refreshToken(request: RefreshTokenRequest, httpRequest: HttpServletRequest): AuthResponse {
        val claims = jwtService.extractRefreshTokenClaims(request.refreshToken)
        val userId = UUID.fromString(claims.subject)
        val sessionId = UUID.fromString(claims["sid"] as String)
        val deviceId = claims["did"] as String
        val deviceIdHeader = httpRequest.getHeader("X-Device-Id") ?: "ALL_DEVICES"
        if (deviceIdHeader != deviceId) throw UnauthorizedException("Invalid device")
        if (claims.expiration.before(Date())) throw UnauthorizedException("Invalid refresh token")
        userSessionRepository.findBySessionIdAndDeviceIdAndRevokedFalse(sessionId, deviceId)
            ?: throw UnauthorizedException("Session not found")
        val user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
            ?: throw UnauthorizedException("User not found")
        return createSessionAndTokens(user, deviceId, httpRequest)
    }

    private fun createSessionAndTokens(
        user: User,
        deviceId: String,
        httpRequest: HttpServletRequest
    ): AuthResponse {
        sessionService.invalidateExistingForUserAndDevice(user.id!!, deviceId)
        val sessionId = UUID.randomUUID()
        val ipAddress = httpRequest.remoteAddr
        val userAgent = httpRequest.getHeader("User-Agent")
        val session = UserSession(user.id!!, sessionId, deviceId, ipAddress!!, userAgent!!)
        userSessionRepository.save(session)
        sessionCache.put(sessionId, deviceId)
        val accessToken = jwtService.generateAccessToken(user, session)
        val refreshToken = jwtService.generateRefreshToken(user, session)
        return AuthResponse(accessToken, refreshToken)
    }

    @Transactional
    fun logout(httpRequest: HttpServletRequest) {
        try {
            val authHeader = httpRequest.getHeader("Authorization") ?: return
            val token = authHeader.substring(7)
            val claims = jwtService.extractAccessTokenClaims(token)
            val sessionId = UUID.fromString(claims["sid"] as String)
            val deviceId = claims["did"] as String
            val userId = UUID.fromString(claims.subject)
            sessionService.revoke(sessionId, userId, deviceId)
        } catch (_: Exception) {}
    }
}
