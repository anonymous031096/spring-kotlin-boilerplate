package com.example.boilerplate.modules.iam.entity

import com.example.boilerplate.shared.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "user_sessions")
class UserSession(
        @Column(name = "user_id", nullable = false) val userId: UUID,
        @Column(name = "session_id", nullable = false) val sessionId: UUID = UUID.randomUUID(),
        @Column(name = "device_id") val deviceId: String? = null,
        @Column(name = "ip_address") val ipAddress: String? = null,
        @Column(name = "user_agent") val userAgent: String? = null,
        @Column(nullable = false) var revoked: Boolean = false,
) : BaseEntity()
