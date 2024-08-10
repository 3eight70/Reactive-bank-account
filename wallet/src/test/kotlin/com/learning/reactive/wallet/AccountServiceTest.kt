package com.learning.reactive.wallet

import com.learning.reactive.wallet.dto.account.AccountBalanceDto
import com.learning.reactive.wallet.dto.account.BankAccountDto
import com.learning.reactive.wallet.dto.transaction.TransactionRequestDto
import com.learning.reactive.wallet.models.Account
import com.learning.reactive.wallet.models.Transaction
import com.learning.reactive.wallet.repository.reactive.ReactiveAccountRepository
import com.learning.reactive.wallet.repository.reactive.ReactiveTransactionRepository
import com.learning.reactive.wallet.security.CustomPrincipal
import com.learning.reactive.wallet.service.impl.AccountServiceImpl
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.testcontainers.junit.jupiter.Testcontainers
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.math.BigDecimal
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
@SpringBootTest(classes = [TestcontainersConfiguration::class])
class AccountServiceTest {
    private val accountRepository: ReactiveAccountRepository = mock()
    private val transactionRepository: ReactiveTransactionRepository = mock()
    private val redisTemplate: ReactiveRedisTemplate<String, AccountBalanceDto> = mock()

    private val accountService = AccountServiceImpl(
        accountRepository = accountRepository,
        retries = 3,
        timeout = 5,
        redisTemplate = redisTemplate,
        transactionRepository = transactionRepository
    )

    @Test
    fun `should get all accounts for the user`() {
        val userId = UUID.randomUUID()
        val principal = CustomPrincipal(userId, "test", "test")
        val account1 = Account(UUID.randomUUID(), userId, BigDecimal.ZERO)
        val account2 = Account(UUID.randomUUID(), userId, BigDecimal.TEN)
        val accountDto1 = BankAccountDto(account1.id)
        val accountDto2 = BankAccountDto(account2.id)

        whenever(accountRepository.findAllByUserId(userId)).thenReturn(Flux.just(account1, account2))

        val result = accountService.getAccounts(principal)

        StepVerifier.create(result)
            .expectNext(accountDto1)
            .expectNext(accountDto2)
            .verifyComplete()
    }

    @Test
    fun `should create a new account`() {
        val userId = UUID.randomUUID()
        val account = Account(UUID.randomUUID(), userId, BigDecimal.ZERO)
        val accountDto = BankAccountDto(account.id)

        whenever(accountRepository.save(any<Account>())).thenReturn(Mono.just(account))

        val result = accountService.createAccount(CustomPrincipal(userId, "test", "test"))

        StepVerifier.create(result)
            .expectNext(accountDto)
            .verifyComplete()
    }

    @Test
    fun `should transfer money between accounts`() {
        val userId = UUID.randomUUID()
        val accountIdFrom = UUID.randomUUID()
        val accountIdWhere = UUID.randomUUID()
        val amount = BigDecimal.TEN

        val accountFrom = Account(accountIdFrom, userId, BigDecimal.valueOf(100))
        val accountWhere = Account(accountIdWhere, userId, BigDecimal.ZERO)
        val transactionRequest = TransactionRequestDto(amount)

        whenever(accountRepository.findById(accountIdFrom)).thenReturn(Mono.just(accountFrom))
        whenever(accountRepository.findById(accountIdWhere)).thenReturn(Mono.just(accountWhere))
        whenever(accountRepository.save(any<Account>()))
            .thenReturn(
                Mono.just(accountFrom.copy(balance = accountFrom.balance.subtract(amount))),
                Mono.just(accountWhere.copy(balance = accountWhere.balance.add(amount)))
            )

        whenever(transactionRepository.save(any<Transaction>())).thenReturn(
            Mono.just(
                Transaction(
                    UUID.randomUUID(),
                    accountIdFrom,
                    accountIdWhere,
                    amount
                )
            )
        )

        val result = accountService.transferMoney(
            CustomPrincipal(userId, "test", "test"),
            transactionRequest,
            accountIdFrom,
            accountIdWhere
        )

        StepVerifier.create(result)
            .expectNextMatches {
                it.amount == amount && it.accountIdWhere == accountIdWhere && it.accountIdFrom == accountIdFrom
            }
            .verifyComplete()
    }
}