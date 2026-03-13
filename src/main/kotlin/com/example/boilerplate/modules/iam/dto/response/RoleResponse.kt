package com.example.boilerplate.modules.iam.dto.response

import java.util.UUID

data class RoleResponse(
    val id: UUID,
    val name: String,
    val permissions: List<PermissionResponse> = emptyList()
)
