package com.learning.reactive.wallet.service.impl

import com.learning.reactive.common.dto.DepositDto
import com.learning.reactive.wallet.dto.account.AccountBalanceDto
import com.learning.reactive.wallet.dto.account.BankAccountDto
import com.learning.reactive.wallet.dto.transaction.ShortTransactionDto
import com.learning.reactive.wallet.dto.transaction.TransactionDto
import com.learning.reactive.wallet.dto.transaction.TransactionEnum
import com.learning.reactive.wallet.dto.transaction.TransactionRequestDto
import com.learning.reactive.wallet.exception.account.AccountNotFoundException
import com.learning.reactive.wallet.exception.account.NotEnoughBalanceException
import com.learning.reactive.wallet.exception.common.BadRequestException
import com.learning.reactive.wallet.exception.common.CustomTimeoutException
import com.learning.reactive.wallet.exception.common.ForbiddenException
import com.learning.reactive.wallet.exception.transaction.TransactionNotFoundException
import com.learning.reactive.wallet.models.Account
import com.learning.reactive.wallet.models.Transaction
import com.learning.reactive.wallet.repository.reactive.ReactiveAccountRepository
import com.learning.reactive.wallet.repository.reactive.ReactiveTransactionRepository
import com.learning.reactive.wallet.security.CustomPrincipal
import com.learning.reactive.wallet.service.AccountService
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.RedisConnectionFailureException
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeoutException

@Service
class AccountServiceImpl(
    @Value("\${application.retry}")
    private val retries: Long,
    @Value("\${application.timeout}")
    private val timeout: Long,
    private val accountRepository: ReactiveAccountRepository,
    private val transactionRepository: ReactiveTransactionRepository,
    private val redisTemplate: ReactiveRedisTemplate<String, AccountBalanceDto>
) : AccountService {
    override fun getAccounts(principal: CustomPrincipal): Flux<BankAccountDto> {
        return accountRepository.findAllByUserId(principal.getId())
            .map {
                BankAccountDto(
                    it.id
                )
            }
            .retry(retries)
            .timeout(Duration.ofSeconds(timeout))
            .onErrorMap { throwable ->
                when (throwable) {
                    is TimeoutException -> CustomTimeoutException()
                    else -> throwable
                }
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
                    .flatMap { transaction ->
                        val updateRedis = Mono.zip(
                            redisTemplate.opsForValue().set(
                                accountIdFrom.toString(),
                                AccountBalanceDto(principal.getId(), accountIdFrom, accountFrom.balance)
                            )
                                .onErrorResume { Mono.empty() },
                            redisTemplate.opsForValue().set(
                                accountIdWhere.toString(),
                                AccountBalanceDto(principal.getId(), accountIdWhere, accountWhere.balance)
                            )
                                .onErrorResume { Mono.empty() }
                        ).then()

                        updateRedis.thenReturn(
                            TransactionDto(
                                transaction.id,
                                transaction.accountIdFrom!!,
                                transaction.accountIdWhere,
                                transaction.amount,
                                transaction.timestamp,
                                TransactionEnum.TRANSFER
                            )
                        )
                    }
            }
            .retry(retries)
            .timeout(Duration.ofSeconds(timeout))
            .onErrorMap { throwable ->
                when (throwable) {
                    is TimeoutException -> CustomTimeoutException()
                    else -> throwable
                }
            }
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
                    it.id
                )
            }
            .retry(retries)
            .timeout(Duration.ofSeconds(timeout))
            .onErrorMap { throwable ->
                when (throwable) {
                    is TimeoutException -> CustomTimeoutException()
                    else -> throwable
                }
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
            .retry(retries)
            .timeout(Duration.ofSeconds(timeout))
            .onErrorMap { throwable ->
                when (throwable) {
                    is TimeoutException -> CustomTimeoutException()
                    else -> throwable
                }
            }
    }

    override fun getTransactionInfo(
        principal: CustomPrincipal,
        accountId: UUID,
        transactionId: UUID
    ): Mono<TransactionDto> {
        return accountRepository.findById(accountId)
            .switchIfEmpty(Mono.error(AccountNotFoundException(accountId)))
            .flatMap { account ->
                if (account.userId != principal.getId()) {
                    return@flatMap Mono.error<TransactionDto>(ForbiddenException())
                }

                transactionRepository.findById(transactionId)
                    .switchIfEmpty(Mono.error(TransactionNotFoundException(transactionId)))
                    .flatMap { trans ->
                        if (trans.accountIdFrom != accountId && trans.accountIdWhere != accountId) {
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
            .retry(retries)
            .timeout(Duration.ofSeconds(timeout))
            .onErrorMap { throwable ->
                when (throwable) {
                    is TimeoutException -> CustomTimeoutException()
                    else -> throwable
                }
            }
    }

    override fun checkAccountBalance(principal: CustomPrincipal, accountId: UUID): Mono<BigDecimal> {
        val stringId = accountId.toString()
        val userId = principal.getId()

        return redisTemplate.hasKey(stringId)
            .flatMap { exists ->
                if (exists) {
                    redisTemplate.opsForValue().get(stringId)
                        .flatMap { accountBalanceDto ->
                            if (accountBalanceDto.userId != userId) {
                                Mono.error(ForbiddenException())
                            } else {
                                Mono.just(accountBalanceDto.balance)
                            }
                        }
                } else {
                    accountRepository.findById(accountId)
                        .switchIfEmpty(Mono.error(AccountNotFoundException(accountId)))
                        .flatMap { account ->
                            if (account.userId != principal.getId()) {
                                return@flatMap Mono.error(ForbiddenException())
                            }

                            redisTemplate.opsForValue().set(
                                stringId,
                                AccountBalanceDto(userId, accountId, account.balance)
                            ).thenReturn(account.balance)
                        }
                }
            }
            .retry(retries)
            .timeout(Duration.ofSeconds(timeout))
            .onErrorResume { throwable ->
                when (throwable) {
                    is RedisConnectionFailureException -> {
                        accountRepository.findById(accountId)
                            .switchIfEmpty(Mono.error(AccountNotFoundException(accountId)))
                            .flatMap { account ->
                                if (account.userId != principal.getId()) {
                                    return@flatMap Mono.error(ForbiddenException())
                                }

                                Mono.just(account.balance)
                            }
                    }

                    is TimeoutException -> Mono.error(CustomTimeoutException())
                    else -> Mono.error(throwable)
                }
            }
    }

    override fun processDeposit(deposit: DepositDto): Mono<Void> {
        val accountId = deposit.accountId

        return accountRepository.findById(accountId)
            .switchIfEmpty(Mono.error(AccountNotFoundException(accountId)))
            .flatMap { account ->
                account.balance = account.balance.add(deposit.amount)
                accountRepository.save(account)
                    .flatMap {
                        val transaction = Transaction(
                            UUID.randomUUID(),
                            null,
                            accountId,
                            deposit.amount,
                            LocalDateTime.now()
                        )

                        transactionRepository.save(transaction)
                            .then(
                                Mono.defer {
                                    redisTemplate.opsForValue().set(
                                        accountId.toString(), AccountBalanceDto(
                                            account.userId,
                                            accountId,
                                            account.balance
                                        )
                                    )
                                        .then()
                                        .onErrorResume { e ->
                                            Mono.empty()
                                        }
                                }
                            )
                    }
            }
            .retry(retries)
            .timeout(Duration.ofSeconds(timeout))
            .onErrorMap { throwable ->
                when (throwable) {
                    is TimeoutException -> CustomTimeoutException()
                    else -> throwable
                }
            }
    }

    private fun determineTransactionStatus(transaction: Transaction, accountId: UUID): TransactionEnum {
        return if (transaction.accountIdFrom == null) TransactionEnum.DEPOSIT
        else if (transaction.accountIdWhere == accountId) TransactionEnum.REFILL
        else TransactionEnum.TRANSFER
    }
}