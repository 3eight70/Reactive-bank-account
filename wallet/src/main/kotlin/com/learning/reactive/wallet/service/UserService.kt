package com.learning.reactive.wallet.service

import com.learning.reactive.wallet.dto.user.AuthResponseDto
import com.learning.reactive.wallet.dto.user.LoginUserRequestDto
import com.learning.reactive.wallet.dto.user.RegisterUserRequestDto
import com.learning.reactive.wallet.dto.user.UserDto
import org.springframework.security.core.Authentication
import reactor.core.publisher.Mono

/**
 * Сервис, отвечающий за пользователей
 */
interface UserService {
    fun registerUser(dto: RegisterUserRequestDto): Mono<UserDto>
    fun loginUser(dto: LoginUserRequestDto): Mono<AuthResponseDto>
    fun getProfile(authentication: Authentication): Mono<UserDto>
}