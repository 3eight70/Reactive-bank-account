package com.learning.reactive.security

import com.learning.reactive.exception.common.UnauthorizedException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import reactor.core.publisher.Mono
import java.util.*

class JwtHandler(
    private val secret: String
) {
    data class VerificationResult(val claims: Claims, val token: String)

    fun checkToken(accessToken: String): Mono<VerificationResult> {
        return Mono.just(verify(accessToken))
            .onErrorResume { e -> Mono.error(UnauthorizedException(e.message ?: "Не авторизован")) }
    }

    private fun verify(token: String): VerificationResult {
        val claims = getClaims(token)
        val expirationDate = claims.expiration

        if (expirationDate.before(Date())) {
            throw UnauthorizedException("Время жизни токена истекло")
        }

        return VerificationResult(claims, token)
    }

    private fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(secret.toByteArray()))
            .build()
            .parseSignedClaims(token)
            .payload

    }
}