package com.learning.reactive.wallet.dto.account

import java.math.BigDecimal
import java.util.*

/**
 * Банковский счет
 */
data class BankAccountDto(
    /**
     * Идентификатор
     */
    val id: UUID,
    /**
     * Сумма денег на счету
     */
    val amountOfMoney: BigDecimal
)