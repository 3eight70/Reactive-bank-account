package com.learning.reactive.dto.user

/**
 * Запрос на авторизацию
 */
data class LoginUserRequestDto (
    /**
     * Логин пользователя
     */
    val login: String,
    /**
     * Пароль пользователя
     */
    val password: String
)