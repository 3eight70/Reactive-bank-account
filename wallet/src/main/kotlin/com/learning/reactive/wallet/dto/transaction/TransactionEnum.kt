package com.learning.reactive.wallet.dto.transaction

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