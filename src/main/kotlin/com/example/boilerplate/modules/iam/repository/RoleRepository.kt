package com.example.boilerplate.modules.iam.repository

import com.example.boilerplate.modules.iam.entity.Role
import java.util.UUID
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface RoleRepository : JpaRepository<Role, UUID> {

    fun findByName(name: String): Role?

    @EntityGraph(attributePaths = ["permissions"])
    @Query("SELECT r FROM Role r WHERE r.id = :id")
    fun findByIdWithPermissions(@Param("id") id: UUID): Role?

    @Query("SELECT DISTINCT r FROM Role r LEFT JOIN FETCH r.permissions")
    fun findAllWithPermissions(): List<Role>
}
