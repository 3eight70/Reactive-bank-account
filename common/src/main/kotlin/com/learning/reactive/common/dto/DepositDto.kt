package com.learning.reactive.common.dto

import java.math.BigDecimal
import java.util.*

/**
 * Dto для депозита
 */
data class DepositDto (
    /**
     * Идентификатор счета, на который происходит пополнение
     */
    val accountId: UUID,
    /**
     * Кол-во средств
     */
    val amount: BigDecimal
)