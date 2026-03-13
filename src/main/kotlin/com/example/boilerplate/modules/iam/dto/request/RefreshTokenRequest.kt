package com.example.boilerplate.modules.iam.dto

import jakarta.validation.constraints.NotBlank

data class RefreshTokenRequest(@field:NotBlank val refreshToken: String)
