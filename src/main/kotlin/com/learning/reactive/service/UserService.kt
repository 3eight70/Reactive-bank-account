package com.learning.reactive.service

import com.learning.reactive.dto.user.AuthResponseDto
import com.learning.reactive.dto.user.LoginUserRequestDto
import com.learning.reactive.dto.user.RegisterUserRequestDto
import com.learning.reactive.dto.user.UserDto
import org.springframework.security.core.Authentication
import reactor.core.publisher.Mono

interface UserService {
    fun registerUser(dto: RegisterUserRequestDto): Mono<UserDto>
    fun loginUser(dto: LoginUserRequestDto): Mono<AuthResponseDto>
    fun getProfile(authentication: Authentication): Mono<UserDto>
}