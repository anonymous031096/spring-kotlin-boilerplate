package com.example.boilerplate.modules.iam.controller

import com.example.boilerplate.modules.iam.dto.RefreshTokenRequest
import com.example.boilerplate.modules.iam.dto.request.SigninRequest
import com.example.boilerplate.modules.iam.dto.request.SignupRequest
import com.example.boilerplate.modules.iam.dto.response.AuthResponse
import com.example.boilerplate.modules.iam.dto.response.UserResponse
import com.example.boilerplate.modules.iam.service.AuthService
import com.example.boilerplate.shared.dto.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/signup")
    fun signup(@Valid @RequestBody request: SignupRequest): ApiResponse<UserResponse> {
        authService.signup(request.username, request.password, request.name)
        return ApiResponse<UserResponse>(true)
    }

    @PostMapping("/signin")
    fun signin(
            @Valid @RequestBody request: SigninRequest,
            httpRequest: HttpServletRequest
    ): ApiResponse<AuthResponse> {
        val response = authService.signin(request, httpRequest)
        return ApiResponse<AuthResponse>(true, response)
    }

    @PostMapping("/refresh")
    fun refreshToken(
            @Valid @RequestBody request: RefreshTokenRequest,
            httpRequest: HttpServletRequest
    ): ApiResponse<AuthResponse> {
        val response = authService.refreshToken(request, httpRequest)
        return ApiResponse<AuthResponse>(true, response)
    }

    @PostMapping("/logout")
    fun logout(httpRequest: HttpServletRequest) {
        authService.logout(httpRequest)
    }
}
