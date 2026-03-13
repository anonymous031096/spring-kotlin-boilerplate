package com.example.boilerplate.modules.iam.repository

import com.example.boilerplate.modules.iam.entity.User
import com.example.boilerplate.modules.iam.entity.UserStatus
import java.util.UUID
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRepository : JpaRepository<User, UUID> {

    fun findByUsername(username: String): User?

    fun findByIdAndStatus(id: UUID, status: UserStatus): User?

    @EntityGraph(attributePaths = ["roles", "roles.permissions", "permissions"])
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.status = :status")
    fun findByIdAndStatusWithRolesAndPermissions(
        @Param("id") id: UUID,
        @Param("status") status: UserStatus
    ): User?

    @EntityGraph(attributePaths = ["roles", "roles.permissions", "permissions"])
    @Query("SELECT u FROM User u WHERE u.id = :id")
    fun findByIdWithRolesAndPermissions(@Param("id") id: UUID): User?
}
