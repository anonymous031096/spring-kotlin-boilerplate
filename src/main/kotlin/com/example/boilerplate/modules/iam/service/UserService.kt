package com.example.boilerplate.modules.iam.service

import com.example.boilerplate.common.exception.NotFoundException
import com.example.boilerplate.common.exception.ValidationException
import com.example.boilerplate.modules.iam.dto.request.UpdateUserRequest
import com.example.boilerplate.modules.iam.dto.response.UserResponse
import com.example.boilerplate.modules.iam.entity.User
import com.example.boilerplate.modules.iam.repository.UserRepository
import java.util.UUID
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
        private val userRepository: UserRepository,
        private val passwordEncoder: PasswordEncoder
) {

    fun findAll(): List<UserResponse> = userRepository.findAll().map { toResponse(it) }

    fun findById(id: UUID): UserResponse = toResponse(getByIdOrThrow(id))

    @Transactional
    fun update(id: UUID, request: UpdateUserRequest): UserResponse {
        val user = getByIdOrThrow(id)
        request.name?.let { user.name = it }
        request.status?.let { user.status = it }
        return toResponse(userRepository.save(user))
    }

    @Transactional
    fun changePassword(userId: UUID, oldPassword: String, newPassword: String) {
        val user = getByIdOrThrow(userId)
        if (!passwordEncoder.matches(oldPassword, user.password)) {
            throw ValidationException(mapOf("oldPassword" to "Current password is incorrect"))
        }
        user.password = requireNotNull(passwordEncoder.encode(newPassword)) { "encode" }
        userRepository.save(user)
    }

    private fun getByIdOrThrow(id: UUID): User =
        userRepository.findById(id).orElseThrow { NotFoundException("User not found") }

    private fun toResponse(user: User): UserResponse =
            UserResponse(
                    id = user.id!!,
                    username = user.username,
                    name = user.name,
                    status = user.status
            )
}
