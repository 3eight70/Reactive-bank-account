package com.learning.reactive.wallet.config

import com.learning.reactive.wallet.security.AuthenticationManager
import com.learning.reactive.wallet.security.BearerTokenServerAuthenticationConverter
import com.learning.reactive.wallet.security.JwtHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import reactor.core.publisher.Mono

@Configuration
@EnableReactiveMethodSecurity
class WebSecurityConfig(
    @Value("\${jwt.secret}")
    private val secret: String
) {
    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity, authManager: AuthenticationManager): SecurityWebFilterChain {
        return http
            .csrf { csrf -> csrf.disable() }
            .cors { cors -> cors.disable() }
            .authorizeExchange { exchange ->
                exchange
                    .pathMatchers(HttpMethod.OPTIONS).permitAll()
                    .pathMatchers(HttpMethod.POST, "/api/v1/user/login", "/api/v1/user/register").permitAll()
                    .anyExchange().authenticated()
            }
            .exceptionHandling { exception ->
                exception
                    .authenticationEntryPoint { serverWebExchange, _ ->
                        Mono.fromRunnable {
                            serverWebExchange.response.statusCode = HttpStatus.UNAUTHORIZED
                        }
                    }
                    .accessDeniedHandler { serverWebExchange, _ ->
                        Mono.fromRunnable {
                            serverWebExchange.response.statusCode = HttpStatus.FORBIDDEN
                        }
                    }
            }
            .addFilterAt(authenticationWebFilter(authManager), SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }

    private fun authenticationWebFilter(auth : AuthenticationManager) : AuthenticationWebFilter{
        val filter = AuthenticationWebFilter(auth)
        filter.setServerAuthenticationConverter(BearerTokenServerAuthenticationConverter(JwtHandler(secret)))
        filter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"))

        return filter
    }
}