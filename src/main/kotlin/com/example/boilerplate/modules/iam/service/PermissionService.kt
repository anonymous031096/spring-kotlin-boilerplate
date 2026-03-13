package com.example.boilerplate.modules.iam.service

import com.example.boilerplate.modules.iam.dto.response.PermissionResponse
import com.example.boilerplate.modules.iam.mapper.PermissionMapper
import com.example.boilerplate.modules.iam.repository.PermissionRepository
import org.springframework.stereotype.Service

@Service
class PermissionService(
        private val permissionRepository: PermissionRepository,
        private val permissionMapper: PermissionMapper
) {

    fun findAll(): List<PermissionResponse> {
        val permissions = permissionRepository.findAll()
        return permissions.map { permissionMapper.toResponse(it) }
    }
}
