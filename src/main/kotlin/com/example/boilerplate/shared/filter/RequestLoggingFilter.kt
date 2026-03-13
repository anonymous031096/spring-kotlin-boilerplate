package com.example.boilerplate.shared.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class RequestLoggingFilter : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(RequestLoggingFilter::class.java)

    override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain
    ) {

        val start = System.currentTimeMillis()
        val requestId = MDC.get("requestId")

        log.info("[$requestId] ${request.method} ${request.requestURI}")

        filterChain.doFilter(request, response)

        val duration = System.currentTimeMillis() - start

        log.info("[$requestId] completed ${response.status} in ${duration}ms")
    }
}
