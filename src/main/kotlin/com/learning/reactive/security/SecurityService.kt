package com.learning.reactive.security

import com.learning.reactive.exception.security.UnauthorizedException
import com.learning.reactive.models.User
import com.learning.reactive.repository.ReactiveUserRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class SecurityService(
    private val userRepository: ReactiveUserRepository,
    private val passwordEncoder: PasswordEncoder,
    @Value("\${jwt.issuer}")
    private val issuer: String,
    @Value("\${jwt.secret}")
    private val secret: String,
    @Value("\${jwt.expiration}")
    private val expirationInSeconds: Int
) {
    fun generateToken(user: User): TokenDetails {
        val claims = HashMap<String, Any>()
        claims.put("email", user.getEmail())
        claims.put("login", user.username)
        return generateToken(claims, user.getId().toString())
    }

    fun generateToken(
        claims: Map<String, Any>,
        subject: String
    ): TokenDetails {
        val expirationTimeInMillis: Long = expirationInSeconds * 1000L
        val expirationDate = Date(Date().time + expirationTimeInMillis)

        return generateToken(expirationDate, claims, subject)
    }

    fun generateToken(
        expirationDate: Date,
        claims: Map<String, Any>,
        subject: String
    ): TokenDetails {
        val createdDate = Date()
        val token: String = Jwts.builder()
            .claims().add(claims)
            .issuer(issuer)
            .subject(subject)
            .issuedAt(createdDate)
            .id(UUID.randomUUID().toString())
            .expiration(expirationDate)
            .and()
            .signWith(Keys.hmacShaKeyFor(secret.toByteArray()), Jwts.SIG.HS256)
            .compact()

        return TokenDetails(
            userId = UUID.fromString(subject),
            token = token,
            issuedAt = createdDate,
            expiresAt = expirationDate
        )
    }

    fun authenticate(login: String, password: String): Mono<TokenDetails> {
        return userRepository.findByLogin(login)
            .flatMap { user ->
                when {
                    !passwordEncoder.matches(password, user.password) -> Mono.error(UnauthorizedException("Неверный пароль"))
                    else -> Mono.just(generateToken(user))
                }
            }
            .switchIfEmpty(Mono.error(UnauthorizedException("Неверный логин")))
    }
}