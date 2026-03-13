package com.example.boilerplate.modules.iam.dto.request

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)
