package com.example.boilerplate.modules.iam.mapper

import com.example.boilerplate.modules.iam.dto.response.RoleResponse
import com.example.boilerplate.modules.iam.entity.Role
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring", uses = [PermissionMapper::class])
interface RoleMapper {

    @Mapping(target = "id", expression = "java(role.getId() != null ? role.getId() : java.util.UUID.randomUUID())")
    fun toResponse(role: Role): RoleResponse
}
