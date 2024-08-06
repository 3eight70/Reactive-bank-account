package com.learning.reactive.dto.transaction

/**
 * Enum для отображения статуса транзакции
 */
enum class TransactionEnum {
    /**
     * Пополнение
      */
    REFILL,

    /**
     * Перевод
     */
    TRANSFER,

    /**
     * Пополнение денег из банкомата
     */
    DEPOSIT
}