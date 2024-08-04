package com.learning.reactive.controller

import com.learning.reactive.dto.user.AuthResponseDto
import com.learning.reactive.dto.user.LoginUserRequestDto
import com.learning.reactive.dto.user.RegisterUserRequestDto
import com.learning.reactive.dto.user.UserDto
import com.learning.reactive.service.UserService
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService
) {
    @PostMapping("/register")
    fun registerUser(@RequestBody registerUserRequestDto: RegisterUserRequestDto) : Mono<UserDto>{
        return userService.registerUser(registerUserRequestDto)
    }

    @PostMapping("/login")
    fun loginUser(@RequestBody loginUserRequestDto: LoginUserRequestDto) : Mono<AuthResponseDto>{
        return userService.loginUser(loginUserRequestDto)
    }

    @GetMapping
    fun getProfile(authentication: Authentication) : Mono<UserDto> {
        return userService.getProfile(authentication)
    }
}