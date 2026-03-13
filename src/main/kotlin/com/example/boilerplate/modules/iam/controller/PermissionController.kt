package com.example.boilerplate.modules.iam.controller

import com.example.boilerplate.modules.iam.dto.response.PermissionResponse
import com.example.boilerplate.modules.iam.service.PermissionService
import com.example.boilerplate.shared.dto.ApiResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/permissions")
class PermissionController(private val permissionService: PermissionService) {

    @GetMapping
    @PreAuthorize("hasAuthority('permission:read')")
    fun findAll(): ApiResponse<List<PermissionResponse>> {
        return ApiResponse<List<PermissionResponse>>(true, permissionService.findAll())
    }
}
