package com.example.boilerplate.modules.iam.dto.request

import jakarta.validation.constraints.NotBlank

data class SigninRequest(
        @field:NotBlank val username: String,
        @field:NotBlank val password: String,
)
