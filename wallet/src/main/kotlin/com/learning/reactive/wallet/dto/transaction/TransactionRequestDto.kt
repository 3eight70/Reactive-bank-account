package com.learning.reactive.wallet.dto.transaction

import java.math.BigDecimal

/**
 * dto для отправления запроса на перевод средств
 */
data class TransactionRequestDto (
    /**
     * Кол-во средств
     */
    val amount: BigDecimal
)