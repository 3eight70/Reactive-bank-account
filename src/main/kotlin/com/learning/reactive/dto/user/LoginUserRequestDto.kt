package com.learning.reactive.dto.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

/**
 * Запрос на авторизацию
 */
data class LoginUserRequestDto (
    /**
     * Логин пользователя
     */
    @field:NotNull(message = "Логин должен быть указан")
    @field:NotBlank(message = "Логин должен быть указан")
    val login: String?,
    /**
     * Пароль пользователя
     */
    @field:NotNull(message = "Пароль должен быть указан")
    @field:NotBlank(message = "Пароль должен быть указан")
    val password: String?
)