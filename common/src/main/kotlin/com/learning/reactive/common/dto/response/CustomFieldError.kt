package com.learning.reactive.common.dto.response

/**
 * Dto для отображения ошибок валидации
 */
data class CustomFieldError(
    /**
     * Поле, в котором допущена ошибка
     */
    val field: String,
    /**
     * Сообщение об ошибке
     */
    val message: String?
)