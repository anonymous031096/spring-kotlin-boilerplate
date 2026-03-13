package com.example.boilerplate.modules.iam.security

import com.example.boilerplate.modules.iam.config.JwtProperties
import java.util.UUID
import java.util.concurrent.TimeUnit
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

private const val KEY_PREFIX = "iam:session:"

@Component
class SessionCache(
    private val redis: StringRedisTemplate,
    private val jwtProperties: JwtProperties
) {

    fun exists(sessionId: UUID, deviceId: String): Boolean =
        redis.hasKey(key(sessionId, deviceId)) == true

    fun put(sessionId: UUID, deviceId: String) {
        redis.opsForValue().set(key(sessionId, deviceId), "1", jwtProperties.accessTokenExp, TimeUnit.SECONDS)
    }

    fun evict(sessionId: UUID, deviceId: String) {
        redis.delete(key(sessionId, deviceId))
    }

    private fun key(sessionId: UUID, deviceId: String) = "$KEY_PREFIX$sessionId:$deviceId"
}
