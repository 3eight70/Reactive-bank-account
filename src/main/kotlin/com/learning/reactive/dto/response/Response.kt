package com.learning.reactive.dto.response

import java.time.LocalDateTime

/**
 * Dto для ответов сервера
 */
data class Response (
    /**
     * Статус ответа
     */
    val status: Int,
    /**
     * Время ответа
     */
    val timestamp: LocalDateTime? = LocalDateTime.now(),
    /**
     * Сообщение ответа
     */
    val message: String?,
    /**
     * Список ошибок
     */
    val errors: List<CustomFieldError>? = null
)