package com.example.boilerplate.shared.ratelimit

import java.time.Duration

data class RateLimitPolicy(val limit: Long, val duration: Duration)
