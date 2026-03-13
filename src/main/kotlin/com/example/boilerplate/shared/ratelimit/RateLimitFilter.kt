package com.example.boilerplate.shared.filter

import com.example.boilerplate.shared.ratelimit.RateLimitPolicy
import com.example.boilerplate.shared.ratelimit.RateLimitService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.time.Duration
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class RateLimitFilter(private val rateLimitService: RateLimitService) : OncePerRequestFilter() {

    private val loginPolicy = RateLimitPolicy(limit = 5, duration = Duration.ofMinutes(1))

    private val apiPolicy = RateLimitPolicy(limit = 100, duration = Duration.ofMinutes(1))

    override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain
    ) {

        val path = request.servletPath
        val ip = request.remoteAddr

        val policy =
                when {
                    path.startsWith("/auth/signin") -> loginPolicy
                    else -> apiPolicy
                }

        val key = buildKey(path, ip)

        val result = rateLimitService.allowRequest(key, policy.limit, policy.duration)

        response.setHeader("X-RateLimit-Limit", policy.limit.toString())
        response.setHeader("X-RateLimit-Remaining", result.remaining.toString())
        response.setHeader("X-RateLimit-Reset", result.reset.toString())

        if (!result.allowed) {
            response.status = 429
            response.writer.write("Too many requests")
            return
        }

        filterChain.doFilter(request, response)
    }

    private fun buildKey(path: String, ip: String): String {

        if (path.startsWith("/auth/signin")) {
            return "signin:$ip"
        }

        val authentication = SecurityContextHolder.getContext().authentication

        val userId = authentication?.name

        return if (userId != null) {
            "api:user:$userId"
        } else {
            "api:ip:$ip"
        }
    }
}
