package com.learning.reactive.service.impl

import com.learning.reactive.dto.user.AuthResponseDto
import com.learning.reactive.dto.user.LoginUserRequestDto
import com.learning.reactive.dto.user.RegisterUserRequestDto
import com.learning.reactive.dto.user.UserDto
import com.learning.reactive.exception.user.UserAlreadyExistsException
import com.learning.reactive.models.User
import com.learning.reactive.repository.ReactiveUserRepository
import com.learning.reactive.security.CustomPrincipal
import com.learning.reactive.security.SecurityService
import com.learning.reactive.service.UserService
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.util.*

@Service
class UserServiceImpl(
    private val userRepository: ReactiveUserRepository,
    private val securityService: SecurityService,
    private val passwordEncoder: PasswordEncoder
) : UserService {
    @Transactional
    override fun registerUser(dto: RegisterUserRequestDto): Mono<UserDto> {
        return userRepository.findByEmail(dto.email!!)
            .flatMap<UserDto> {
                Mono.error(UserAlreadyExistsException("Пользователь с указанной почтой: ${dto.email} уже существует"))
            }
            .switchIfEmpty(
                userRepository.findByLogin(dto.login!!)
                    .flatMap {
                        Mono.error(
                            UserAlreadyExistsException("Пользователь с указанным логином: ${dto.login} уже существует")
                        )
                    }
            )
            .switchIfEmpty(
                Mono.just(
                    User(
                        UUID.randomUUID(),
                        dto.login,
                        dto.email,
                        passwordEncoder.encode(dto.password)
                    )
                )
                    .flatMap { userRepository.save(it) }
                    .map {
                        UserDto(
                            it.getId(),
                            it.username,
                            it.getEmail()
                        )
                    }
            )
    }

    override fun loginUser(dto: LoginUserRequestDto): Mono<AuthResponseDto> {
        return securityService.authenticate(dto.login!!, dto.password!!)
            .flatMap { tokenDetails ->
                Mono.just(
                    AuthResponseDto(
                        userId = tokenDetails.userId,
                        token = tokenDetails.token,
                        issuedAt = tokenDetails.issuedAt,
                        expiresAt = tokenDetails.expiresAt
                    )
                )
            }
    }

    @Transactional(readOnly = true)
    override fun getProfile(authentication: Authentication): Mono<UserDto> {
        val principal = authentication.principal as CustomPrincipal

        return userRepository.findById(principal.getId())
            .map { user ->
                UserDto(
                    user.getId(),
                    user.username,
                    user.getEmail()
                )
            }
    }
}