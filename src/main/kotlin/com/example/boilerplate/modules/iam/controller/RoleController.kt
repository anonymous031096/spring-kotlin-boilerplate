package com.example.boilerplate.modules.iam.controller

import com.example.boilerplate.modules.iam.dto.request.CreateRoleRequest
import com.example.boilerplate.modules.iam.dto.request.UpdateRoleRequest
import com.example.boilerplate.modules.iam.dto.response.RoleResponse
import com.example.boilerplate.modules.iam.service.RoleService
import com.example.boilerplate.shared.dto.ApiResponse
import java.util.UUID
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/roles")
class RoleController(private val roleService: RoleService) {

    @GetMapping
    @PreAuthorize("hasAuthority('role:read')")
    fun findAll(): ApiResponse<List<RoleResponse>> =
        ApiResponse(true, data = roleService.findAll())

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role:read')")
    fun findById(@PathVariable id: UUID): ApiResponse<RoleResponse> =
        ApiResponse(true, data = roleService.findById(id))

    @PostMapping
    @PreAuthorize("hasAuthority('role:create')")
    fun create(@RequestBody request: CreateRoleRequest): ApiResponse<RoleResponse> =
        ApiResponse(true, data = roleService.create(request))

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role:update')")
    fun update(@PathVariable id: UUID, @RequestBody request: UpdateRoleRequest): ApiResponse<RoleResponse> =
        ApiResponse(true, data = roleService.update(id, request))

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role:delete')")
    fun delete(@PathVariable id: UUID): ApiResponse<Unit> {
        roleService.delete(id)
        return ApiResponse(true, message = "Role deleted")
    }
}
