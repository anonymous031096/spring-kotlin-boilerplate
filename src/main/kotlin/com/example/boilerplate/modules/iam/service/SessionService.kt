package com.example.boilerplate.modules.iam.service

import com.example.boilerplate.modules.iam.repository.UserSessionRepository
import com.example.boilerplate.modules.iam.security.SessionCache
import java.util.UUID
import org.springframework.stereotype.Service

/**
 * Thu hồi session: luôn evict Redis + cập nhật DB để tránh quên evict.
 */
@Service
class SessionService(
    private val sessionCache: SessionCache,
    private val userSessionRepository: UserSessionRepository
) {

    /** Thu hồi session (logout hoặc trước khi tạo session mới khi refresh). */
    fun revoke(sessionId: UUID, userId: UUID, deviceId: String) {
        sessionCache.evict(sessionId, deviceId)
        userSessionRepository.revokeByUserIdAndDeviceIdAndRevokedFalse(userId, deviceId)
    }

    /** Vô hiệu hóa session hiện tại của user+device (trước signin/refresh): evict cache + xóa row. */
    fun invalidateExistingForUserAndDevice(userId: UUID, deviceId: String) {
        userSessionRepository.findByUserIdAndDeviceId(userId, deviceId)?.let { session ->
            sessionCache.evict(session.sessionId, deviceId)
        }
        userSessionRepository.deleteByUserIdAndDeviceId(userId, deviceId)
    }
}
