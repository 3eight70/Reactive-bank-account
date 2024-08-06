package com.learning.reactive.security

import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class BearerTokenServerAuthenticationConverter(
    private val jwtHandler: JwtHandler,
) : ServerAuthenticationConverter {
    private val BEARER_PREFIX = "Bearer "
    private val getBearerValue: (String) -> Mono<String> = { authValue ->
        Mono.justOrEmpty(authValue.removePrefix(BEARER_PREFIX))
    }

    override fun convert(exchange: ServerWebExchange?): Mono<Authentication> {
        return extractHeader(exchange)
            .flatMap(getBearerValue)
            .flatMap(jwtHandler::checkToken)
            .flatMap(UserAuthenticationBearer::create)
    }

    private fun extractHeader(exchange: ServerWebExchange?): Mono<String> {
        return Mono.justOrEmpty(
            exchange
                ?.request
                ?.headers
                ?.getFirst(HttpHeaders.AUTHORIZATION)
        )
    }
}