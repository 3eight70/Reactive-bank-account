package com.learning.reactive.dto.user

import jakarta.validation.constraints.*

/**
 * Запрос на регистрацию пользователя
 */
data class RegisterUserRequestDto(
    /**
     * Логин пользователя
     */
    @field:NotNull(message = "Логин должен быть указан")
    @field:Pattern(
        regexp = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9_-]+|([a-zA-Z0-9]+)",
        message = "Логин должен состоять из букв и цифр"
    )
    @field:NotBlank(message = "Логин должен быть указан")
    val login: String?,
    /**
     * Электронная почта пользователя
     */
    @field:Size(min = 1, message = "Минимальная длина не менее 1 символа")
    @field:Email(message = "Неверный адрес электронной почты")
    @field:NotNull(message = "Адрес почты должен быть указан")
    @field:NotBlank(message = "Адрес почты должен быть указан")
    val email: String?,
    /**
     * Пароль пользователя
     */
    @field:Pattern(regexp = "^(?=.*\\d).{4,}$", message = "Пароль должен содержать не менее 4 символов и 1 цифры")
    @field:NotNull(message = "Пароль должен быть указан")
    @field:NotBlank(message = "Пароль должен быть указан")
    val password: String?
)