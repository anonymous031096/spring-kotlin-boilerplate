package com.example.boilerplate.shared.config

import com.example.boilerplate.modules.iam.security.JwtAuthEntryPoint
import com.example.boilerplate.modules.iam.security.JwtAuthFilter
import com.example.boilerplate.shared.filter.RateLimitFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableMethodSecurity
class SecurityConfig(
        private val jwtAuthFilter: JwtAuthFilter,
        private val jwtAuthEntryPoint: JwtAuthEntryPoint,
        private val rateLimitFilter: RateLimitFilter
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {

        http
                .csrf { it.disable() }
                .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
                .exceptionHandling { it.authenticationEntryPoint(jwtAuthEntryPoint) }
                .authorizeHttpRequests {

                    // auth api
                    it.requestMatchers("/auth/**").permitAll()

                    // swagger (dev only)
                    it.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                            .permitAll()

                    // actuator health (optional)
                    it.requestMatchers("/actuator/health").permitAll()

                    // all other APIs require authentication
                    it.anyRequest().authenticated()
                }
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter::class.java)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
