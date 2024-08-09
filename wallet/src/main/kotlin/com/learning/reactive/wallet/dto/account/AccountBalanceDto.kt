package com.learning.reactive.wallet.dto.account

import java.math.BigDecimal
import java.util.*

data class AccountBalanceDto (
    val userId: UUID = UUID.randomUUID(),
    val accountId: UUID = UUID.randomUUID(),
    val balance: BigDecimal = BigDecimal.ZERO
)