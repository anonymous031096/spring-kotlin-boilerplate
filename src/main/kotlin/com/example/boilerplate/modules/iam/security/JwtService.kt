package com.example.boilerplate.modules.iam.security

import com.example.boilerplate.modules.iam.config.JwtProperties
import com.example.boilerplate.modules.iam.entity.User
import com.example.boilerplate.modules.iam.entity.UserSession
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import java.io.File
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import java.util.Date
import org.springframework.stereotype.Service

@Service
class JwtService(private val jwtProperties: JwtProperties) {

    private val accessPrivateKey: PrivateKey = loadPrivateKey(jwtProperties.accessPrivateKeyPath)
    private val accessPublicKey: PublicKey = loadPublicKey(jwtProperties.accessPublicKeyPath)
    private val refreshPrivateKey: PrivateKey = loadPrivateKey(jwtProperties.refreshPrivateKeyPath)
    private val refreshPublicKey: PublicKey = loadPublicKey(jwtProperties.refreshPublicKeyPath)

    fun generateAccessToken(user: User, session: UserSession): String {
        val now = Date()
        val expiry = Date(now.time + jwtProperties.accessTokenExp * 1000)
        return Jwts.builder()
                .subject(user.id.toString())
                .claim("sid", session.sessionId.toString())
                .claim("did", session.deviceId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(accessPrivateKey)
                .compact()
    }

    fun generateRefreshToken(user: User, session: UserSession): String {
        val now = Date()
        val expiry = Date(now.time + jwtProperties.refreshTokenExp * 1000)
        return Jwts.builder()
                .subject(user.id.toString())
                .claim("sid", session.sessionId.toString())
                .claim("did", session.deviceId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(refreshPrivateKey)
                .compact()
    }

    /**
     * Verify and parse access token only. Used by JwtAuthFilter and logout. Refresh token will fail
     * here.
     */
    fun extractAccessTokenClaims(token: String): Claims {
        return Jwts.parser().verifyWith(accessPublicKey).build().parseSignedClaims(token).payload
    }

    /**
     * Verify and parse refresh token only. Used by refresh endpoint. Access token will fail here.
     */
    fun extractRefreshTokenClaims(token: String): Claims {
        return Jwts.parser().verifyWith(refreshPublicKey).build().parseSignedClaims(token).payload
    }

    fun isAccessTokenValid(token: String, userId: String): Boolean {
        val claims = extractAccessTokenClaims(token)
        return claims.subject == userId && !claims.expiration.before(Date())
    }

    // ====== key loader ======

    private fun loadPrivateKey(path: String): PrivateKey {

        val key =
                File(path)
                        .readText()
                        .replace("-----BEGIN PRIVATE KEY-----", "")
                        .replace("-----END PRIVATE KEY-----", "")
                        .replace("\n", "")
                        .replace("\r", "")

        val decoded = Base64.getDecoder().decode(key)
        val spec = PKCS8EncodedKeySpec(decoded)

        return KeyFactory.getInstance("RSA").generatePrivate(spec)
    }

    private fun loadPublicKey(path: String): PublicKey {

        val key =
                File(path)
                        .readText()
                        .replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "")
                        .replace("\n", "")
                        .replace("\r", "")

        val decoded = Base64.getDecoder().decode(key)
        val spec = X509EncodedKeySpec(decoded)

        return KeyFactory.getInstance("RSA").generatePublic(spec)
    }
}
