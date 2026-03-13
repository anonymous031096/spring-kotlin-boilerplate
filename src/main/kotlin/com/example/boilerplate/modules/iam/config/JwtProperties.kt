package com.example.boilerplate.modules.iam.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
        var accessPrivateKeyPath: String = "",
        var accessPublicKeyPath: String = "",
        var refreshPrivateKeyPath: String = "",
        var refreshPublicKeyPath: String = "",
        var accessTokenExp: Long = 900,
        var refreshTokenExp: Long = 2592000
)
