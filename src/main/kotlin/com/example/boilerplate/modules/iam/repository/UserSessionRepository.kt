package com.example.boilerplate.modules.iam.repository

import com.example.boilerplate.modules.iam.entity.UserSession
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface UserSessionRepository : JpaRepository<UserSession, UUID> {

    fun findBySessionIdAndDeviceIdAndRevokedFalse(sessionId: UUID, deviceId: String): UserSession?

    fun findByUserIdAndDeviceId(userId: UUID, deviceId: String): UserSession?

    fun deleteByUserIdAndDeviceId(userId: UUID, deviceId: String)

    @Modifying
    @Query(
            "update UserSession s set s.revoked = true where s.userId = :userId and s.deviceId = :deviceId and s.revoked = false"
    )
    fun revokeByUserIdAndDeviceIdAndRevokedFalse(userId: UUID, deviceId: String): Int
}
