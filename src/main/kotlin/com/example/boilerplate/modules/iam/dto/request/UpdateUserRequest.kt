package com.example.boilerplate.modules.iam.dto.request

import com.example.boilerplate.modules.iam.entity.UserStatus

data class UpdateUserRequest(
    val name: String? = null,
    val status: UserStatus? = null
)
