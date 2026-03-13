package com.example.boilerplate.modules.iam.controller

import com.example.boilerplate.modules.iam.dto.request.ChangePasswordRequest
import com.example.boilerplate.modules.iam.dto.request.UpdateUserRequest
import com.example.boilerplate.modules.iam.dto.response.UserResponse
import com.example.boilerplate.modules.iam.service.UserService
import com.example.boilerplate.shared.dto.ApiResponse
import com.example.boilerplate.shared.security.SecurityUtils
import java.util.UUID
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    fun findAll(): ApiResponse<List<UserResponse>> = ApiResponse(true, data = userService.findAll())

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('user:read-me')")
    fun me(): ApiResponse<UserResponse> =
            ApiResponse(true, data = userService.findById(SecurityUtils.currentUserId()))

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:read')")
    fun findById(@PathVariable id: UUID): ApiResponse<UserResponse> =
            ApiResponse(true, data = userService.findById(id))

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:update')")
    fun update(
            @PathVariable id: UUID,
            @RequestBody request: UpdateUserRequest
    ): ApiResponse<UserResponse> = ApiResponse(true, data = userService.update(id, request))

    @PutMapping("/me/password")
    @PreAuthorize("hasAuthority('user:read-me')")
    fun changePassword(@RequestBody request: ChangePasswordRequest): ApiResponse<Unit> {
        userService.changePassword(SecurityUtils.currentUserId(), request.oldPassword, request.newPassword)
        return ApiResponse(true, message = "Password updated")
    }
}
