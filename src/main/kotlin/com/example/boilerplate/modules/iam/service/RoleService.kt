package com.example.boilerplate.modules.iam.service

import com.example.boilerplate.common.exception.NotFoundException
import com.example.boilerplate.common.exception.ValidationException
import com.example.boilerplate.modules.iam.dto.request.CreateRoleRequest
import com.example.boilerplate.modules.iam.dto.request.UpdateRoleRequest
import com.example.boilerplate.modules.iam.dto.response.RoleResponse
import com.example.boilerplate.modules.iam.entity.Permission
import com.example.boilerplate.modules.iam.entity.Role
import com.example.boilerplate.modules.iam.mapper.RoleMapper
import com.example.boilerplate.modules.iam.repository.PermissionRepository
import com.example.boilerplate.modules.iam.repository.RoleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class RoleService(
    private val roleRepository: RoleRepository,
    private val permissionRepository: PermissionRepository,
    private val roleMapper: RoleMapper
) {

    @Transactional
    fun create(request: CreateRoleRequest): RoleResponse {
        if (roleRepository.findByName(request.name) != null) {
            throw ValidationException(mapOf("name" to "Role name already exists"))
        }
        val permissions = resolvePermissions(request.permissionIds)
        val role = Role(name = request.name)
        role.permissions.addAll(permissions)
        return roleMapper.toResponse(roleRepository.save(role))
    }

    fun findAll(): List<RoleResponse> =
        roleRepository.findAllWithPermissions().map { roleMapper.toResponse(it) }

    fun findById(id: UUID): RoleResponse {
        val role = roleRepository.findByIdWithPermissions(id)
            ?: throw NotFoundException("Role not found")
        return roleMapper.toResponse(role)
    }

    @Transactional
    fun update(id: UUID, request: UpdateRoleRequest): RoleResponse {
        val role = roleRepository.findByIdWithPermissions(id)
            ?: throw NotFoundException("Role not found")
        request.name?.let { newName ->
            val existing = roleRepository.findByName(newName)
            if (existing != null && existing.id != role.id) {
                throw ValidationException(mapOf("name" to "Role name already exists"))
            }
            role.name = newName
        }
        request.permissionIds?.let { ids ->
            role.permissions.clear()
            role.permissions.addAll(resolvePermissions(ids))
        }
        return roleMapper.toResponse(roleRepository.save(role))
    }

    @Transactional
    fun delete(id: UUID) {
        if (!roleRepository.existsById(id)) {
            throw NotFoundException("Role not found")
        }
        roleRepository.deleteById(id)
    }

    private fun resolvePermissions(ids: List<UUID>): Set<Permission> {
        if (ids.isEmpty()) return emptySet()
        val permissions = permissionRepository.findAllById(ids)
        val foundIds = permissions.map { it.id!! }.toSet()
        val missing = ids.filter { it !in foundIds }
        if (missing.isNotEmpty()) {
            throw ValidationException(mapOf("permissionIds" to "Permissions not found: $missing"))
        }
        return permissions.toSet()
    }
}
