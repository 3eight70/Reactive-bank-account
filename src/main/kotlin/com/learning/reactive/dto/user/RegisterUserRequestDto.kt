package com.learning.reactive.dto.user

/**
 * Запрос на регистрацию пользователя
 */
data class RegisterUserRequestDto (
    /**
     * Логин пользователя
     */
    val login: String,
    /**
     * Электронная почта пользователя
     */
    val email: String,
    /**
     * Пароль пользователя
     */
    val password: String
)