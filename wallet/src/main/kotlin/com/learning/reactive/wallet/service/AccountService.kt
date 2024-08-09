package com.learning.reactive.wallet.service

import com.learning.reactive.wallet.dto.account.BankAccountDto
import com.learning.reactive.wallet.dto.deposit.DepositDto
import com.learning.reactive.wallet.dto.transaction.ShortTransactionDto
import com.learning.reactive.wallet.dto.transaction.TransactionDto
import com.learning.reactive.wallet.dto.transaction.TransactionRequestDto
import com.learning.reactive.wallet.security.CustomPrincipal
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.util.*

/**
 * Сервис, отвечающий за счета
 */
interface AccountService {
    fun getAccounts(principal: CustomPrincipal): Flux<BankAccountDto>
    fun transferMoney(
        principal: CustomPrincipal, transactionRequestDto: TransactionRequestDto,
        accountIdFrom: UUID, accountIdWhere: UUID
    ): Mono<TransactionDto>
    fun createAccount(principal: CustomPrincipal): Mono<BankAccountDto>
    fun accountHistory(principal: CustomPrincipal, accountId: UUID): Flux<ShortTransactionDto>
    fun getTransactionInfo(principal: CustomPrincipal, accountId: UUID, transactionId: UUID): Mono<TransactionDto>
    fun checkAccountBalance(principal: CustomPrincipal, accountId: UUID): Mono<BigDecimal>
    fun processDeposit(deposit: DepositDto): Mono<Void>
}