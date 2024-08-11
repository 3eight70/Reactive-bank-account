package com.learning.reactive.common.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.util.*

/**
 * Dto для депозита
 */
data class DepositDto (
    /**
     * Идентификатор счета, на который происходит пополнение
     */
    @NotNull(message = "Идентификатор аккаунта должен быть указан")
    @NotBlank(message = "Идентификатор аккаунта должен быть указан")
    val accountId: UUID,
    /**
     * Кол-во средств
     */
    @NotNull(message = "Сумма должна быть указана")
    @NotBlank(message = "Сумма должна быть указана")
    val amount: BigDecimal
)