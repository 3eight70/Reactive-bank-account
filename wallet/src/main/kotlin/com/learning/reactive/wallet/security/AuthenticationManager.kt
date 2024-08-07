package com.learning.reactive.wallet.security

import com.learning.reactive.wallet.repository.reactive.ReactiveUserRepository
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AuthenticationManager(
    private val userRepository: ReactiveUserRepository
) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        val principal: CustomPrincipal = authentication?.principal as CustomPrincipal
        return userRepository.findById(principal.getId())
            .map { authentication }
    }
}