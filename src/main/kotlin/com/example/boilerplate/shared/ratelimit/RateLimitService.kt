package com.example.boilerplate.shared.ratelimit

import java.time.Duration
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class RateLimitService(private val redisTemplate: StringRedisTemplate) {

    fun allowRequest(key: String, limit: Long, duration: Duration): RateLimitResult {

        val redisKey = "ratelimit:$key"

        val count = redisTemplate.opsForValue().increment(redisKey)

        if (count == 1L) {
            redisTemplate.expire(redisKey, duration)
        }

        val ttl = redisTemplate.getExpire(redisKey)

        val allowed = count!! <= limit

        return RateLimitResult(
                allowed = allowed,
                remaining = (limit - count).coerceAtLeast(0),
                reset = ttl
        )
    }
}

data class RateLimitResult(val allowed: Boolean, val remaining: Long, val reset: Long)
