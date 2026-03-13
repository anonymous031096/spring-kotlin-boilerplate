package com.example.boilerplate.modules.iam.dto.response

import com.example.boilerplate.modules.iam.entity.UserStatus
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val username: String,
    val name: String,
    val status: UserStatus
)
