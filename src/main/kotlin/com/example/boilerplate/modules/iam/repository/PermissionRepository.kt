package com.example.boilerplate.modules.iam.repository

import com.example.boilerplate.modules.iam.entity.Permission
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface PermissionRepository : JpaRepository<Permission, UUID> {

    fun findByName(name: String): Permission?
}
