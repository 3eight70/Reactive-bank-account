package com.learning.reactive.service.impl

import com.learning.reactive.dto.account.BankAccountDto
import com.learning.reactive.dto.transaction.ShortTransactionDto
import com.learning.reactive.dto.transaction.TransactionDto
import com.learning.reactive.dto.transaction.TransactionEnum
import com.learning.reactive.dto.transaction.TransactionRequestDto
import com.learning.reactive.exception.account.AccountNotFoundException
import com.learning.reactive.exception.account.NotEnoughBalanceException
import com.learning.reactive.exception.common.BadRequestException
import com.learning.reactive.exception.common.ForbiddenException
import com.learning.reactive.exception.transaction.TransactionNotFoundException
import com.learning.reactive.models.Account
import com.learning.reactive.models.Transaction
import com.learning.reactive.repository.reactive.ReactiveAccountRepository
import com.learning.reactive.repository.reactive.ReactiveTransactionRepository
import com.learning.reactive.security.CustomPrincipal
import com.learning.reactive.service.AccountService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Service
class AccountServiceImpl(
    private val accountRepository: ReactiveAccountRepository,
    private val transactionRepository: ReactiveTransactionRepository
) : AccountService {
    override fun getAccounts(principal: CustomPrincipal): Flux<BankAccountDto> {
        return accountRepository.findAllByUserId(principal.getId())
            .map {
                BankAccountDto(
                    it.id,
                    it.balance
                )
            }
    }

    override fun transferMoney(
        principal: CustomPrincipal,
        transactionRequestDto: TransactionRequestDto,
        accountIdFrom: UUID,
        accountIdWhere: UUID
    ): Mono<TransactionDto> {
        if (accountIdWhere == accountIdFrom) {
            return Mono.error(BadRequestException("Нельзя перевести деньги на тот же самый счет"))
        }

        return Mono.zip(
            accountRepository.findById(accountIdFrom)
                .switchIfEmpty(Mono.error(AccountNotFoundException(accountIdFrom))),
            accountRepository.findById(accountIdWhere)
                .switchIfEmpty(Mono.error(AccountNotFoundException(accountIdWhere)))
        )
            .flatMap { tuple ->
                val accountFrom = tuple.t1
                val accountWhere = tuple.t2

                if (accountFrom.userId != principal.getId()) {
                    return@flatMap Mono.error<TransactionDto>(ForbiddenException())
                }

                val amountOfMoney = transactionRequestDto.amount
                if (accountFrom.balance.subtract(amountOfMoney) < BigDecimal.ZERO) {
                    return@flatMap Mono.error<TransactionDto>(NotEnoughBalanceException())
                }

                accountFrom.balance = accountFrom.balance.subtract(amountOfMoney)
                accountWhere.balance = accountWhere.balance.add(amountOfMoney)

                Mono.zip(
                    accountRepository.save(accountFrom),
                    accountRepository.save(accountWhere)
                )
                    .flatMap {
                        val transaction = Transaction(
                            UUID.randomUUID(),
                            accountIdFrom,
                            accountIdWhere,
                            amountOfMoney,
                            LocalDateTime.now()
                        )
                        transactionRepository.save(transaction)
                    }
                    .map { transaction ->
                        TransactionDto(
                            transaction.id,
                            transaction.accountIdFrom!!,
                            transaction.accountIdWhere,
                            transaction.amount,
                            transaction.timestamp,
                            TransactionEnum.TRANSFER
                        )
                    }
            }
            .subscribeOn(Schedulers.boundedElastic())
    }

    override fun createAccount(principal: CustomPrincipal): Mono<BankAccountDto> {
        return Mono.just(
            Account(
                id = UUID.randomUUID(),
                userId = principal.getId(),
                balance = BigDecimal.ZERO
            )
        ).flatMap { accountRepository.save(it) }
            .map {
                BankAccountDto(
                    it.id,
                    it.balance
                )
            }
    }

    override fun accountHistory(principal: CustomPrincipal, accountId: UUID): Flux<ShortTransactionDto> {
        return accountRepository.findById(accountId).switchIfEmpty(Mono.error(AccountNotFoundException(accountId)))
            .flatMapMany { account ->
                if (account.userId != principal.getId()) {
                    return@flatMapMany Flux.error<ShortTransactionDto>(ForbiddenException())
                }

                transactionRepository.findAllByAccountId(accountId)
                    .map { transaction ->
                        ShortTransactionDto(
                            id = transaction.id,
                            amount = transaction.amount,
                            timestamp = transaction.timestamp,
                            status = determineTransactionStatus(transaction, accountId)
                        )
                    }
            }
    }

    override fun getTransactionInfo(principal: CustomPrincipal, accountId: UUID, transactionId: UUID): Mono<TransactionDto> {
        return accountRepository.findById(accountId)
            .switchIfEmpty(Mono.error(AccountNotFoundException(accountId)))
            .flatMap { account ->
                if (account.userId != principal.getId()) {
                    return@flatMap Mono.error<TransactionDto>(ForbiddenException())
                }

                transactionRepository.findById(transactionId)
                    .switchIfEmpty(Mono.error(TransactionNotFoundException(transactionId)))
                    .flatMap { trans ->
                        if (trans.accountIdFrom != accountId && trans.accountIdWhere != accountId){
                            return@flatMap Mono.error<TransactionDto>(TransactionNotFoundException(transactionId))
                        }

                        Mono.just(
                            TransactionDto(
                                id = trans.id,
                                accountIdWhere = trans.accountIdWhere,
                                accountIdFrom = trans.accountIdFrom,
                                amount = trans.amount,
                                timestamp = trans.timestamp,
                                status = determineTransactionStatus(trans, accountId)
                            )
                        )
                    }
            }
    }

    private fun determineTransactionStatus(transaction: Transaction, accountId: UUID): TransactionEnum {
        return if (transaction.accountIdFrom == null) TransactionEnum.DEPOSIT
        else if (transaction.accountIdWhere == accountId) TransactionEnum.REFILL
        else TransactionEnum.TRANSFER
    }
}