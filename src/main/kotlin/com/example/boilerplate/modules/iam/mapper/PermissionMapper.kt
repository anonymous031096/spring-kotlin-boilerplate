package com.example.boilerplate.modules.iam.mapper

import com.example.boilerplate.modules.iam.dto.response.PermissionResponse
import com.example.boilerplate.modules.iam.entity.Permission
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface PermissionMapper {

    fun toResponse(permission: Permission): PermissionResponse
}
