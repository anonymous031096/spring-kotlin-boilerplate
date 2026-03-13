package com.example.boilerplate.modules.iam.dto.request

import java.util.UUID

data class UpdateRoleRequest(
    val name: String? = null,
    val permissionIds: List<UUID>? = null
)
