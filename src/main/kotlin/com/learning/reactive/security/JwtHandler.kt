package com.learning.reactive.security

import com.learning.reactive.exception.security.AuthException
import com.learning.reactive.exception.security.UnauthorizedException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class JwtHandler(
    private val secret: String
) {
    data class VerificationResult(val claims: Claims, val token: String)

    fun checkToken(accessToken: String): Mono<VerificationResult> {
        return Mono.just(verify(accessToken))
            .onErrorResume { e -> Mono.error(UnauthorizedException(e.message ?: "Unauthorized")) }
    }

    fun verify(token: String): VerificationResult {
        val claims = getClaims(token)
        val expirationDate = claims.expiration

        if (expirationDate.before(Date())){
            throw AuthException("Token expired", "TOKEN_EXPIRED")
        }

        return VerificationResult(claims, token)
    }

    private fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .payload

    }

    fun getSignInKey(): SecretKey {
        val bytes = Base64.getDecoder()
            .decode(secret.toByteArray(StandardCharsets.UTF_8));
        return SecretKeySpec(bytes, "HmacSHA256"); }
}