package com.example.boilerplate.modules.iam.security

import com.example.boilerplate.common.exception.UnauthorizedException
import com.example.boilerplate.modules.iam.entity.UserStatus
import com.example.boilerplate.modules.iam.repository.UserRepository
import com.example.boilerplate.modules.iam.repository.UserSessionRepository
import com.example.boilerplate.shared.dto.CurrentUser
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.util.UUID
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
        private val jwtService: JwtService,
        private val userRepository: UserRepository,
        private val userSessionRepository: UserSessionRepository,
        private val sessionCache: SessionCache
) : OncePerRequestFilter() {

    override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        if (authHeader.isNullOrBlank() || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val jwt = authHeader.removePrefix("Bearer ").trim()
        try {
            val claims = jwtService.extractAccessTokenClaims(jwt)

            validateDeviceId(request, claims)

            val userIdString = claims.subject
            val sessionIdString =
                    claims["sid"] as? String
                            ?: throw UnauthorizedException("Session ID missing in token")
            val sessionId = UUID.fromString(sessionIdString)
            val deviceId = claims["did"] as String

            if (!sessionCache.exists(sessionId, deviceId)) {
                userSessionRepository.findBySessionIdAndDeviceIdAndRevokedFalse(sessionId, deviceId)
                        ?: throw UnauthorizedException("Session not found")
                sessionCache.put(sessionId, deviceId)
            }
            if (SecurityContextHolder.getContext().authentication == null) {
                val userId = UUID.fromString(userIdString)
                val user =
                        userRepository.findByIdAndStatusWithRolesAndPermissions(
                                userId,
                                UserStatus.ACTIVE
                        )
                                ?: throw UnauthorizedException("User not found")
                if (jwtService.isAccessTokenValid(jwt, userIdString)) {
                    SecurityContextHolder.getContext().authentication =
                            buildAuthentication(user, request)
                }
            }
        } catch (_: Exception) {
            // Intentionally ignored, pass processing to filter chain
        }

        filterChain.doFilter(request, response)
    }

    private fun validateDeviceId(request: HttpServletRequest, claims: Map<String, Any>) {
        val deviceIdHeader = request.getHeader("X-Device-Id") ?: "ALL_DEVICES"
        val tokenDeviceId =
                claims["did"] as? String
                        ?: throw UnauthorizedException("Device ID missing in token")
        if (deviceIdHeader != tokenDeviceId) {
            throw UnauthorizedException("Invalid device")
        }
    }

    private fun buildAuthentication(
            user: com.example.boilerplate.modules.iam.entity.User,
            request: HttpServletRequest
    ): UsernamePasswordAuthenticationToken {
        val roleAuthorities = user.roles.map { SimpleGrantedAuthority("ROLE_${it.name}") }
        val permissionsFromRoles =
                user.roles.flatMap { it.permissions }.map { SimpleGrantedAuthority(it.name) }
        val userDirectPermissions = user.permissions.map { SimpleGrantedAuthority(it.name) }
        val authorities =
                (roleAuthorities + permissionsFromRoles + userDirectPermissions).distinctBy {
                    it.authority
                }

        val principal = CurrentUser(user.id!!, user.username, user.name)

        return UsernamePasswordAuthenticationToken(principal, null, authorities).apply {
            details = WebAuthenticationDetailsSource().buildDetails(request)
        }
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.servletPath
        return path.startsWith("/auth/") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/actuator")
    }
}
