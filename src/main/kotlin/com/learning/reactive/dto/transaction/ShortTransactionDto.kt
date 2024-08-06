package com.learning.reactive.dto.transaction

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

/**
 * Dto для отображения истории транзакций
 */
data class ShortTransactionDto (
    /**
     * Идентификатор транзакции
     */
    val id: UUID,
    /**
     * Сумма средств
     */
    val amount: BigDecimal,
    /**
     * Время совершения транзакции
     */
    val timestamp: LocalDateTime,
    /**
     * Статус транзакции, отображающий был совершен перевод или же пополнение средств на счету
     */
    val status: TransactionEnum
)