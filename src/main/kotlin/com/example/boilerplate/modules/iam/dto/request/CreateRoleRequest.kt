package com.example.boilerplate.modules.iam.dto.request

import java.util.UUID

data class CreateRoleRequest(
    val name: String,
    val permissionIds: List<UUID> = emptyList()
)
