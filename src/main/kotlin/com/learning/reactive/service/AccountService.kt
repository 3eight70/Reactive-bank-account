package com.learning.reactive.service

import com.learning.reactive.dto.account.BankAccountDto
import com.learning.reactive.dto.transaction.ShortTransactionDto
import com.learning.reactive.dto.transaction.TransactionDto
import com.learning.reactive.dto.transaction.TransactionRequestDto
import com.learning.reactive.security.CustomPrincipal
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface AccountService {
    fun getAccounts(principal: CustomPrincipal): Flux<BankAccountDto>
    fun transferMoney(
        principal: CustomPrincipal, transactionRequestDto: TransactionRequestDto,
        accountIdFrom: UUID, accountIdWhere: UUID
    ): Mono<TransactionDto>
    fun createAccount(principal: CustomPrincipal): Mono<BankAccountDto>
    fun accountHistory(principal: CustomPrincipal, accountId: UUID): Flux<ShortTransactionDto>
    fun getTransactionInfo(principal: CustomPrincipal, accountId: UUID, transactionId: UUID): Mono<TransactionDto>
}