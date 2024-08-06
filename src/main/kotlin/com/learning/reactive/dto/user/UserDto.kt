package com.learning.reactive.dto.user

import java.util.*

/**
 * Dto для пользователя
 */
data class UserDto(
    /**
     * Идентификатор
     * Пример: 5bb2ade3-1192-4f37-8faa-c87bbd993fcd
     */
    val id: UUID,
    /**
     * Логин пользователя
     * Пример: user
     */
    val login: String,
    /**
     * Электронная почта пользователя
     * Пример: user@gmail.com
     */
    val email: String,
)