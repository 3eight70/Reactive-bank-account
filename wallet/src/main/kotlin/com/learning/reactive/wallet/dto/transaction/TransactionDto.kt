package com.learning.reactive.wallet.dto.transaction

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

/**
 * Dto для транзакций
 */
data class TransactionDto(
    /**
     * Идентификатор транзакции
     */
    val id: UUID,
    /**
     * Идентификатор счета, откуда переводятся деньги
     */
    val accountIdFrom: UUID?,
    /**
     * Идентификатор счета, куда переводятся деньги
     */
    val accountIdWhere: UUID,
    /**
     * Количество переводимых денег
     */
    val amount: BigDecimal,
    /**
     * Время совершения транзакции
     */
    val timestamp: LocalDateTime,
    /**
     * Статус транзакции
     *
     * Виды статусов: REFILL, DEPOSIT, TRANSFER. Подробнее см. {@link TransactionEnum}
     */
    val status: TransactionEnum
)