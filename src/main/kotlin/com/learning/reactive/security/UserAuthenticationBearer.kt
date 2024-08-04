package com.learning.reactive.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import reactor.core.publisher.Mono
import java.util.*

object UserAuthenticationBearer {
    fun create(result : JwtHandler.VerificationResult) : Mono<Authentication> {
        val claims = result.claims
        val subject = claims.subject
        val email = claims.get("role", String::class.java)
        val login = claims.get("login", String::class.java)

        val principal = CustomPrincipal(UUID.fromString(subject), login, email)

        return Mono.justOrEmpty(UsernamePasswordAuthenticationToken(principal, null, null))
    }
}