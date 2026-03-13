package com.example.boilerplate.modules.iam.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SignupRequest(
        @field:NotBlank
        @field:Size(min = 3, message = "username must be at least 3 characters")
        val username: String,
        @field:NotBlank val password: String,
        @field:NotBlank val name: String
)
