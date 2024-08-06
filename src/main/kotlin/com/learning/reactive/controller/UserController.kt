package com.learning.reactive.controller

import com.learning.reactive.dto.user.AuthResponseDto
import com.learning.reactive.dto.user.LoginUserRequestDto
import com.learning.reactive.dto.user.RegisterUserRequestDto
import com.learning.reactive.dto.user.UserDto
import com.learning.reactive.service.UserService
import jakarta.validation.Valid
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

/**
 * Контроллер для работы с пользователями
 */
@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService
) {
    /**
     * Метод, отвечающий за регистрацию пользователя
     */
    @PostMapping("/register")
    fun registerUser(@RequestBody @Valid registerUserRequestDto: RegisterUserRequestDto): Mono<UserDto> {
        return userService.registerUser(registerUserRequestDto)
    }

    /**
     * Метод, отвечающий за авторизацию пользователя
     */
    @PostMapping("/login")
    fun loginUser(@RequestBody @Valid loginUserRequestDto: LoginUserRequestDto): Mono<AuthResponseDto> {
        return userService.loginUser(loginUserRequestDto)
    }

    /**
     * Метод, отвечающий за получение профиля пользователя
     */
    @GetMapping
    fun getProfile(authentication: Authentication): Mono<UserDto> {
        return userService.getProfile(authentication)
    }
}