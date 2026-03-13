package com.example.boilerplate.modules.iam.mapper

import com.example.boilerplate.modules.iam.dto.response.UserResponse
import com.example.boilerplate.modules.iam.entity.User
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface UserMapper {

    fun toResponse(user: User): UserResponse
}
